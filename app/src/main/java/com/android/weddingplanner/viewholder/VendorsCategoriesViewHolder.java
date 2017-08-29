package com.android.weddingplanner.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Vendors_categories;
import com.squareup.picasso.Picasso;


public class VendorsCategoriesViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_categoryTitle;
    public ImageView imageView_categoryImage;


    public VendorsCategoriesViewHolder(View itemView) {
        super(itemView);


        textView_categoryTitle = (TextView) itemView.findViewById(R.id.textView_vendorCategoryName);
        imageView_categoryImage = (ImageView) itemView.findViewById(R.id.imageView_categoryImage);

    }

    public void bindToPost(Vendors_categories vendors_categories, View.OnClickListener starClickListener) {
        textView_categoryTitle.setText(vendors_categories.vendorCategoryName);


        if (!TextUtils.isEmpty(vendors_categories.categoryImage))
        {
            Picasso.with(imageView_categoryImage.getContext()).load(vendors_categories.categoryImage).into(imageView_categoryImage);

        }

    }
}
