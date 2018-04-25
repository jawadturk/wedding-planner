package com.android.weddingplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.UserProfileActivity;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.WeddingDetails;
import com.android.weddingplanner.viewholder.UsersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InviteToWeddingFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<User, UsersViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootview;
    private static final String TAG = InviteToWeddingFragment.class.getSimpleName();
    WeddingDetails weddingDetails;
    String weddingKey;
    User user;

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

        fetchUserProfile();
        setupRecyclerView();
        fetchWeddingSavedData();


    }

    private void fetchUserProfile() {

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " is unexpectedly null");
                            Toast.makeText(getContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {


                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
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
                final String userId = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);

                    }
                });


                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        if (weddingDetails != null) {

                            inviteToWedding(new Invitation(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.firstName + " " + user.lastName, userId, model.firstName + " " + model.lastName, weddingDetails), model.gcm);
                        } else {
                            Toast.makeText(getContext(), "Please fill wedding details first in profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseReference) {

        //fetch users
        Query recentPostsQuery = databaseReference.child("users");


        return recentPostsQuery;
    }

    void fetchWeddingSavedData() {
        mDatabase.child("weddings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        weddingDetails = dataSnapshot.getValue(WeddingDetails.class);
                        weddingKey = dataSnapshot.getKey();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
    }

    private void inviteToWedding(final Invitation invitation, final String gcm) {


        String key = mDatabase.child("invitations").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/invitations/" + key, invitation.toMap());

        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendFCMPush(invitation, gcm);
            }
        });
    }

    private void sendFCMPush(Invitation invitation, String receiverKey) {

        final String Legacy_SERVER_KEY = "AAAA8IhBP0o:APA91bG7a6D62ewGLGG1mz2JNLHHcvrtPthGMmn-jaqAnPDaN7ROR77fg31isF710CVqpg6qDyw7pnK7_4DN-XklmYSMPy2uYzrFIfA5iNzmEF9oEaI0ycruzYQgKhANCNpJbVOjEVdC";
        String msg = invitation.senderIdentity + " would like to invite you to the wedding of "
                + invitation.weddingDetails.groomName + " and " + invitation.weddingDetails.brideName
                + " in " + invitation.weddingDetails.weddingLocation + " at " + invitation.weddingDetails.weddingtime;
        String title = "Wedding Invitation";
        String token = receiverKey;

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("sound", "default");
            objData.put("icon", "icon_name"); //   icon_name image must be there in drawable
            objData.put("tag", token);
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("message", msg);
            dataobjData.put("title", title);

            obj.put("to", token);
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}
