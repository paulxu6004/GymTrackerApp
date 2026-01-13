package model;

import model.EventLog;
import model.Event;

import org.json.JSONObject;
import org.json.JSONArray;
import persistence.Writable;

import java.util.*;

// Represents the gym tracker that tracks the progress of a user over time with each workout session and exercise stats
public class GymTracker implements Writable {
    private Map<String, List<WorkoutSession>> workoutHistory;

    /*
     * EFFECTS: creates Hash Map for the Workout History 
     */
    public GymTracker() {
        this.workoutHistory = new HashMap<>();
    }

    /*
     * REQUIRES: session != null
     * MODIFIES: this
     * EFFECTS: adds the workout session to the history organized by focus
     */
    public void addWorkoutSession(WorkoutSession session) {
        String focus = session.getFocus();
        if (!this.workoutHistory.containsKey(focus)) {
            workoutHistory.put(focus, new ArrayList<>());
        }
        this.workoutHistory.get(focus).add(session);
        EventLog.getInstance().logEvent(new Event("Added workout session: " 
                + session.getFocus() + " on " + session.getDate()));
    }

    /*
     * EFFECTS: returns list of workout sessions for the given focus,
     *          or empty list if focus not found
     */
    public List<WorkoutSession> getHistoryByFocus(String focus) {
        List<WorkoutSession> sessions = this.workoutHistory.get(focus);
        if (sessions == null) {
            return new ArrayList<>();
        }
        return sessions;
    }

    /*
     * EFFECTS: returns all workout sessions from all focuses combined
     */
    public List<WorkoutSession> getAllWorkoutSessions() {
        List<WorkoutSession> allSessions = new ArrayList<>();
        for (List<WorkoutSession> sessions : this.workoutHistory.values()) {
            allSessions.addAll(sessions);
        }
        return allSessions;
    }

    /*
     * EFFECTS: returns the workout history
     */
    public Map<String, List<WorkoutSession>> getWorkoutHistory() {
        return workoutHistory;
    }

    /*
     * EFFECTS: returns all workout sessions sorted by date from oldest to newest
     */
    public List<WorkoutSession> getSessionsSortedByDate() {
        List<WorkoutSession> allSessions = getAllWorkoutSessions();
        allSessions.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        return allSessions;
    }


    /*
     * REQUIRES: exercise name exists and has been recorded
     * EFFECTS: gets progress for a specific exercise returns progress as 
     *          percentage improvement in ORM for time periods (weekly, monthly, yearly)
     */
    public Map<String, Double> getExcerciseProgress(String excerciseName) {
        Map<String, Double> progress = new HashMap<>();
        List<Double> orms = getAllORMsForExcercise(excerciseName);

        if (orms.size() < 2) {
            progress.put("progress", 0.0);
        } else {
            double newest = orms.get(0);
            double oldest = orms.get(orms.size() - 1);
            double improvement = ((newest - oldest) / oldest) * 100;
            progress.put("progress", Math.round(improvement * 100.0) / 100.0);
        }
        
        return progress;
    }

    /* helper function for getExcerciseProgress
     * EFFECTS: gets all ORMs for a specific exercise, sorted by date with newest first
     */
    private List<Double> getAllORMsForExcercise(String excerciseName) {
        List<Double> orms = new ArrayList<>();
        List<WorkoutSession> allSessions = getAllWorkoutSessions();
        allSessions.sort((s1, s2) -> s2.getDate().compareTo(s1.getDate()));
        
        for (WorkoutSession session : allSessions) {
            for (Excercise excercise : session.getExcercises()) {
                if (excercise.getName().equals(excerciseName)) {
                    orms.add(excercise.getOrm());
                }
            }
        }
        return orms;
    }

    /*
     * REQUIRES: there are exercises tracked in workout sessions
     * EFFECTS: gets name of exercise with least progress in terms of 
     *          percentage improvement in ORM
     */
    public String getExerciseWithLeastProgress() {
        if (getAllWorkoutSessions().isEmpty()) {
            return "No exercises tracked";
        }
        
        Map<String, Double> exerciseProgress = new HashMap<>();
        
        // Get all unique exercise names
        Set<String> allExerciseNames = getAllExerciseNames(); // ← Uses helper method
        
        // Calculate progress for each exercise
        for (String exerciseName : allExerciseNames) {
            Map<String, Double> progress = getExcerciseProgress(exerciseName);
            exerciseProgress.put(exerciseName, progress.get("progress"));
        }
        
        // Find exercise with least progress
        String leastProgressExercise = "";
        double leastProgress = Double.MAX_VALUE;
        
        for (String exerciseName : exerciseProgress.keySet()) {
            double progress = exerciseProgress.get(exerciseName);
            if (progress < leastProgress) {
                leastProgress = progress;
                leastProgressExercise = exerciseName;
            }
        }
        
        return leastProgressExercise;
    }

    /* helper function for getExerciseWithLeastProgress
     * EFFECTS: gets all excercise names from all sessions
     */
    private Set<String> getAllExerciseNames() {
        Set<String> excerciseNames = new HashSet<>();
        for (WorkoutSession session : getAllWorkoutSessions()) {
            for (Excercise excercise : session.getExcercises()) {
                excerciseNames.add(excercise.getName());
            }
        }
        return excerciseNames;
    }

    /*
     * REQUIRES: there are workouts with different focuses in workout history
     * EFFECTS: gets name of focus with least progress measured by least sessions
     */
    public String getFocusWithLeastProgress() {
        if (this.workoutHistory.isEmpty()) {
            return "No focuses tracked";
        }
        
        String leastFocus = "";
        int minSessions = Integer.MAX_VALUE;
        
        for (String focus : this.workoutHistory.keySet()) {
            int sessionCount = this.workoutHistory.get(focus).size();
            if (sessionCount < minSessions) {
                minSessions = sessionCount;
                leastFocus = focus;
            }
        }
        
        return leastFocus;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("workoutHistory", workoutHistoryToJson());
        return json;
    }

    private JSONObject workoutHistoryToJson() {
        JSONObject json = new JSONObject();
        for (String focus : this.workoutHistory.keySet()) {
            json.put(focus, sessionsToJson(this.workoutHistory.get(focus)));
        }
        return json;
    }

    private JSONArray sessionsToJson(List<WorkoutSession> sessions) {
        JSONArray jsonArray = new JSONArray();
        for (WorkoutSession session : sessions) {
            jsonArray.put(session.toJson());
        }
        return jsonArray;
    }

}
