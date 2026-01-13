package model;

import org.json.JSONObject;
import persistence.Writable;


// Represents an excersize with sets, reps, and weight used for the excersize
public class Excercise implements Writable {
    private String name;                    // name of excercise 
    private int sets;                       // number of sets done in the previous session for at max intensity
    private int reps;                       // number of reps for each set done in the previous session at max intensity
    private double weight;                  // amount of weight used in the previous session at max intensity in lbs
    private double volume;                  // total volume of previous session (sets x reps x weight) at max intensity
    private double orm;                     // one rep max weight
    private boolean ormIsEstimated;         // if one rep max weight is estimated with Brzycki Formula


    /*
     * REQUIRES: excersize Name has a non-zero length, initial Set, Reps, and Weight >= 0
     * EFFECTS: name of excersize is set to excersizeName; sets, reps, weight 
     *          is set to initialSet, initialReps, initialWeight respectively.
     *          orm is set as initialOrm unless = 0, then ormIsEstimated is set
     *          to true and a estimated orm is calculated
     */
    public Excercise(String excerciseName, int initialSet, int initialReps, double initialWeight, double initialOrm) {
        this.name = excerciseName;
        this.sets = initialSet;
        this.reps = initialReps;
        this.weight = initialWeight;
        this.volume = calculateVolume(initialSet, initialReps, initialWeight);
        if (initialOrm == 0) {
            calculateOrm();
        } else {
            orm = initialOrm;
            this.ormIsEstimated = false;
        }
    }

    /*
     * MODIFIES: this
     * EFFECTS: estimated one rep max calculated with Brzycki Formula, ormIsEstimated is set to true
     */
    public void calculateOrm() {
        double orm = weight / (1.0278 - (0.0278 * reps));
        this.orm = Math.round(orm * 100.0) / 100.0;
        this.ormIsEstimated = true;
    }

    /*
     * REQUIRES: s > 0, r > 0, w >= 0
     * EFFECTS: total volume of previous session (sets x reps x weight) at max intensity calculated
     */
    public double calculateVolume(int s, int r, double w) {
        w = Math.round(w * 100.0) / 100.0;
        return s * r * w;
    }

    public String getName() {
        return name;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public double getWeight() {
        return Math.round(weight * 100.0) / 100.0;
    }

    public double getOrm() {
        return Math.round(orm * 100.0) / 100.0;
    }

    public double getVolume() {
        return Math.round(volume * 100.0) / 100.0;
    }

    public boolean getOrmIsEstimated() {
        return ormIsEstimated;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("sets", sets);
        json.put("reps", reps);
        json.put("weight", weight);
        json.put("volume", volume);
        json.put("orm", orm);
        json.put("ormIsEstimated", ormIsEstimated);
        return json;
    }
}
