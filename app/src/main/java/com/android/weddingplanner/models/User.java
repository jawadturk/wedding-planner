package com.android.weddingplanner.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String firstName;
    public String lastName;
    public String gcm;

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

}
// [END blog_user_class]
