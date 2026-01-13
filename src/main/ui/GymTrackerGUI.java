package ui;

import model.*;
import model.Event;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.net.URL;


import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Graphical User Interface for Gym Tracker application
@ExcludeFromJacocoGeneratedReport
public class GymTrackerGUI extends JFrame {
    private static final String JSON_STORE = "./data/gymtracker.json";
    private GymTracker gymTracker;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    private DefaultListModel<String> sessionListModel;
    private JList<String> sessionList;
    private JTextArea detailsArea;
    private ProgressChartPanel chartPanel;
    private JComboBox<String> exerciseComboBox;

    // EFFECTS: runs the the application
    public GymTrackerGUI() {
        super("Gym Tracker");
        initializeModel();
        initializeGUI();
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initializes model and persistence objects
    private void initializeModel() {
        gymTracker = new GymTracker();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
    }

    // MODIFIES: this
    // EFFECTS: initializes GUI components
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1000, 600));
        setLayout(new BorderLayout());

        setupWindowProperties(); 

        createMenuBar();
        createMainPanel();

        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }

    // this doesnt work because Mac
    // MODIFIES: this
    // EFFECTS: sets up basic window properties and styling
    private void setupWindowProperties() {
        URL test = getClass().getResource("/ui/icon.png");
        System.out.println("DEBUG: icon resource = " + test);   
        Image appIcon = createAppIcon();
        if (appIcon != null) {
            setIconImage(appIcon);
        }
    }

    // this doesnt work because on Mac apparently 
    // EFFECTS: creates and returns the application icon image from data folder
    private Image createAppIcon() {
        URL url = getClass().getResource("/ui/icon.png");
        if (url == null) {
            System.out.println("Icon not found");
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    // MODIFIES: this
    // EFFECTS: creates menu bar with save/load options
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        saveItem.addActionListener(e -> saveGymTracker());
        loadItem.addActionListener(e -> loadGymTracker());
        exitItem.addActionListener(e -> handleExit());
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu workoutMenu = new JMenu("Workout");
        JMenuItem addSessionItem = new JMenuItem("Add Workout Session");
        JMenuItem viewProgressItem = new JMenuItem("View Exercise Progress");
        
        addSessionItem.addActionListener(e -> addWorkoutSession());
        viewProgressItem.addActionListener(e -> viewExerciseProgress());
        
        workoutMenu.add(addSessionItem);
        workoutMenu.add(viewProgressItem);
        
        menuBar.add(fileMenu);
        menuBar.add(workoutMenu);
        
        setJMenuBar(menuBar);
    }

    // MODIFIES: this
    // EFFECTS: creates main panel with session list and progress chart
    private void createMainPanel() {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerLocation(300);
        
        JSplitPane sessionSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        sessionSplitPane.setDividerLocation(400);
        
        sessionSplitPane.setLeftComponent(createSessionListPanel());
        sessionSplitPane.setRightComponent(createDetailsPanel());
        
        JPanel progressPanel = createProgressPanel();
        
        mainSplitPane.setTopComponent(sessionSplitPane);
        mainSplitPane.setBottomComponent(progressPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
    }

    // EFFECTS: creates session list panel
    private JPanel createSessionListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Workout Sessions"));

        sessionListModel = new DefaultListModel<>();
        updateSessionList();
        
        sessionList = new JList<>(sessionListModel);
        sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionList.addListSelectionListener(e -> displaySessionDetails());
        
        JScrollPane scrollPane = new JScrollPane(sessionList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // EFFECTS: creates session details panel
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Session Details"));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // EFFECTS: creates progress panel with chart
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Exercise Progress Chart"));

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Select Exercise:"));
        
        exerciseComboBox = new JComboBox<>();
        updateExerciseComboBox();
        exerciseComboBox.addActionListener(e -> updateProgressChart());
        
        controlPanel.add(exerciseComboBox);
        
        chartPanel = new ProgressChartPanel(gymTracker);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }

    // MODIFIES: this
    // EFFECTS: updates session list
    private void updateSessionList() {
        sessionListModel.clear();
        
        for (WorkoutSession session : gymTracker.getSessionsSortedByDate()) {
            String sessionInfo = session.getDate() + " - " + session.getFocus() 
                    + " (" + session.getExcerciseCount() + " exercises)";
            sessionListModel.addElement(sessionInfo);
        }
        
        if (sessionListModel.isEmpty()) {
            sessionListModel.addElement("No workout sessions available");
        }
    }

    // MODIFIES: this
    // EFFECTS: updates exercise combo box
    private void updateExerciseComboBox() {
        exerciseComboBox.removeAllItems();
        
        for (WorkoutSession session : gymTracker.getAllWorkoutSessions()) {
            for (Excercise exercise : session.getExcercises()) {
                String name = exercise.getName();
                boolean found = false;
                for (int i = 0; i < exerciseComboBox.getItemCount(); i++) {
                    if (exerciseComboBox.getItemAt(i).equals(name)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    exerciseComboBox.addItem(name);
                }
            }
        }
        
        if (exerciseComboBox.getItemCount() == 0) {
            exerciseComboBox.addItem("No exercises available");
        }
    }

    // MODIFIES: this
    // EFFECTS: displays details of selected session
    private void displaySessionDetails() {
        int selectedIndex = sessionList.getSelectedIndex();
        if (selectedIndex == -1 || sessionListModel.isEmpty()) {
            detailsArea.setText("Select a workout session to view details");
            return;
        }
        
        List<WorkoutSession> sessions = gymTracker.getSessionsSortedByDate();
        if (selectedIndex >= sessions.size()) {
            return;
        }
        
        WorkoutSession session = sessions.get(selectedIndex);
        StringBuilder details = new StringBuilder();
        
        details.append("Focus: ").append(session.getFocus()).append("\n");
        details.append("Date: ").append(session.getDate()).append("\n");
        details.append("Exercises: ").append(session.getExcerciseCount()).append("\n\n");
        
        for (Excercise exercise : session.getExcercises()) {
            details.append(exercise.getName()).append(":\n");
            details.append("  Sets: ").append(exercise.getSets()).append("\n");
            details.append("  Reps: ").append(exercise.getReps()).append("\n");
            details.append("  Weight: ").append(exercise.getWeight()).append(" lbs\n");
            details.append("  ORM: ").append(exercise.getOrm()).append(" lbs\n");
            details.append("  Volume: ").append(exercise.getVolume()).append("\n\n");
        }
        
        detailsArea.setText(details.toString());
    }

    // MODIFIES: this
    // EFFECTS: updates progress chart
    private void updateProgressChart() {
        if (chartPanel != null && exerciseComboBox.getSelectedItem() != null) {
            String selectedExercise = (String) exerciseComboBox.getSelectedItem();
            if (!selectedExercise.equals("No exercises available")) {
                chartPanel.setSelectedExercise(selectedExercise);
                chartPanel.repaint();
            }
        }
        updateProgressChart();
    }

    // MODIFIES: this
    // EFFECTS: adds a new workout session
    private void addWorkoutSession() {
        String focus = JOptionPane.showInputDialog(this, "Enter muscle group focus:");
        if (focus == null || focus.trim().isEmpty()) {
            return;
        }
        String dateInput = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):", 
                LocalDate.now().toString());
        if (dateInput == null) {
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateInput.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Using today's date.");
            date = LocalDate.now();
        }
        
        WorkoutSession session = new WorkoutSession(focus.trim(), date);
        addExercisesToSession(session);
        gymTracker.addWorkoutSession(session);
        
        updateSessionList();
        updateExerciseComboBox();
        
        JOptionPane.showMessageDialog(this, "Workout session added!");
    }

    // REQUIRES: session != null
    // MODIFIES: session
    // EFFECTS: adds exercises to the session
    // Uses suppress warnings because the entire method is for adding excercises to workout session
    @SuppressWarnings("methodlength")
    private void addExercisesToSession(WorkoutSession session) {
        boolean addingExercises = true;
        
        while (addingExercises) {
            String name = JOptionPane.showInputDialog(this, "Enter exercise name:");
            if (name == null || name.trim().isEmpty()) {
                break;
            }
            
            String setsStr = JOptionPane.showInputDialog(this, "Enter number of sets:");
            if (setsStr == null) {
                break;
            }
            int sets = Integer.parseInt(setsStr.trim());

            String repsStr = JOptionPane.showInputDialog(this, "Enter number of reps:");
            if (repsStr == null) {
                break;
            }
            int reps = Integer.parseInt(repsStr.trim());
            
            String weightStr = JOptionPane.showInputDialog(this, "Enter weight (lbs):");
            if (weightStr == null) {
                break;
            }
            double weight = Double.parseDouble(weightStr.trim());
            
            String ormStr = JOptionPane.showInputDialog(this, "Enter ORM (0 to estimate):", "0");
            if (ormStr == null) {
                break;
            }
            double orm = Double.parseDouble(ormStr.trim());
            
            Excercise exercise = new Excercise(name.trim(), sets, reps, weight, orm);
            session.addExcercise(exercise);
            
            int response = JOptionPane.showConfirmDialog(this, 
                    "Exercise added! Add another exercise?", "Continue", 
                    JOptionPane.YES_NO_OPTION);
            addingExercises = (response == JOptionPane.YES_OPTION);
        }
    }

    // EFFECTS: shows exercise progress
    private void viewExerciseProgress() {
        String exerciseName = JOptionPane.showInputDialog(this, "Enter exercise name:");
        if (exerciseName == null || exerciseName.trim().isEmpty()) {
            return;
        }
        
        Map<String, Double> progress = gymTracker.getExcerciseProgress(exerciseName.trim());
        
        if (progress.isEmpty() || progress.get("progress") == 0.0) {
            JOptionPane.showMessageDialog(this, 
                    "No progress data available for " + exerciseName 
                    + "\nNeed at least 2 sessions with this exercise.");
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Progress for " + exerciseName + ": "
                    + String.format("%.2f", progress.get("progress")) + "% improvement");
        }
    }

    // EFFECTS: saves data to file
    private void saveGymTracker() {
        try {
            jsonWriter.open();
            jsonWriter.write(gymTracker);
            jsonWriter.close();
            JOptionPane.showMessageDialog(this, "Data saved to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Unable to save to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads data from file
    private void loadGymTracker() {
        try {
            gymTracker = jsonReader.read();
            updateSessionList();
            updateExerciseComboBox();
            JOptionPane.showMessageDialog(this, "Data loaded from " + JSON_STORE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to load from file: " + JSON_STORE);
        }
    }

    // EFFECTS: handles application exit
    private void handleExit() {
        int response = JOptionPane.showConfirmDialog(this,
                "Save before exiting?", "Exit", 
                JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            saveGymTracker();
            printEventsOnExit();
            dispose();
        } else if (response == JOptionPane.NO_OPTION) {
            printEventsOnExit();
            dispose();
        }
    }

    // EFFECTS: prints all logged events to console when application closes
    private void printEventsOnExit() {
        System.out.println("\nEVENT LOG");
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.toString());
            System.out.println("---");
        }
        System.out.println("\nEND OF EVENT LOG");
    }
}