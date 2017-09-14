package com.android.weddingplanner.models;

import java.util.ArrayList;

/**
 * Created by jawad.turk on 12-Sep-17.
 */

public class BudgetByCategoryItem {
    public String categoryId;
    public String categoryName;
    public ArrayList<BudgetItem> budgetItems = new ArrayList<>();
}
