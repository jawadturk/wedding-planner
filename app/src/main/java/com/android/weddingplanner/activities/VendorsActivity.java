package com.android.weddingplanner.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Vendor;
import com.android.weddingplanner.viewholder.VendorsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VendorsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Vendor, VendorsViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private EditText editText_zipCode;
    private Button button_search;
    private LinearLayoutManager mManager;

    public static final String EXTRA_VENDOR_CATEGORY_KEY = "vendor_category";
    public static final String EXTRA_VENDOR_CATEGORY_NAME = "vendor_category_Name";
    private String mVendorCategoryKey;
    private String mVendorCategoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);

        // Get post key from intent
        mVendorCategoryKey = getIntent().getStringExtra(EXTRA_VENDOR_CATEGORY_KEY);
        mVendorCategoryName = getIntent().getStringExtra(EXTRA_VENDOR_CATEGORY_NAME);
        if (mVendorCategoryKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        editText_zipCode=(EditText)  findViewById(R.id.editText_zipCode);
        button_search=(Button)  findViewById(R.id.button_searchZipCode);


        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCode=editText_zipCode.getText().toString().trim();
                if (zipCode.length()>0)
                {
                    Query vendorsQuery =   getQuery(mDatabase,zipCode);
                    mAdapter = new FirebaseRecyclerAdapter<Vendor, VendorsViewHolder>(Vendor.class, R.layout.vendor_row_item,
                            VendorsViewHolder.class, vendorsQuery) {
                        @Override
                        protected void populateViewHolder(final VendorsViewHolder viewHolder, final Vendor model, final int position) {
                            final DatabaseReference vendorsRef = getRef(position);

                            // Set click listener for the whole post view
                            final String vendorKey = vendorsRef.getKey();

                            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(VendorsActivity.this, VendorDetailsAvtivity.class);
                                    intent.putExtra(VendorDetailsAvtivity.EXTRA_VENDOR_KEY, vendorKey);
                                    startActivity(intent);
                                }
                            });


                            // Bind Post to ViewHolder, setting OnClickListener for the star button
                            viewHolder.bindToPost(model, new View.OnClickListener() {
                                @Override
                                public void onClick(View starView) {

                                }
                            });
                        }
                    };
                    mRecycler.setAdapter(mAdapter);

                }
            }
        });

        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_vendors);
        mRecycler.setHasFixedSize(true);

        setupRecyclerView();
        setUpToolBar();
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase,"");

        mAdapter = new FirebaseRecyclerAdapter<Vendor, VendorsViewHolder>(Vendor.class, R.layout.vendor_row_item,
                VendorsViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final VendorsViewHolder viewHolder, final Vendor model, final int position) {
                final DatabaseReference vendorsRef = getRef(position);

                // Set click listener for the whole post view
                final String vendorKey = vendorsRef.getKey();

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VendorsActivity.this, VendorDetailsAvtivity.class);
                        intent.putExtra(VendorDetailsAvtivity.EXTRA_VENDOR_KEY, vendorKey);
                        startActivity(intent);
                    }
                });


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {

                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference,String zipCode) {
        Query recentPostsQuery;
        if (zipCode.isEmpty()) {
            recentPostsQuery = databaseReference.child("vendors").orderByChild("vendorCategoryId").equalTo(mVendorCategoryKey);

        }else
        {
            recentPostsQuery = databaseReference.child("vendors").orderByChild("vendorCategoryId_zipCode").equalTo(mVendorCategoryKey+"_"+zipCode);

        }


        return recentPostsQuery;
    }

    private void setUpToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mVendorCategoryName);
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
}
