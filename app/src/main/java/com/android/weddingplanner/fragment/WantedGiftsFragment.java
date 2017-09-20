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
import com.android.weddingplanner.activities.AddNewGiftItemActivity;
import com.android.weddingplanner.adapter.CollapsingSectionedAdapter;
import com.android.weddingplanner.helper.BudgetManager;
import com.android.weddingplanner.helper.DatabaseOps;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.StaticValues;
import com.android.weddingplanner.models.UserGifts;
import com.android.weddingplanner.stickyrecyclerheaders.SectionedRecyclerViewAdapter;
import com.android.weddingplanner.viewholder.GiftsViewHolder;
import com.android.weddingplanner.viewholder.WeddingInvitationViewHolder;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class WantedGiftsFragment extends Fragment {
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<UserGifts, GiftsViewHolder> mAdapter;
    private View rootview;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_gifts, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupRecyclerView();

        rootview.findViewById(R.id.fab_addItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewGiftItemActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setupRecyclerView() {
        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_wantedGifts);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<UserGifts, GiftsViewHolder>(UserGifts.class, R.layout.list_item_gift,
                GiftsViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final GiftsViewHolder viewHolder, final UserGifts model, final int position) {
                final DatabaseReference postRef = getRef(position);

                final String invitationId = postRef.getKey();


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, false, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query recentInvitationsQuery = databaseReference.child("user-gifts").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        return recentInvitationsQuery;
    }


}
