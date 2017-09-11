package com.android.weddingplanner.todomodule;

import android.content.res.Resources;

import com.android.weddingplanner.R;


public class PreferenceKeys {
    final String night_mode_pref_key;

    public PreferenceKeys(Resources resources) {
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key);
    }
}
