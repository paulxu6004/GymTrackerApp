package persistence;

import model.Excercise;
import model.GymTracker;
import model.WorkoutSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.time.LocalDate;

import org.json.*;

// Represents a reader that reads workroom from JSON data stored in file
// I referenced off of the JsonSerialization Demo for this class
public class JsonReader {
    private String source;

    //EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    //EFFECTS: reads gym tracker from file and returns it;
    //throws IOException if an error occurs reading data from file
    public GymTracker read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseGymTracker(jsonObject);
    }

    //EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    //EFFECTS: parses gym tracker from JSON object and returns it
    private GymTracker parseGymTracker(JSONObject jsonObject) {
        GymTracker gymTracker = new GymTracker();
        JSONObject workoutHistory = jsonObject.getJSONObject("workoutHistory");
        addWorkoutSessions(gymTracker, workoutHistory);
        return gymTracker;
    }

    // MODIFIES: gymTracker
    // EFFECTS: parses workout sessions from JSON object and adds them to gym tracker
    private void addWorkoutSessions(GymTracker gymTracker, JSONObject jsonObject) {
        for (String focus : jsonObject.keySet()) {
            JSONArray jsonArray = jsonObject.getJSONArray(focus);
            for (Object json : jsonArray) {
                JSONObject nextSession = (JSONObject) json;
                addWorkoutSession(gymTracker, nextSession);
            }
        }
    }

    //MODIFIES: gymTracker
    //EFFECTS: parses workout session from JSON object and adds it to gym tracker
    private void addWorkoutSession(GymTracker gymTracker, JSONObject jsonObject) {
        String focus = jsonObject.getString("focus");
        LocalDate date = LocalDate.parse(jsonObject.getString("date"));
        WorkoutSession session = new WorkoutSession(focus, date);
        addExcercises(session, jsonObject.getJSONArray("exercises"));
        gymTracker.addWorkoutSession(session);
    }

    // MODIFIES: session
    // EFFECTS: parses excercises from JSON array and adds them to workout session
    private void addExcercises(WorkoutSession session, JSONArray jsonArray) {
        for (Object json : jsonArray) {
            JSONObject nextExercise = (JSONObject) json;
            addExcercise(session, nextExercise);
        }
    }

    // MODIFIES: session
    // EFFECTS: parses excercise from JSON object and adds it to workout session
    private void addExcercise(WorkoutSession session, JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        int sets = jsonObject.getInt("sets");
        int reps = jsonObject.getInt("reps");
        double weight = jsonObject.getDouble("weight");
        double orm = jsonObject.getDouble("orm");
        
        Excercise excercise = new Excercise(name, sets, reps, weight, orm);
        session.addExcercise(excercise);
    }
}