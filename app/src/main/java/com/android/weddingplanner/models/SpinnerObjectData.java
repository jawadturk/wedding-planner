package com.android.weddingplanner.models;

import android.util.Log;


import java.util.ArrayList;
import java.util.List;


public class SpinnerObjectData {
    String id;
    String text = "";

    public SpinnerObjectData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof SpinnerObjectData) {
            SpinnerObjectData spinnerObjectData = (SpinnerObjectData) o;
            return id.equals(spinnerObjectData.getId());
        }
        return false;
    }


}
