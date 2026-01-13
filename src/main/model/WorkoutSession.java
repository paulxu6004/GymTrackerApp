package model;

import model.EventLog;
import model.Event;

import java.time.LocalDate;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;
import persistence.Writable;


//represents a workout session with a muscle group focus and a list of excercises and date it was done on
public class WorkoutSession implements Writable {
    private String focus;                   // muscle group focus of this session
    private List<Excercise> excercises;     // list of excercises done this session
    private LocalDate date;                       // date of workout session


    /*
     * REQUIRES: sessionFocus is not 0 length and sessionDate is not null and sessions not null
     * EFFECTS: focus is set to sessionFocus and date is set to sessionDate
     *          an empty arraylist is created for excercises. 
     */
    public WorkoutSession(String sessionFocus, LocalDate sessionDate) {
        this.focus = sessionFocus;
        this.date = sessionDate;
        this.excercises = new ArrayList<>();
    }

    /*
     * REQUIRES: excercise != null
     * MODIFIES: this
     * EFFECTS: adds the exercise to this workout session's exercise list and logs the event
     */
    public void addExcercise(Excercise excercise) {
        this.excercises.add(excercise);
        EventLog.getInstance().logEvent(new Event("Added exercise: " + excercise.getName() 
                + " to " + focus + " session on " + date));
    }

    /*
     * EFFECTS: returns the number of excercises in this session
     */
    public int getExcerciseCount() {
        return this.excercises.size();
    }

    /*
     * EFFECTS: returns list of all excercise names in this session
     */
    public List<String> getAllExcerciseNames() {
        List<String> names = new ArrayList<>();
        for (Excercise excercise : this.excercises) {
            names.add(excercise.getName());
        }
        return names;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getFocus() {
        return focus;
    }

    public List<Excercise> getExcercises() {
        return excercises;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("focus", focus);
        json.put("date", date.toString());
        json.put("exercises", exercisesToJson());
        return json;
    }

    private JSONArray exercisesToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Excercise exercise : this.excercises) {
            jsonArray.put(exercise.toJson());
        }
        return jsonArray;
    }
}
