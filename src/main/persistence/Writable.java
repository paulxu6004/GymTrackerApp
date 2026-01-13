package persistence;

import org.json.JSONObject;

// This was taken from the Json Serialization Demo given 
public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
