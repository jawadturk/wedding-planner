package com.android.weddingplanner.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Vendor;
import com.squareup.picasso.Picasso;


public class VendorsViewHolder extends RecyclerView.ViewHolder {

    public ImageView vendorThumbnale;
    public TextView vendrName;
    public RatingBar vendorRating;
    public TextView vendorReviews;
    public TextView vendorPlace;
    public CardView cardView;

    public VendorsViewHolder(View itemView) {
        super(itemView);


        vendorThumbnale = (ImageView) itemView.findViewById(R.id.imageView_vendorThumbnail);
        vendrName = (TextView) itemView.findViewById(R.id.textView_vendorName);
        vendorReviews = (TextView) itemView.findViewById(R.id.textView_numberOfReviews);
        vendorPlace = (TextView) itemView.findViewById(R.id.textView_vendorPlace);
        vendorRating = (RatingBar) itemView.findViewById(R.id.ratingBar_vendorRating);
        cardView = (CardView) itemView.findViewById(R.id.vendor_cardview);

    }

    public void bindToPost(Vendor vendor, View.OnClickListener starClickListener) {
        vendrName.setText(vendor.vendorName);
        vendorPlace.setText(vendor.vendorPlace);
        vendorReviews.setText(vendor.vendorReviewsNumber + " Reviews");
        vendorRating.setRating((float) vendor.vendorRating);


        if (vendor.vendorPhotos.size() > 0) {
            Picasso.with(vendorThumbnale.getContext()).load(vendor.vendorPhotos.get(0)).into(vendorThumbnale);

        }


    }
}
