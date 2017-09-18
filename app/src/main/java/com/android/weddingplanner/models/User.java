package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public String gcm;

    public User(String gcm) {
        this.gcm = gcm;
    }

    public String profilePicture = "";

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String firstName, String lastName, String gcm) {
        this.username = username;
        this.email = email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.gcm = gcm;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("gcm", gcm);
        return result;
    }

}
// [END blog_user_class]
