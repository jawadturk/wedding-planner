package com.android.weddingplanner;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.weddingplanner.adapter.ImageViewPagerAdapter;
import com.android.weddingplanner.customcomponents.MyViewPager;
import com.android.weddingplanner.models.Comment;
import com.android.weddingplanner.models.Post;
import com.android.weddingplanner.models.Review;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.Vendor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import at.blogc.android.views.ExpandableTextView;


public class VendorDetailsAvtivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = VendorDetailsAvtivity.class.getSimpleName();

    public static final String EXTRA_VENDOR_KEY = "vendorKey";
    private RecyclerView mRecyclerViewWhosGoing;
    private LinearLayoutManager mVerticallLayoutManager;


    private TextView vendrName;
    private RatingBar vendorRating;
    private TextView vendorReviews;
    private TextView vendorPlace;
    private ExpandableTextView expandableTextView;
    Button buttonToggle;
    private MyViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ImageViewPagerAdapter mAdapter;
    private ArrayList<String> mImageResources = new ArrayList<String>();
    private AppBarLayout app_bar_layout;
    private Toolbar toolbar;


    private Button button_writeReview;
    Handler handler;
    Runnable update;
    private Timer timer;

    private String mVendorKey;

    private DatabaseReference mVendorReference;
    private DatabaseReference mReviewReference;
    private ValueEventListener mVendorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors_details);
        mVendorKey = getIntent().getStringExtra(EXTRA_VENDOR_KEY);
        if (mVendorKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }


        setUpViews();

        setupToolbar();
        setupExpandableTextView();
        setupDataBaseReference();
    }

    private void setupDataBaseReference() {
        mVendorReference = FirebaseDatabase.getInstance().getReference()
                .child("vendors").child(mVendorKey);
        mReviewReference = FirebaseDatabase.getInstance().getReference()
                .child("vendors_reviews").child(mVendorKey);

    }

    private void setupExpandableTextView() {


        // set interpolators for both expanding and collapsing animations
        expandableTextView.setInterpolator(new OvershootInterpolator());


// listen for expand / collapse events
        expandableTextView.setOnExpandListener(new ExpandableTextView.OnExpandListener() {
            @Override
            public void onExpand(final ExpandableTextView view) {

                buttonToggle.setText(R.string.readLess);

            }

            @Override
            public void onCollapse(final ExpandableTextView view) {
                buttonToggle.setText(R.string.read_more);
            }
        });
// toggle the ExpandableTextView
        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                expandableTextView.toggle();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_eventDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setUpViews() {


        vendrName = (TextView) findViewById(R.id.textView_vendorName);
        vendorReviews = (TextView) findViewById(R.id.textView_numberOfReviews);
        vendorPlace = (TextView) findViewById(R.id.textView_vendorPlace);
        vendorRating = (RatingBar) findViewById(R.id.ratingBar_vendorRating);


        toolbar = (Toolbar) findViewById(R.id.toolbar_activity_eventDetails);


        mVerticallLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        expandableTextView = (ExpandableTextView) findViewById(R.id.expandableTextView);
        buttonToggle = (Button) findViewById(R.id.button_toggle);

        intro_images = (MyViewPager) findViewById(R.id.viewPager_eventImages);
        intro_images.setOffscreenPageLimit(100);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);

        button_writeReview = (Button) findViewById(R.id.button_writeReview);
        button_writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWriteReviewDialog();
            }
        });
    }


    private void setupPagerImages(ArrayList<String> images) {

        mImageResources.clear();
        mImageResources = images;
        mAdapter = new ImageViewPagerAdapter(VendorDetailsAvtivity.this, mImageResources);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(this);

        handler = new Handler();
        update = new Runnable() {
            public void run() {
                if (intro_images.getCurrentItem() == 3) {
                    intro_images.setCurrentItem(0, true);
                } else {
                    intro_images.setCurrentItem(intro_images.getCurrentItem() + 1, true);
                }


            }
        };


        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);


        setUiPageViewController();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(update);
        timer.cancel();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            if (i == 0) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.selecteditem_dot));
            }
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);


            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(VendorDetailsAvtivity.this, R.drawable.selecteditem_dot));
    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener vendorListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Vendor vendor = dataSnapshot.getValue(Vendor.class);
                vendrName.setText(vendor.vendorName);
                vendorPlace.setText(vendor.vendorPlace);
                vendorReviews.setText(vendor.vendorReviewsNumber + " Reviews");
                vendorRating.setRating((float) vendor.vendorRating);
                expandableTextView.setText(vendor.vendorDescription);

                setupPagerImages(vendor.vendorPhotos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "vendorLoad:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(VendorDetailsAvtivity.this, "Failed to load vendor",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mVendorReference.addValueEventListener(vendorListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mVendorListener = vendorListener;


    }

    private void showWriteReviewDialog() {
        final Dialog ReviewDialog = new Dialog(this);
        // Set GUI of login screen
        ReviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        ReviewDialog.setCancelable(true);
        ReviewDialog.setContentView(R.layout.layout_add_review);


        // Init button of login GUI

        final EditText editText_review = (EditText) ReviewDialog.findViewById(R.id.editText_review);
        final RatingBar ratingBar = (RatingBar) ReviewDialog.findViewById(R.id.ratingBar_vendorRating);
        Button buttonSave = (Button) ReviewDialog.findViewById(R.id.button_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment(editText_review.getText().toString(), ratingBar.getRating());
                ReviewDialog.dismiss();
            }
        });


        // Make dialog box visible.
        ReviewDialog.show();
    }

    private void postComment(final String reviewText, final float rating) {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new reve=iew object

                        Review review = new Review(uid, authorName, reviewText, rating);

                        // Push the comment, it will appear in the list
                        mReviewReference.push().setValue(review);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
