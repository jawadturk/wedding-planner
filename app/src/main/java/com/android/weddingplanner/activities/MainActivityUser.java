package com.android.weddingplanner.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.weddingplanner.R;
import com.android.weddingplanner.fragment.BudgetFragment;
import com.android.weddingplanner.fragment.InviteToWeddingFragment;
import com.android.weddingplanner.fragment.ProfileFragment;
import com.android.weddingplanner.fragment.VendorsCategoriesFragment;
import com.android.weddingplanner.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivityUser extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ImageView imgView;
    private Fragment currentFragment;
    private TextView textView_user_name;
    private TextView textView_user_email;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setUpToolBarAndDrawer();
        setUpViews();
        getSupportFragmentManager().addOnBackStackChangedListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserProfile();
    }

    private void setUpViews() {

        textView_user_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        textView_user_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_email);
        imgView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.image);


        textView_user_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        textView_user_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    private void setUpToolBarAndDrawer() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        //Setup Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_vendor));
        navigationView.setCheckedItem(R.id.nav_vendor);


        getSupportActionBar().setTitle(getString(R.string.title_section1));


    }

    boolean hasBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        return backStackEntryCount > 0;
    }

    private void setupHomeButton() {
        boolean hasBackEntries = hasBackStack();
        if (hasBackEntries) {
            disableDrawer();
        } else {
            enableDrawer();
        }
        if (hasBackEntries) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(hasBackEntries);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            } else {
                actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(hasBackEntries);
            }
        } else if (actionBarDrawerToggle != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);

                }
            });
            actionBarDrawerToggle.setDrawerIndicatorEnabled(!hasBackEntries);
            actionBarDrawerToggle.syncState();
        }

    }

    private void enableDrawer() {
        if (drawer != null) {
            drawer.setEnabled(true);
        }
    }

    private void disableDrawer() {
        if (drawer != null) {
            drawer.setEnabled(false);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.actionBarDrawerToggle != null) {
            this.actionBarDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.actionBarDrawerToggle != null) {
            this.actionBarDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackStackChanged() {
        setupHomeButton();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String title = getString(R.string.app_name);
        switch (id) {
            case R.id.nav_vendor:
                title = getString(R.string.title_section1);
                showVendorsCategoriesFeagment();
                break;
            case R.id.nav_todo:
                Intent intent = new Intent(MainActivityUser.this, TodoActivity.class);
                startActivity(intent);
                navigationView.getMenu().getItem(1).setChecked(false);
//                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
            case R.id.nav_budget:
                title = getString(R.string.title_section3);
                showBudgetFragment();
                break;
            case R.id.nav_invite:
                title = getString(R.string.title_section4);
                showInvitePeopleFragment();
                break;
            case R.id.nav_profile:
                title = getString(R.string.title_section5);
                showProfileFragment();
                break;
            case R.id.nav_logOut:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.nav_admin:

                Intent intentAdmin = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intentAdmin);

                break;

        }
        if (id != R.id.nav_todo) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment instanceof VendorsCategoriesFragment) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

                navigationView.getMenu().getItem(0).setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.title_section1));
                getSupportActionBar().show();
                showVendorsCategoriesFeagment();
                return;
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchContent(Fragment fragment, @Nullable String backStackTag, boolean isAddToBackStack, boolean isShowAnimation) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContent, fragment, fragment.getClass().getSimpleName());
        if (isAddToBackStack) {// add to backstack
            if (TextUtils.isEmpty(backStackTag)) {
                ft.addToBackStack(fragment.getClass().getSimpleName());
            } else {
                ft.addToBackStack(backStackTag);
            }
        }
        if (isShowAnimation) {
            ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, 0, 0, R.anim.abc_shrink_fade_out_from_bottom);
        }

        ft.commit();

        currentFragment = fragment;
    }

    public void switchContent(Fragment fragment, boolean isAddToBackStack, boolean isShowAnimation) {
        switchContent(fragment, null, isAddToBackStack, isShowAnimation);
    }

    public void showVendorsCategoriesFeagment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        VendorsCategoriesFragment vendorsCategoriesFragment = (VendorsCategoriesFragment) supportFragmentManager.findFragmentByTag(VendorsCategoriesFragment.class.getSimpleName());
        if (vendorsCategoriesFragment == null) {
            vendorsCategoriesFragment = new VendorsCategoriesFragment();
            switchContent(vendorsCategoriesFragment, false, true);
        } else {
            supportFragmentManager.popBackStack(VendorsCategoriesFragment.class.getSimpleName(), 0); //or return false/true based on where you are calling from to deny adding
        }
    }

    public void showBudgetFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        BudgetFragment budgetFragment = (BudgetFragment) supportFragmentManager.findFragmentByTag(BudgetFragment.class.getSimpleName());
        if (budgetFragment == null) {
            budgetFragment = new BudgetFragment();
            switchContent(budgetFragment, false, true);
        } else {
            supportFragmentManager.popBackStack(BudgetFragment.class.getSimpleName(), 0); //or return false/true based on where you are calling from to deny adding
        }
    }

    public void showInvitePeopleFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        InviteToWeddingFragment inviteToWeddingFragment = (InviteToWeddingFragment) supportFragmentManager.findFragmentByTag(InviteToWeddingFragment.class.getSimpleName());
        if (inviteToWeddingFragment == null) {
            inviteToWeddingFragment = new InviteToWeddingFragment();
            switchContent(inviteToWeddingFragment, false, true);
        } else {
            supportFragmentManager.popBackStack(InviteToWeddingFragment.class.getSimpleName(), 0); //or return false/true based on where you are calling from to deny adding
        }
    }

    public void showProfileFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ProfileFragment profileFragment = (ProfileFragment) supportFragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
            switchContent(profileFragment, false, true);
        } else {
            supportFragmentManager.popBackStack(ProfileFragment.class.getSimpleName(), 0); //or return false/true based on where you are calling from to deny adding
        }
    }

    private void fetchUserProfile() {
        DatabaseReference globalPostRef = mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        globalPostRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + FirebaseAuth.getInstance().getCurrentUser().getUid() + " is unexpectedly null");
                            Toast.makeText(MainActivityUser.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            textView_user_name.setText(user.firstName + " " + user.lastName);
                            setImage(user.profilePicture);

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());

                    }
                });
    }

    void setImage(String path) {
        if (!TextUtils.isEmpty(path)) {
            Picasso.with(imgView.getContext()).load(path).transform(new CropCircleTransformation()).into(imgView);

        }
    }
}
