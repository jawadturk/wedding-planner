package com.android.weddingplanner.helper;


import com.android.weddingplanner.application.ApplicationClass;


public class BudgetManager {
    private static final String KEY_BUGET_VALUE = "budgetValue";
    private static final int defaultBudgetValue = 0;


    public static float getBudgetValue() {

        return PrefUtils.getFloat(ApplicationClass.getInstance(), KEY_BUGET_VALUE, defaultBudgetValue);
    }

    public static void setBudgetValue(float value) {
        PrefUtils.setFloat(ApplicationClass.getInstance(), KEY_BUGET_VALUE, value);
    }


}
