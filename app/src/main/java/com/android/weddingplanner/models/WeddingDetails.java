package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class WeddingDetails {

    public String userId;

    public WeddingDetails(String userId, String groomName, String brideName, String weddingLocation, String weddingtime) {
        this.userId = userId;
        this.groomName = groomName;
        this.brideName = brideName;
        this.weddingLocation = weddingLocation;
        this.weddingtime = weddingtime;
    }

    public String groomName;
    public String brideName;
    public String weddingLocation;
    public String weddingtime;


    public WeddingDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("groomName", groomName);
        result.put("brideName", brideName);
        result.put("weddingLocation", weddingLocation);
        result.put("weddingtime", weddingtime);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
