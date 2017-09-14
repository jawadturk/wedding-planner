package com.android.weddingplanner.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.AddBudgetItemActivity;
import com.android.weddingplanner.activities.VendorsActivity;
import com.android.weddingplanner.adapter.CollapsingSectionedAdapter;
import com.android.weddingplanner.helper.BudgetManager;
import com.android.weddingplanner.helper.DatabaseOps;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.Vendors_categories;
import com.android.weddingplanner.stickyrecyclerheaders.SectionedRecyclerViewAdapter;
import com.android.weddingplanner.viewholder.UsersViewHolder;
import com.android.weddingplanner.viewholder.VendorsCategoriesViewHolder;
import com.android.weddingplanner.viewmodel.BudgetItemView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class InviteToWeddingFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<User, UsersViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootview;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_invite_to_wedding, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_listOfFriends);
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
        mAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(User.class, R.layout.list_item_users,
                UsersViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder, final User model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String categoryKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


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
        Query recentPostsQuery = databaseReference.child("users");
        // [END recent_posts_query]

        return recentPostsQuery;
    }


}
