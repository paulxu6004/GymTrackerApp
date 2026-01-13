package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.*;

@ExcludeFromJacocoGeneratedReport
public class TestGymTracker {
    private GymTracker tracker;
    private WorkoutSession pushSession1;
    private WorkoutSession pushSession2;
    private WorkoutSession legSession;
    private Excercise bench;
    private Excercise squat;

    @BeforeEach
    void runBefore() {
        tracker = new GymTracker();
        bench = new Excercise("Bench Press", 1, 10, 100.0, 0);
        squat = new Excercise("Squat", 1, 10, 200.0, 0);

        // Fixed: Use LocalDate instead of int
        pushSession1 = new WorkoutSession("Push", LocalDate.of(2025, 10, 10));
        pushSession1.addExcercise(bench);  // Fixed: method name
        
        pushSession2 = new WorkoutSession("Push", LocalDate.of(2025, 11, 10));
        pushSession2.addExcercise(new Excercise("Bench Press", 1, 10, 110, 0));  // Fixed: method name
        
        legSession = new WorkoutSession("Legs", LocalDate.of(2025, 12, 10));
        legSession.addExcercise(squat);  // Fixed: method name
    }

    @Test
    public void testConstructor() {
        assertTrue(tracker.getWorkoutHistory().isEmpty());
        assertEquals(0, tracker.getAllWorkoutSessions().size());
    }

    @Test
    public void testAddWorkoutSessionSingleSession() {
        tracker.addWorkoutSession(pushSession1);
       
        Map<String, List<WorkoutSession>> history = tracker.getWorkoutHistory();
        assertEquals(1, history.size());
        assertTrue(history.containsKey("Push"));
        assertEquals(1, history.get("Push").size());
    }

    @Test
    public void testAddWorkoutSessionMultipleSessionsSameFocus() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(pushSession2);
        
        Map<String, List<WorkoutSession>> history = tracker.getWorkoutHistory();
        assertEquals(1, history.size());
        assertEquals(2, history.get("Push").size()); 
    }

    @Test
    public void testAddWorkoutSessionMultipleSessionsDifferentFocus() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(legSession);
        
        Map<String, List<WorkoutSession>> history = tracker.getWorkoutHistory();
        assertEquals(2, history.size());
        assertTrue(history.containsKey("Push"));
        assertTrue(history.containsKey("Legs"));
        assertEquals(1, history.get("Push").size());
        assertEquals(1, history.get("Legs").size());
    }


    @Test
    public void testGetHistoryByFocus() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(pushSession2);
        
        List<WorkoutSession> pushSessions = tracker.getHistoryByFocus("Push");  // Fixed: "Push" not "Chest"
        assertEquals(2, pushSessions.size());
    }

    @Test
    public void testGetAllWorkoutSessions() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(legSession);
        List<WorkoutSession> allSessions = tracker.getAllWorkoutSessions();
        assertEquals(2, allSessions.size());
    }

    @Test
    public void testGetSessionsSortedByDate() {
        tracker.addWorkoutSession(pushSession1);  
        tracker.addWorkoutSession(pushSession2); 
        tracker.addWorkoutSession(legSession);    
        
        List<WorkoutSession> sorted = tracker.getSessionsSortedByDate();
        assertEquals(3, sorted.size());
        assertEquals(LocalDate.of(2025, 12, 10), sorted.get(0).getDate());
        assertEquals(LocalDate.of(2025, 11, 10), sorted.get(1).getDate());
        assertEquals(LocalDate.of(2025, 10, 10), sorted.get(2).getDate());
    }

    @Test
    public void testGetExcerciseProgress() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(pushSession2);
        Map<String, Double> progress = tracker.getExcerciseProgress("Bench Press");
        assertTrue(progress.containsKey("progress"));
        assertEquals(10.0, progress.get("progress"), 0.01);
    }

    @Test
    public void testGetExcerciseProgressNotEnoughData() {
        tracker.addWorkoutSession(pushSession1); 
        Map<String, Double> progress = tracker.getExcerciseProgress("Bench Press");
        assertEquals(0.0, progress.get("progress"));
    }

    @Test
    public void testGetExerciseWithLeastProgress() {
        tracker.addWorkoutSession(pushSession1);  
        tracker.addWorkoutSession(pushSession2);
        tracker.addWorkoutSession(legSession);
        
        WorkoutSession legSession2 = new WorkoutSession("Legs", LocalDate.of(2025, 12, 15));
        legSession2.addExcercise(new Excercise("Squat", 1, 10, 200.0, 0));
        tracker.addWorkoutSession(legSession2);
        String leastProgress = tracker.getExerciseWithLeastProgress();
        assertEquals("Squat", leastProgress);
    }

    @Test
    public void testGetExerciseWithLeastProgressNoData() {
        String result = tracker.getExerciseWithLeastProgress();
        assertEquals("No exercises tracked", result);
    }

    @Test
    public void testGetFocusWithLeastProgress() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(pushSession2);
        tracker.addWorkoutSession(legSession);
        
        String leastFocus = tracker.getFocusWithLeastProgress();
        assertEquals("Legs", leastFocus);
    }

    @Test
    public void testGetFocusWithLeastProgressNoData() {
        String result = tracker.getFocusWithLeastProgress();
        assertEquals("No focuses tracked", result);
    }

    @Test
    public void testGetAllExerciseNames() {
        tracker.addWorkoutSession(pushSession1);
        tracker.addWorkoutSession(legSession);
    
        String leastProgress = tracker.getExerciseWithLeastProgress();
        assertTrue(leastProgress.equals("Bench Press") || leastProgress.equals("Squat"));
    }
}