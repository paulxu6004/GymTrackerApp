package persistence;

import model.Excercise;
import model.WorkoutSession;

import static org.junit.jupiter.api.Assertions.*;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

// I referenced off of the JsonSerialization Demo for this class
@ExcludeFromJacocoGeneratedReport
public class JsonTest {
    protected void checkExercise(String name, int sets, int reps, double weight, double orm, Excercise exercise) {
        assertEquals(name, exercise.getName());
        assertEquals(sets, exercise.getSets());
        assertEquals(reps, exercise.getReps());
        assertEquals(weight, exercise.getWeight(), 0.01);
        assertEquals(orm, exercise.getOrm(), 0.01);
    }

    protected void checkWorkoutSession(String focus, String date, int exerciseCount, WorkoutSession session) {
        assertEquals(focus, session.getFocus());
        assertEquals(date, session.getDate().toString());
        assertEquals(exerciseCount, session.getExcerciseCount());
    }
}
