package com.android.weddingplanner.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String firstName;
    public String lastName;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String firstName,String lastName) {
        this.username = username;
        this.email = email;
        this.firstName=firstName;
        this.lastName=lastName;
    }

}
// [END blog_user_class]
