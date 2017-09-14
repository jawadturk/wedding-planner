package com.android.weddingplanner.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.weddingplanner.application.ApplicationClass;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;


import java.util.ArrayList;
import java.util.List;


public class DatabaseOps {

    private static DatabaseOps currentInstance;

    private SQLiteHandler sqLiteHandler;

    private DatabaseOps() {
        sqLiteHandler = new SQLiteHandler(ApplicationClass.getInstance());
    }

    public synchronized static DatabaseOps getCurrentInstance() {
        if (currentInstance == null) {
            currentInstance = new DatabaseOps();
        }
        return currentInstance;
    }


    public boolean insertBudgetItem(BudgetItem item) {

        SQLiteDatabase writableDatabase = sqLiteHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();


        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NAME, item.itemName);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_VALUE, item.itemBudjet);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NOTES, item.itemNotes);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_ID, item.itemCategoryId);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_NAME, item.itemCategoryName);

        long insertedRowID = writableDatabase.insertWithOnConflict(SQLiteHandler.TABLE_BUDGET_ITEMS.TABLE_NAME,
                null,
                contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        writableDatabase.close();

        return insertedRowID >= 0;
    }

    public List<BudgetByCategoryItem> getBudgetItems() {


        SQLiteDatabase readableDatabase = sqLiteHandler.getReadableDatabase();

        String selectQuery = "SELECT * FROM "
                + SQLiteHandler.TABLE_BUDGET_ITEMS.TABLE_NAME
//                +" GROUP BY "+SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_ID
                + " ORDER BY " + SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_ID + " ASC";

        Cursor cursor = readableDatabase.rawQuery(selectQuery, null);
        List<BudgetByCategoryItem> budgetByCategoryItems = new ArrayList<>();
        String categoryId = "";
        if (!cursor.moveToFirst())
            return budgetByCategoryItems;

        do {
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.itemId = (cursor.getLong(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_ID)));
            budgetItem.itemName = (cursor.getString(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NAME)));
            budgetItem.itemBudjet = (cursor.getFloat(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_VALUE)));
            budgetItem.itemNotes = (cursor.getString(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NOTES)));
            budgetItem.itemCategoryId = (cursor.getString(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_ID)));
            budgetItem.itemCategoryName = (cursor.getString(cursor.getColumnIndex(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_NAME)));


            if (!budgetItem.itemCategoryId.equals(categoryId)) {
                BudgetByCategoryItem categoryItems = new BudgetByCategoryItem();
                categoryItems.categoryId = budgetItem.itemCategoryId;
                categoryItems.categoryName = budgetItem.itemCategoryName;
                budgetByCategoryItems.add(categoryItems);

            }
            budgetByCategoryItems.get(budgetByCategoryItems.size() - 1).budgetItems.add(budgetItem);
            categoryId = budgetItem.itemCategoryId;


        } while (cursor.moveToNext());
        cursor.close();
        readableDatabase.close();
        Log.d("sqllite", "getBudgetItems: " + budgetByCategoryItems.size());
        return budgetByCategoryItems;

    }

    public boolean updateBudgetItem(BudgetItem item) {
        SQLiteDatabase writableDatabase = sqLiteHandler.getWritableDatabase();//open database

        ContentValues contentValues = new ContentValues();

        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NAME, item.itemName);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_VALUE, item.itemBudjet);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_NOTES, item.itemNotes);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_ID, item.itemCategoryId);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_CATEGORY_NAME, item.itemCategoryName);
        contentValues.put(SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_ID, item.itemId);

        int numberOfAffectedRows = writableDatabase.update(SQLiteHandler
                        .TABLE_BUDGET_ITEMS
                        .TABLE_NAME,
                contentValues,
                SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.itemId)});

        writableDatabase.close();//release database


        return numberOfAffectedRows > 0;
    }


    public boolean deleteBudgetItem(BudgetItem item) {
        String table = SQLiteHandler.TABLE_BUDGET_ITEMS.TABLE_NAME;
        String whereClause = SQLiteHandler.TABLE_BUDGET_ITEMS.COL_BUDGET_ITEM_ID + " =?";
        String[] whereArgs = new String[]{Long.toString(item.itemId)};
        int numberOfAffectedRows = sqLiteHandler
                .getWritableDatabase()
                .delete(table, whereClause, whereArgs);

        return numberOfAffectedRows > 0;
    }

    public void deleteAllBudgetItems() {

        sqLiteHandler.getWritableDatabase().delete(SQLiteHandler.TABLE_BUDGET_ITEMS.TABLE_NAME, null, null);


    }


}
