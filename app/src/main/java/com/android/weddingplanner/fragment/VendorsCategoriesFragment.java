package com.android.weddingplanner.fragment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.VendorsActivity;
import com.android.weddingplanner.models.Vendors_categories;
import com.android.weddingplanner.viewholder.VendorsCategoriesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VendorsCategoriesFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Vendors_categories, VendorsCategoriesViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootview;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_vendors_categories, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_vendorsCategories);
        mRecycler.setHasFixedSize(true);

        setupRecyclerView();
    }


    private void setupRecyclerView() {
        mManager = new LinearLayoutManager(getContext());
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
                        Intent intent = new Intent(getActivity(), VendorsActivity.class);
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
