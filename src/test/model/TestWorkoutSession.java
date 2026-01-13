package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@ExcludeFromJacocoGeneratedReport
public class TestWorkoutSession {
    private WorkoutSession session1;
    private Excercise benchPress;
    private Excercise shoulderPress;
    private Excercise chestFly;

    @BeforeEach
    void runBefore() {
        session1 = new WorkoutSession("Push", LocalDate.of(2025, 10, 10));
        benchPress = new Excercise("Bench Press", 1, 10, 100.0, 0);
        shoulderPress = new Excercise("Shoulder Press", 1, 10, 50.0, 0);
        chestFly = new Excercise("Chest Fly", 1, 10, 90.0, 0);
    }

    @Test
    public void testConstructor() {
        assertEquals(LocalDate.of(2025, 10, 10), session1.getDate());
        assertEquals("Push", session1.getFocus());
        assertEquals(0, session1.getExcerciseCount());
        assertTrue(session1.getExcercises().isEmpty());
    }

    @Test
    public void testAddExcercise() {
        session1.addExcercise(benchPress);
        assertEquals(1, session1.getExcerciseCount());
        assertEquals("Bench Press", session1.getExcercises().get(0).getName());
    }

    @Test
    public void testAddMultipleExcercises() { 
        session1.addExcercise(benchPress);
        session1.addExcercise(shoulderPress);
        session1.addExcercise(chestFly);

        assertEquals(3, session1.getExcerciseCount());
        assertEquals("Bench Press", session1.getExcercises().get(0).getName());
        assertEquals("Shoulder Press", session1.getExcercises().get(1).getName());
        assertEquals("Chest Fly", session1.getExcercises().get(2).getName());
    }

    @Test
    public void testGetAllExcerciseNames() {  
        session1.addExcercise(benchPress);
        session1.addExcercise(shoulderPress);
        
        List<String> names = session1.getAllExcerciseNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("Bench Press"));
        assertTrue(names.contains("Shoulder Press"));
    }

    @Test
    public void testGetExcerciseCount() {  
        assertEquals(0, session1.getExcerciseCount());
        session1.addExcercise(benchPress);
        assertEquals(1, session1.getExcerciseCount());
        session1.addExcercise(shoulderPress);
        assertEquals(2, session1.getExcerciseCount());
    }
}
