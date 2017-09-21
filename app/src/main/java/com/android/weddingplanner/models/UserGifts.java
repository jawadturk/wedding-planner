package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserGifts {

    public UserGifts(String giftTitle, String giftStoreDetails, String giftImage, String userId) {
        this.giftTitle = giftTitle;
        this.giftImage = giftImage;
        this.giftStoreDetails = giftStoreDetails;
        this.userId = userId;
    }

    public String giftTitle;
    public String giftImage;
    public String giftStoreDetails;
    public String userId;
    public boolean purchsed = false;


    public UserGifts() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("giftTitle", giftTitle);
        result.put("giftImage", giftImage);
        result.put("giftStoreDetails", giftStoreDetails);
        result.put("userId", userId);
        result.put("purchsed", purchsed);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
