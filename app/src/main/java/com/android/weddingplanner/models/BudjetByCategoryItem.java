package com.android.weddingplanner.models;

import java.util.ArrayList;

/**
 * Created by jawad.turk on 12-Sep-17.
 */

public class BudjetByCategoryItem {
    public String categoryId;
    public String categoryName;
    public ArrayList<BudjetItem> budjetItems = new ArrayList<>();
}
