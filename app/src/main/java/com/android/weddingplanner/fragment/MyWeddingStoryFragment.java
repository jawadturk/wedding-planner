package com.android.weddingplanner.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.activities.AddBudgetItemActivity;
import com.android.weddingplanner.activities.AddVendorActivity;
import com.android.weddingplanner.adapter.CollapsingSectionedAdapter;
import com.android.weddingplanner.helper.BudgetManager;
import com.android.weddingplanner.helper.DatabaseOps;
import com.android.weddingplanner.models.BudgetByCategoryItem;
import com.android.weddingplanner.models.BudgetItem;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.Vendor;
import com.android.weddingplanner.models.WeddingDetails;
import com.android.weddingplanner.stickyrecyclerheaders.SectionedRecyclerViewAdapter;
import com.android.weddingplanner.viewmodel.BudgetItemView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyWeddingStoryFragment extends Fragment {
    private static final String TAG = MyWeddingStoryFragment.class.getSimpleName();
    private RecyclerView mRecycler;

    private View rootview;
    private TextInputLayout textInputLayout_groomName;
    private TextInputLayout textInputLayout_brideName;
    private TextInputLayout textInputLayout_weddingLocation;
    private TextInputLayout textInputLayout_weddingTime;
    private Button button_save;
    private ProgressBar progressBar_saving;
    private DatabaseReference mDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_my_wedding_story, container, false);
        return rootview;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setUpViews();
        fetchWeddingSavedData();

    }

    private void setUpViews() {

        textInputLayout_groomName = (TextInputLayout) rootview.findViewById(R.id.textInput_groomName);
        textInputLayout_brideName = (TextInputLayout) rootview.findViewById(R.id.textInput_brideName);
        textInputLayout_weddingLocation = (TextInputLayout) rootview.findViewById(R.id.textInput_weddingLocation);
        textInputLayout_weddingTime = (TextInputLayout) rootview.findViewById(R.id.textInput_weddingTime);
        button_save = (Button) rootview.findViewById(R.id.button_save);
        progressBar_saving = (ProgressBar) rootview.findViewById(R.id.progressBar_saving);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateForm()) {
                    WeddingDetails wedding = new WeddingDetails();
                    wedding.groomName = textInputLayout_groomName.getEditText().getText().toString();
                    wedding.brideName = textInputLayout_brideName.getEditText().getText().toString();
                    wedding.weddingLocation = textInputLayout_weddingLocation.getEditText().getText().toString();
                    wedding.weddingtime = textInputLayout_weddingTime.getEditText().getText().toString();

                    writeWeddingDetailsToDatabase(wedding);

                }
            }
        });

    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(textInputLayout_groomName.getEditText().getText().toString())) {
            textInputLayout_groomName.setError("Required");
            result = false;
        } else {
            textInputLayout_groomName.setError(null);
        }

        if (TextUtils.isEmpty(textInputLayout_brideName.getEditText().getText().toString())) {
            textInputLayout_brideName.setError("Required");
            result = false;
        } else {
            textInputLayout_brideName.setError(null);
        }
        if (TextUtils.isEmpty(textInputLayout_weddingLocation.getEditText().getText().toString())) {
            textInputLayout_weddingLocation.setError("Required");
            result = false;
        } else {
            textInputLayout_weddingLocation.setError(null);
        }

        if (TextUtils.isEmpty(textInputLayout_weddingTime.getEditText().getText().toString())) {
            textInputLayout_weddingTime.setError("Required");
            result = false;
        } else {
            textInputLayout_weddingTime.setError(null);
        }

        return result;
    }

    private void writeWeddingDetailsToDatabase(WeddingDetails weddingDetails) {
        progressBar_saving.setVisibility(View.VISIBLE);

        Map<String, Object> vendorValues = weddingDetails.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/weddings/" + FirebaseAuth.getInstance().getCurrentUser().getUid(), vendorValues);


        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar_saving.setVisibility(View.GONE);
            }
        });
    }

    void fetchWeddingSavedData() {
        mDatabase.child("weddings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        WeddingDetails wedding = dataSnapshot.getValue(WeddingDetails.class);

                        // [START_EXCLUDE]
                        if (wedding == null) {

                        } else {

                            textInputLayout_groomName.getEditText().setText(wedding.groomName);
                            textInputLayout_brideName.getEditText().setText(wedding.brideName);
                            textInputLayout_weddingLocation.getEditText().setText(wedding.weddingLocation);
                            textInputLayout_weddingTime.getEditText().setText(wedding.weddingtime);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
    }
}
