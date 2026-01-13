package ui;

import model.GymTracker;
import model.Excercise;
import model.WorkoutSession;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// A panel that displays line chart for exercise progress
@ExcludeFromJacocoGeneratedReport
public class ProgressChartPanel extends JPanel {
    private GymTracker gymTracker;
    private String selectedExercise;

    // EFFECTS: constructs a progress chart panel with given gym tracker
    public ProgressChartPanel(GymTracker gymTracker) {
        this.gymTracker = gymTracker;
        this.selectedExercise = null;
        setPreferredSize(new Dimension(600, 300));
        setBackground(Color.WHITE);
    }

    // MODIFIES: this
    // EFFECTS: sets the selected exercise for the chart display
    public void setSelectedExercise(String exercise) {
        this.selectedExercise = exercise;
    }

    // MODIFIES: this
    // EFFECTS: paints the progress chart component based on current state
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (selectedExercise == null || "No exercises available".equals(selectedExercise)) {
            drawMessage(g, "Select an exercise to view progress");
            return;
        }
        
        List<DataPoint> dataPoints = getDataPoints();
        if (dataPoints.isEmpty()) {
            drawMessage(g, "No data for: " + selectedExercise);
            return;
        }
        
        if (dataPoints.size() < 2) {
            drawMessage(g, "Need at least 2 data points for chart");
            return;
        }
        
        drawChart(g, dataPoints);
    }

    // EFFECTS: returns list of data points for selected exercise, sorted by date
    private List<DataPoint> getDataPoints() {
        List<DataPoint> points = new ArrayList<>();
        List<WorkoutSession> sessions = gymTracker.getAllWorkoutSessions();
        
        sessions.sort((s1, s2) -> s1.getDate().compareTo(s2.getDate()));
        
        for (WorkoutSession session : sessions) {
            for (Excercise exercise : session.getExcercises()) {
                if (exercise.getName().equals(selectedExercise)) {
                    points.add(new DataPoint(session.getDate(), exercise.getOrm()));
                }
            }
        }
        return points;
    }

    // REQUIRES: points.size() >= 2
    // MODIFIES: this
    // EFFECTS: draws the complete chart with axes, data points, and progress line
    // Suppresswarnings used because it draws out the entire chart, and only makes sense to be in one method
    @SuppressWarnings("methodlength")   
    private void drawChart(Graphics g, List<DataPoint> points) {
        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        double minWeight = points.get(0).weight;
        double maxWeight = points.get(0).weight;
        for (DataPoint point : points) {
            if (point.weight < minWeight) {
                minWeight = point.weight;
            } 
            if (point.weight > maxWeight) {
                maxWeight = point.weight;
            } 
        }
        
        minWeight = Math.max(0, minWeight - 10);
        maxWeight += 10;
        double weightRange = maxWeight - minWeight;
    
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Progress: " + selectedExercise, padding, 20);
        
        g.drawLine(padding, padding, padding, padding + chartHeight);
        g.drawLine(padding, padding + chartHeight, padding + chartWidth, padding + chartHeight);
        
        g.setColor(Color.BLUE);
        Point prevPoint = null;
        
        for (int i = 0; i < points.size(); i++) {
            DataPoint point = points.get(i);
            
            int x = padding + (i * chartWidth) / (points.size() - 1);
            int y = padding + chartHeight - (int)((point.weight - minWeight) * chartHeight / weightRange);
            
            g.fillOval(x - 3, y - 3, 6, 6);
            if (prevPoint != null) {
                g.drawLine(prevPoint.x, prevPoint.y, x, y);
            }

            g.setColor(Color.RED);
            g.drawString(String.format("%.1f", point.weight), x + 5, y - 5);
            g.setColor(Color.BLUE);

            if (i == 0 || i == points.size() - 1) {
                g.setColor(Color.BLACK);
                g.drawString(point.date.toString(), x - 20, padding + chartHeight + 15);
            }
            
            prevPoint = new Point(x, y);
        }
        
        double progress = ((points.get(points.size() - 1).weight - points.get(0).weight) 
                / points.get(0).weight) * 100;
        g.setColor(progress >= 0 ? Color.GREEN : Color.RED);
        g.drawString(String.format("Progress: %.1f%%", progress), width - 150, 20);
    }

    // EFFECTS: draws a message in the center of the panel
    private void drawMessage(Graphics g, String message) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.ITALIC, 14));
        int messageWidth = g.getFontMetrics().stringWidth(message);
        g.drawString(message, (getWidth() - messageWidth) / 2, getHeight() / 2);
    }

    // EFFECTS: constructs a data point with given date and weight
    private static class DataPoint {
        LocalDate date;
        double weight;
        
        DataPoint(LocalDate date, double weight) {
            this.date = date;
            this.weight = weight;
        }
    }
}