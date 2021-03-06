package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Vendor {

    public String vendorId;
    public String vendorName;
    public String vendorDescription;
    public String vendorZipCode;
    public ArrayList<String> vendorPhotos = new ArrayList<>();
    public String vendorPlace;
    public boolean isFavorite = false;
    public int vendorReviewsNumber = 0;
    public double vendorRating = 0.0;
    public String vendorCategoryId;
    public String vendorCategoryId_zipCode;

    public Vendor() {
    }

    public Vendor(String vendorId, String vendorName, String vendorDescription, String vendorPlace, String vendorCategoryId,String vendorZipCode, ArrayList<String> imagesLinks) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorDescription = vendorDescription;
        this.vendorPhotos = imagesLinks;
        this.vendorPlace = vendorPlace;
        this.vendorCategoryId = vendorCategoryId;
        this.vendorZipCode=vendorZipCode;
        this.vendorCategoryId_zipCode=vendorCategoryId+"_"+vendorZipCode;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("vendorId", vendorId);
        result.put("vendorName", vendorName);
        result.put("vendorDescription", vendorDescription);
        result.put("vendorPhotos", vendorPhotos);
        result.put("vendorPlace", vendorPlace);
        result.put("vendorReviewsNumber", vendorReviewsNumber);
        result.put("vendorRating", vendorRating);
        result.put("vendorCategoryId", vendorCategoryId);
        result.put("vendorZipCode",vendorZipCode);
        result.put("vendorCategoryId_zipCode",vendorCategoryId+"_"+vendorZipCode);


        return result;
    }
    // [END post_to_map]

}
// [END post_class]
