package com.android.weddingplanner.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.android.weddingplanner.R;
import com.android.weddingplanner.helper.DatabaseOps;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.models.Vendors_categories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddBudgetItemActivity extends AppCompatActivity {
    private ArrayList<Vendors_categories> vendorCategoriesList = new ArrayList<>();
    public String[] vendorsCategories;
    private MaterialSpinner spinner;

    private EditText editText_itemName;
    private EditText editText_itemBudgetValue;
    private EditText editText_itemNotes;
    private Button button_save;
    private Button button_delete;

    private DatabaseReference mDatabase;
    private ArrayAdapter<String> adapter;

    String vendorCategoryId;
    String vendorCategoryName;
    long itemEditId;

    BudgetItem budgetItemPassed;
    public static final String BUDGET_ITEM_KEY = "budgetItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_item);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setUpToolBar();
        setUpViews();
        retrieveVendorsCategories();

        if (getIntent() != null) {
            budgetItemPassed = getIntent().getParcelableExtra(BUDGET_ITEM_KEY);

            if (budgetItemPassed != null) {


                itemEditId = budgetItemPassed.itemId;
                editText_itemBudgetValue.setText(String.valueOf(budgetItemPassed.itemBudjet));
                editText_itemName.setText(budgetItemPassed.itemName);
                editText_itemNotes.setText(budgetItemPassed.itemNotes);
            }
        }
    }

    private void setUpViews() {

        editText_itemName = (EditText) findViewById(R.id.editText_itemName);
        editText_itemBudgetValue = (EditText) findViewById(R.id.editText_itemBudget);
        editText_itemNotes = (EditText) findViewById(R.id.editText_itemNotes);

        button_save = (Button) findViewById(R.id.button_save);
        button_delete = (Button) findViewById(R.id.button_delete);

        spinner = (MaterialSpinner) findViewById(R.id.spinner_vendorsCategories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 0) {

                    vendorCategoryId = vendorCategoriesList.get(position).vendorCategoryId;
                    vendorCategoryName = vendorCategoriesList.get(position).vendorCategoryName;
                } else {
                    vendorCategoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    BudgetItem item = new BudgetItem();

                    item.itemName = editText_itemName.getText().toString();
                    item.itemBudjet = Float.parseFloat(editText_itemBudgetValue.getText().toString());
                    item.itemNotes = editText_itemNotes.getText().toString();
                    item.itemCategoryId = vendorCategoryId;
                    item.itemCategoryName = vendorCategoryName;
                    if (itemEditId != 0) {
                        item.itemId = itemEditId;
                        DatabaseOps.getCurrentInstance().updateBudgetItem(item);

                    } else {

                        DatabaseOps.getCurrentInstance().insertBudgetItem(item);
                    }

                    finish();
                }
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemEditId != 0) {
                    DatabaseOps.getCurrentInstance().deleteBudgetItem(budgetItemPassed);
                    finish();
                } else {
                    finish();
                }
            }
        });
    }

    private void retrieveVendorsCategories() {
        Query postsQuery = getQuery(mDatabase);

        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear old data
                vendorCategoriesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Vendors_categories categories = postSnapshot.getValue(Vendors_categories.class);


                    if (!TextUtils.isEmpty(categories.vendorCategoryName) && !TextUtils.isEmpty(categories.vendorCategoryId)) {
                        vendorCategoriesList.add(categories);
                    }
                    vendorsCategories = new String[vendorCategoriesList.size()];
                    for (int i = 0; i < vendorCategoriesList.size(); i++) {
                        vendorsCategories[i] = vendorCategoriesList.get(i).vendorCategoryName;
                    }
                    setUpSpinner();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpSpinner() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vendorsCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPaddingSafe(0, 0, 0, 0);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        //retrieve vendors categories
        Query vendorsCategoriesQuery = databaseReference.child("vendors-categories").orderByKey();
        return vendorsCategoriesQuery;
    }

    private void setUpToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Budget Item");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editText_itemName.getText().toString())) {
            editText_itemName.setError("Required");
            result = false;
        } else {
            editText_itemName.setError(null);
        }

        if (TextUtils.isEmpty(editText_itemBudgetValue.getText().toString())) {
            editText_itemBudgetValue.setError("Required");
            result = false;
        } else {
            editText_itemBudgetValue.setError(null);
        }


        if (TextUtils.isEmpty(vendorCategoryId)) {
            spinner.setError("Required");
            result = false;
        } else {
            spinner.setError(null);
        }

        return result;
    }
}
