package com.android.weddingplanner.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.UserGifts;
import com.android.weddingplanner.service.MyUploadService;
import com.android.weddingplanner.viewholder.GiftsViewHolder;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.ipicker.IPicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends BaseActivity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private ImageView userProfileImage;
    private TextView userName;
    private TextView userEmail;

    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<UserGifts, GiftsViewHolder> mAdapter;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        setUpToolBar();
        setUpViews();


        if (getIntent() != null) {
            userId = getIntent().getStringExtra("userId");
            retrieveUserData();
            setupRecyclerView();
        }
    }

    private void setUpViews() {
        userProfileImage = (ImageView) findViewById(R.id.profile_image);
        userEmail = (TextView) findViewById(R.id.textView_email);
        userName = (TextView) findViewById(R.id.textView_name);

    }

    private void retrieveUserData() {


        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(UserProfileActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            userName.setText(user.firstName + " " + user.lastName);
                            userEmail.setText(user.email);
                            if (!TextUtils.isEmpty(user.profilePicture)) {
                                Picasso.with(userProfileImage.getContext()).load(user.profilePicture).into(userProfileImage);

                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
        // [END single_value_read]
    }


    private void setupRecyclerView() {
        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_wantedGifts);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<UserGifts, GiftsViewHolder>(UserGifts.class, R.layout.list_item_gift,
                GiftsViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final GiftsViewHolder viewHolder, final UserGifts model, final int position) {
                final DatabaseReference postRef = getRef(position);

                final String giftId = postRef.getKey();


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!model.purchsed) {
                            updateGiftState(giftId);
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Some one else already purchased this.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query recentInvitationsQuery = databaseReference.child("user-gifts").orderByChild("userId").equalTo(userId);
        return recentInvitationsQuery;
    }

    private void setUpToolBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
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

    private void updateGiftState(String giftId) {

        DatabaseReference globalPostRef = mDatabase.child("user-gifts").child(giftId);

        globalPostRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                UserGifts gift = mutableData.getValue(UserGifts.class);
                if (gift == null) {
                    return Transaction.success(mutableData);
                }

                gift.purchsed = true;
                // Set value and report transaction success
                mutableData.setValue(gift);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Toast.makeText(UserProfileActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }
}
