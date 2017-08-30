package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Vendors_categories {

    public String vendorCategoryName;
    public String categoryImage;
    public String vendorCategoryId;


    public Vendors_categories() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Vendors_categories(String vendorCategoryName, String imageUrl, String vendorCategoryId) {
        this.vendorCategoryName = vendorCategoryName;
        this.categoryImage=imageUrl;
        this.vendorCategoryId = vendorCategoryId;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("vendorCategoryName", vendorCategoryName);
        result.put("categoryImage", categoryImage);
        result.put("vendorCategoryId", vendorCategoryId);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
