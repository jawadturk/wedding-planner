package com.android.weddingplanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.weddingplanner.models.Vendor;
import com.android.weddingplanner.models.Vendors_categories;
import com.android.weddingplanner.viewholder.VendorsCategoriesViewHolder;
import com.android.weddingplanner.viewholder.VendorsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VendorsActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Vendor, VendorsViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public static final String EXTRA_VENDOR_CATEGORY_KEY = "vendor_category";
    private String mVendorCategoryKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);

        // Get post key from intent
        mVendorCategoryKey = getIntent().getStringExtra(EXTRA_VENDOR_CATEGORY_KEY);
        if (mVendorCategoryKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_vendors);
        mRecycler.setHasFixedSize(true);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

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

    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("vendors").orderByChild("vendorCategoryId").equalTo(mVendorCategoryKey);
        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
