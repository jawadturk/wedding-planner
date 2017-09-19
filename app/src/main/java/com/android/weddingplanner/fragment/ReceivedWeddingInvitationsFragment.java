package com.android.weddingplanner.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.weddingplanner.R;
import com.android.weddingplanner.helper.GcmManagerManager;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.StaticValues;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.viewholder.WeddingInvitationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

public class ReceivedWeddingInvitationsFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Invitation, WeddingInvitationViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootview;
    private static final String TAG = ReceivedWeddingInvitationsFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_wedding_invitations, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootview.findViewById(R.id.recyclerView_weddingInvitations);
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
        mAdapter = new FirebaseRecyclerAdapter<Invitation, WeddingInvitationViewHolder>(Invitation.class, R.layout.list_wedding_invitations,
                WeddingInvitationViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final WeddingInvitationViewHolder viewHolder, final Invitation model, final int position) {
                final DatabaseReference postRef = getRef(position);

                final String invitationId = postRef.getKey();


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateInvitationState(invitationId, StaticValues.acceptedInvitation);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateInvitationState(invitationId, StaticValues.rejectedInvitation);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query recentInvitationsQuery = databaseReference.child("invitations").orderByChild("receiverId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        return recentInvitationsQuery;
    }

    private void updateInvitationState(String invitationId, final String state) {

        DatabaseReference globalPostRef = mDatabase.child("invitations").child(invitationId);

        globalPostRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Invitation invitation = mutableData.getValue(Invitation.class);
                if (invitation == null) {
                    return Transaction.success(mutableData);
                }

                invitation.state = state;
                // Set value and report transaction success
                mutableData.setValue(invitation);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }

}
