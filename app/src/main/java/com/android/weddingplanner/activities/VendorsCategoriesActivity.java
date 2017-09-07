package com.android.weddingplanner.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Vendors_categories;
import com.android.weddingplanner.viewholder.VendorsCategoriesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VendorsCategoriesActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Vendors_categories, VendorsCategoriesViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_categories);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_vendorsCategories);
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
        mAdapter = new FirebaseRecyclerAdapter<Vendors_categories, VendorsCategoriesViewHolder>(Vendors_categories.class, R.layout.item_vendor_category,
                VendorsCategoriesViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final VendorsCategoriesViewHolder viewHolder, final Vendors_categories model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String categoryKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VendorsCategoriesActivity.this, VendorsActivity.class);
                        intent.putExtra(VendorsActivity.EXTRA_VENDOR_CATEGORY_KEY, categoryKey);
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
        Query recentPostsQuery = databaseReference.child("vendors-categories");
        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
