/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.android.weddingplanner.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();


    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "WeddingPlanner";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_BUDGET_ITEMS.CREATE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TABLE_BUDGET_ITEMS.DROP_TABLE);
        // Create tables again
        onCreate(db);
    }


    public static final class TABLE_BUDGET_ITEMS {
        public static final String TABLE_NAME = "budgetItems";
        public static final String COL_BUDGET_ITEM_ID = "id";
        public static final String COL_BUDGET_ITEM_NAME = "itemName";
        public static final String COL_BUDGET_ITEM_CATEGORY_ID = "itemCategoryId";
        public static final String COL_BUDGET_ITEM_CATEGORY_NAME = "itemCategoryName";
        public static final String COL_BUDGET_ITEM_VALUE = "itemValue";
        public static final String COL_BUDGET_ITEM_NOTES = "itemNotes";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_BUDGET_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BUDGET_ITEM_NAME + " TEXT  , " +
                COL_BUDGET_ITEM_CATEGORY_ID + " TEXT , " +
                COL_BUDGET_ITEM_CATEGORY_NAME + " TEXT , " +
                COL_BUDGET_ITEM_NOTES + " TEXT , " +
                COL_BUDGET_ITEM_VALUE + " REAL  " +
                ")";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


}
