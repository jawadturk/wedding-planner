package com.android.weddingplanner.application;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.facebook.stetho.Stetho;


//import timber.log.Timber;

/**
 * Created by mostafa.samhoury on 12/17/2016.
 */

public class ApplicationClass extends Application {

    private static ApplicationClass mInstance;

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }

    public static boolean hasNetwork() {
        return mInstance.checkIfHasNetwork();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        Stetho.initializeWithDefaults(this);


    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
