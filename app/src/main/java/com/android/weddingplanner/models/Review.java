package com.android.weddingplanner.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Review {

    public String uid;
    public String author;
    public String review;
    public float rating;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Review(String uid, String author, String text, float rating) {
        this.uid = uid;
        this.author = author;
        this.review = text;
        this.rating = rating;
    }

}
// [END comment_class]
