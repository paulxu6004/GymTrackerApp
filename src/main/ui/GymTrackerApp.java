package ui;

import model.GymTracker;
import model.WorkoutSession;
import model.Excercise;
import persistence.JsonReader;
import persistence.JsonWriter;


import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.io.IOException;
import java.io.FileNotFoundException;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// Gym Tacker application
// For the added saving and loading functionality, I referenced JsonSerializar\tionDemo
@ExcludeFromJacocoGeneratedReport
public class GymTrackerApp {
    private static final String JSON_STORE = "./data/gymtracker.json";
    private GymTracker gymTracker;
    private Scanner input;
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;



    // EFFECTS: runs the Gym Tacker application
    public GymTrackerApp() {
        gymTracker = new GymTracker();
        input = new Scanner(System.in);
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        runGymTracker();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runGymTracker() {
        boolean keepGoing = true;
        String command = null;

        System.out.println("Welcome to Gym Tracker!");
   
        while (keepGoing) {
            displayMainMenu();
            command = input.nextLine().trim();

            if (command.equals("7")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
            
        }
        System.out.println("\nThank you for using Gym Tracker!");
    }

    // EFFECTS: displays the main menu options
    private void displayMainMenu() {
        System.out.println("\nMain Menu");
        System.out.println("1 -> Add Workout Session");
        System.out.println("2 -> View Exercise Progress");
        System.out.println("3 -> View Least Progress Areas");
        System.out.println("4 -> View Workout History");
        System.out.println("5 -> Save Gym Tracker to File");  
        System.out.println("6 -> Load Gym Tracker from File");
        System.out.println("7 -> Exit");      
        System.out.print("Choose an option: ");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        if (command.equals("1")) {
            addWorkoutSession();
        } else if (command.equals("2")) {
            viewExerciseProgress();
        } else if (command.equals("3")) {
            viewLeastProgress();
        } else if (command.equals("4")) {
            viewWorkoutHistory();
        } else if (command.equals("5")) {
            saveGymTracker();
        } else if (command.equals("6")) { 
            loadGymTracker();
        } else {
            System.out.println("Selection not valid. Please try again.");
        }       
    }

    // MODIFIES: this
    // EFFECTS: adds a workout session
    @SuppressWarnings("methodlength")
    private void addWorkoutSession() {
        System.out.print("Enter muscle group focus (Push, Pull, Legs): ");
        String focus = input.nextLine().trim();  

        System.out.print("Enter date (YYYY-MM-DD format, e.g., 2024-01-15): ");
        String dateInput = input.nextLine().trim();
        LocalDate date = LocalDate.parse(dateInput); 
        
        WorkoutSession session = new WorkoutSession(focus, date);
        
        boolean addingExercises = true;
        while (addingExercises) {
            System.out.println("\nAdd Exercise to Session");

            System.out.print("Enter exercise name: ");
            String exerciseName = input.nextLine().trim();
            
            System.out.print("Enter number of sets: ");
            int sets = Integer.parseInt(input.nextLine().trim());
            
            System.out.print("Enter number of reps: ");
            int reps = Integer.parseInt(input.nextLine().trim());
            
            System.out.print("Enter weight (lbs): ");
            double weight = Double.parseDouble(input.nextLine().trim());
            
            System.out.print("Enter actual One Rep Max (or 0 to calculate estimated orm): ");
            double orm = Double.parseDouble(input.nextLine().trim());
            
            Excercise exercise = new Excercise(exerciseName, sets, reps, weight, orm);
            session.addExcercise(exercise);
            
            System.out.println(exerciseName + " added successfully! Add another exercise? (y/n): ");
            String response = input.nextLine().trim().toLowerCase();
            addingExercises = response.equals("y") || response.equals("yes");
        }
        
        gymTracker.addWorkoutSession(session);
        System.out.println("Workout session added successfully!");
    }

    // EFFECTS: Shows progress for a specific exercise
    private void viewExerciseProgress() {
        System.out.print("Enter exercise name to track progress: ");
        String exerciseName = input.nextLine().trim();

        Map<String, Double> progress = gymTracker.getExcerciseProgress(exerciseName);

        if (progress.isEmpty() || progress.get("progress") == 0.0) {
            System.out.println("No progress data available for exercise: " + exerciseName);
            System.out.println("At least 2 sessions with this exercise is needed to get progress");
        } else {
            System.out.println("\nProgress for " + exerciseName + ":");
            System.out.printf("Overall Progress: %.2f%%\n", progress.get("progress"));
            System.out.println("(Improvement from oldest to newest performance)");
        }     
    }

    // EFFECTS: shows least progress focus and excercises
    private void viewLeastProgress() {
        String leastExercise = gymTracker.getExerciseWithLeastProgress();
        String leastFocus = gymTracker.getFocusWithLeastProgress();
        
        if (leastExercise.equals("No exercises tracked") || leastFocus.equals("No focuses tracked")) {
            System.out.println("Not enough data to determine least progress areas.");
            System.out.println("You need at least 2 sessions with different exercises.");
        } else {
            System.out.println("Exercise needing most attention: " + leastExercise);
            System.out.println("Muscle group needing most attention: " + leastFocus);
            
            System.out.println("\n💡 Tip: Consider increasing volume or intensity for these areas,");
            System.out.println("       or ensuring proper form and recovery.");
        }      
    }

    // MODIFIES: this
    // EFFECTS: shows least progress focus and excercises
    private void viewWorkoutHistory() {
        System.out.println("\nWorkout History");  
        Map<String, List<WorkoutSession>> history = gymTracker.getWorkoutHistory();
        
        if (history.isEmpty()) {
            System.out.println("No workout history available.");
        } else {

            for (String focus : history.keySet()) {
                List<WorkoutSession> sessions = history.get(focus);
                
                System.out.println("\n" + focus + " Workouts");
                System.out.println("Total sessions: " + sessions.size());

                for (WorkoutSession session : sessions) {
                    System.out.println("  Date: " + session.getDate() 
                            + " Exercises: " + session.getExcercises().size());   
                            
                    for (Excercise exercise : session.getExcercises()) {
                        double roundedWeight = Math.round(exercise.getWeight() * 100.0) / 100.0;
                        double roundedOrm = Math.round(exercise.getOrm() * 100.0) / 100.0;

                        System.out.println(exercise.getName() + ": " + exercise.getSets() + " sets x " 
                                            + exercise.getReps() + " reps @ " + roundedWeight 
                                            + "lbs (ORM: " + roundedOrm + ")");
                    }
                }
            }
        } 
    }

    // EFFECTS: saves the gym tracker to file
    private void saveGymTracker() {
        try {
            jsonWriter.open();
            jsonWriter.write(gymTracker);
            jsonWriter.close();
            System.out.println("Saved gym tracker to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads gym tracker from file
    private void loadGymTracker() {
        try {
            gymTracker = jsonReader.read();
            System.out.println("Loaded gym tracker from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }
}