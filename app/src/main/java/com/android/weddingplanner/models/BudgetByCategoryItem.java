package com.android.weddingplanner.models;

import java.util.ArrayList;


public class BudgetByCategoryItem {
    public String categoryId;
    public String categoryName;
    public ArrayList<BudgetItem> budgetItems = new ArrayList<>();
}
