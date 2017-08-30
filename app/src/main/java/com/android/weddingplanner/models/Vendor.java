package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Vendor {

    public String vendorId;
    public String vendorName;
    public String vendorDescription;
    public ArrayList<String> vendorPhotos = new ArrayList<>();

    public Vendor() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Vendor(String vendorId, String vendorName, String vendorDescription, ArrayList<String> imagesLinks) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorDescription = vendorDescription;
        this.vendorPhotos = imagesLinks;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("vendorId", vendorId);
        result.put("vendorName", vendorName);
        result.put("vendorDescription", vendorDescription);
        result.put("vendorPhotos", vendorPhotos);


        return result;
    }
    // [END post_to_map]

}
// [END post_class]
