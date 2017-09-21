package com.android.weddingplanner.models;

import android.os.Parcel;
import android.os.Parcelable;


public class BudgetItem implements Parcelable {

    public long itemId;
    public float itemBudjet;
    public String itemName;
    public String itemCategoryId;
    public String itemNotes;
    public String itemCategoryName;

    public BudgetItem() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.itemId);
        dest.writeFloat(this.itemBudjet);
        dest.writeString(this.itemName);
        dest.writeString(this.itemCategoryId);
        dest.writeString(this.itemNotes);
        dest.writeString(this.itemCategoryName);
    }

    protected BudgetItem(Parcel in) {
        this.itemId = in.readLong();
        this.itemBudjet = in.readFloat();
        this.itemName = in.readString();
        this.itemCategoryId = in.readString();
        this.itemNotes = in.readString();
        this.itemCategoryName = in.readString();
    }

    public static final Parcelable.Creator<BudgetItem> CREATOR = new Parcelable.Creator<BudgetItem>() {
        @Override
        public BudgetItem createFromParcel(Parcel source) {
            return new BudgetItem(source);
        }

        @Override
        public BudgetItem[] newArray(int size) {
            return new BudgetItem[size];
        }
    };
}
