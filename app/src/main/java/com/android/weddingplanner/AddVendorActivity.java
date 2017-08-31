package com.android.weddingplanner;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.weddingplanner.adapter.HorizontalImageListSelectorAdapter;
import com.android.weddingplanner.models.Vendor;
import com.android.weddingplanner.models.Vendors_categories;
import com.android.weddingplanner.service.MyUploadService;
import com.android.weddingplanner.utils.RecyclerTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.liuguangqiang.ipicker.IPicker;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import fr.ganfra.materialspinner.MaterialSpinner;

public class AddVendorActivity extends AppCompatActivity {
    private RecyclerView recyclerView_vendorImages;
    private HorizontalImageListSelectorAdapter mAdapter;
    private DatabaseReference mDatabase;
    private String TAG = AddVendorActivity.class.getSimpleName();
    private ArrayList<Vendors_categories> vendorCategoriesList = new ArrayList<>();
    public String[] vendorsCategories;
    private MaterialSpinner spinner;
    private ArrayAdapter<String> adapter;
    private EditText editText_vendorName;
    private EditText ediText_vendorDetails;
    private ProgressBar progressBar;
    private Button button_save;
    String vendorCategoryId;
    private ProgressDialog mProgressDialog;
    private BroadcastReceiver mBroadcastReceiver;
    ArrayList<String> uploadedImagesUrls = new ArrayList<>();
    private int currentIndex = 0;
    private int numberOfRetriedLeft = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setUpViews();
        retrieveVendorsCategories();
        setupRecyclerView();
        setupBroadCastReceiver();
    }

    private void setupBroadCastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideDialog();

                switch (intent.getAction()) {

                    case MyUploadService.UPLOAD_COMPLETED:
                        Log.d(TAG, "images size" + mAdapter.getImageURLList().size());
                        onUploadResultIntent(intent);


                        break;
                    case MyUploadService.UPLOAD_ERROR:
                        numberOfRetriedLeft--;
                        if (numberOfRetriedLeft == 0) {
                            currentIndex++;
                            numberOfRetriedLeft = 3;
                        }
                        uploadFromUri();
                        break;
                }
            }
        };
    }

    private void setUpViews() {
        spinner = (MaterialSpinner) findViewById(R.id.spinner_vendorsCategories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position >= 0) {

                    vendorCategoryId = vendorCategoriesList.get(position).vendorCategoryId;
                } else {
                    vendorCategoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ediText_vendorDetails = (EditText) findViewById(R.id.field_vendorDetails);
        editText_vendorName = (EditText) findViewById(R.id.field_vendorName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_saving);
        button_save = (Button) findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateForm()) {
                    //start uploading images first
                    if (mAdapter.getImageURLList().size() > 0) {
                        uploadFromUri();
                    } else {
                        Vendor vendor = new Vendor();
                        vendor.vendorDescription = ediText_vendorDetails.getText().toString();
                        vendor.vendorName = editText_vendorName.getText().toString();
                        vendor.vendorPhotos = uploadedImagesUrls;
                        writeNewVendorToDataBase(vendor);
                    }


                } else {

                }

            }
        });

    }

    private void retrieveVendorsCategories() {
        Query postsQuery = getQuery(mDatabase);

        postsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear old data
                vendorCategoriesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Vendors_categories categories = postSnapshot.getValue(Vendors_categories.class);


                    if (!TextUtils.isEmpty(categories.vendorCategoryName) && !TextUtils.isEmpty(categories.vendorCategoryId)) {
                        vendorCategoriesList.add(categories);
                    }
                    vendorsCategories = new String[vendorCategoriesList.size()];
                    for (int i = 0; i < vendorCategoriesList.size(); i++) {
                        vendorsCategories[i] = vendorCategoriesList.get(i).vendorCategoryName;
                    }
                    setUpSpinner();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpSpinner() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vendorsCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPaddingSafe(0, 0, 0, 0);
    }

    private void setupRecyclerView() {
        recyclerView_vendorImages = (RecyclerView) findViewById(R.id.recyclerView_vendorImages);

        IPicker.setLimit(10);
        IPicker.setCropEnable(false);
        IPicker.setOnSelectedListener(new IPicker.OnSelectedListener() {
            @Override
            public void onSelected(final List<String> paths) {

                mAdapter.setImageURLList(paths, AddVendorActivity.this);
                mAdapter.notifyDataSetChanged();

            }
        });
        recyclerView_vendorImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new HorizontalImageListSelectorAdapter();
        mAdapter.setImageURLList(new ArrayList<String>(), this);
        recyclerView_vendorImages.setAdapter(mAdapter);
        recyclerView_vendorImages
                .addOnItemTouchListener(new RecyclerTouchListener(this,
                        recyclerView_vendorImages,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                if (position == mAdapter.getItemCount() - 1) {
                                    IPicker.open(AddVendorActivity.this, (ArrayList<String>) mAdapter.getImageURLList());
                                }


                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                if (position == mAdapter.getItemCount() - 1) {
                                    //do nothing thats image icon
                                } else {


                                }


                            }
                        }));
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Query recentPostsQuery = databaseReference.child("vendors-categories").orderByKey();
        return recentPostsQuery;
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editText_vendorName.getText().toString())) {
            editText_vendorName.setError("Required");
            result = false;
        } else {
            editText_vendorName.setError(null);
        }

        if (TextUtils.isEmpty(ediText_vendorDetails.getText().toString())) {
            ediText_vendorDetails.setError("Required");
            result = false;
        } else {
            ediText_vendorDetails.setError(null);
        }

        if (TextUtils.isEmpty(vendorCategoryId)) {
            spinner.setError("Required");
            result = false;
        } else {
            spinner.setError(null);
        }

        return result;
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        Uri imageUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        uploadedImagesUrls.add(imageUrl.toString());
        Log.d(TAG, "onUploadResultIntent: " + imageUrl);
        currentIndex++;
        numberOfRetriedLeft = 3;
        if (currentIndex <= mAdapter.getImageURLList().size() - 1) {
            uploadFromUri();
        } else {

            Vendor vendor = new Vendor();
            vendor.vendorDescription = ediText_vendorDetails.getText().toString();
            vendor.vendorName = editText_vendorName.getText().toString();
            vendor.vendorPhotos = uploadedImagesUrls;
            writeNewVendorToDataBase(vendor);
        }


    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    private void uploadFromUri() {
        File file = new File(mAdapter.getImageURLList().get(currentIndex));
        Uri uri = Uri.fromFile(file);
        Log.d(TAG, "uploadFromUri:src:" + uri.toString());
        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, uri)
                .putExtra(MyUploadService.EXTRA_FILE_DIRECTOY_NAME, "vendors_photos")
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void writeNewVendorToDataBase(Vendor vendor) {
        String key = mDatabase.child("vendors").push().getKey();
        vendor.vendorId = key;

        Map<String, Object> vendorValues = vendor.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/vendors/" + key, vendorValues);


        mDatabase.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Toast.makeText(AddVendorActivity.this, "Insert Vendor Completed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
