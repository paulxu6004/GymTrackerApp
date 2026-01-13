package persistence;

import model.Excercise;
import model.WorkoutSession;
import model.GymTracker;
import org.junit.jupiter.api.Test;

import ca.ubc.cs.ExcludeFromJacocoGeneratedReport;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// I referenced off of the JsonSerialization Demo for this class
@ExcludeFromJacocoGeneratedReport
class JsonWriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyGymTracker() {
        try {
            GymTracker gymTracker = new GymTracker();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyGymTracker.json");
            writer.open();
            writer.write(gymTracker);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyGymTracker.json");
            gymTracker = reader.read();
            assertEquals(0, gymTracker.getAllWorkoutSessions().size());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralGymTracker() {
        try {
            GymTracker gymTracker = new GymTracker();

            //Equivalent to thingy 1
            WorkoutSession pushSession = new WorkoutSession("Push", LocalDate.of(2024, 1, 15));
            pushSession.addExcercise(new Excercise("Bench Press", 3, 10, 185.0, 225.0));
            pushSession.addExcercise(new Excercise("Shoulder Press", 3, 12, 95.0, 115.0));
            
            //Equivalent to thingy 2
            WorkoutSession legSession = new WorkoutSession("Legs", LocalDate.of(2024, 1, 16));
            legSession.addExcercise(new Excercise("Squat", 4, 8, 225.0, 275.0));
            
            gymTracker.addWorkoutSession(pushSession);
            gymTracker.addWorkoutSession(legSession);

            JsonWriter writer = new JsonWriter("./data/testWriterGeneralGymTracker.json");
            writer.open();
            writer.write(gymTracker);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralGymTracker.json");
            gymTracker = reader.read();
            assertEquals(2, gymTracker.getAllWorkoutSessions().size());
            assertEquals(2, gymTracker.getWorkoutHistory().size());
            assertTrue(gymTracker.getWorkoutHistory().containsKey("Push"));
            assertTrue(gymTracker.getWorkoutHistory().containsKey("Legs"));
            
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}