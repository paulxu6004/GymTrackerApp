package persistence;


import model.Excercise;
import model.GymTracker;
import model.WorkoutSession;
import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// I referenced off of the JsonSerialization Demo for this class
@ExcludeFromJacocoGeneratedReport
class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            GymTracker gymTracker = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyGymTracker() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyGymTracker.json");
        try {
            GymTracker gymTracker = reader.read();
            assertEquals(0, gymTracker.getAllWorkoutSessions().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralGymTracker() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralGymTracker.json");
        try {
            GymTracker gymTracker = reader.read();
            
            assertEquals(2, gymTracker.getAllWorkoutSessions().size());
            WorkoutSession pushSession = gymTracker.getHistoryByFocus("Push").get(0);
            checkWorkoutSession("Push", "2024-01-15", 2, pushSession);
            WorkoutSession legSession = gymTracker.getHistoryByFocus("Legs").get(0);
            checkWorkoutSession("Legs", "2024-01-16", 1, legSession);
            Excercise benchPress = pushSession.getExcercises().get(0);
            checkExercise("Bench Press", 3, 10, 185.0, 225.0, benchPress);
            Excercise shoulderPress = pushSession.getExcercises().get(1);
            checkExercise("Shoulder Press", 3, 12, 95.0, 115.0, shoulderPress);
            Excercise squat = legSession.getExcercises().get(0);
            checkExercise("Squat", 4, 8, 225.0, 275.0, squat);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}