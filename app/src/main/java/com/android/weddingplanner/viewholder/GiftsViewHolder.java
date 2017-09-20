package com.android.weddingplanner.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.StaticValues;
import com.android.weddingplanner.models.UserGifts;
import com.github.mikephil.charting.utils.Utils;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class GiftsViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_giftTitle;
    public TextView textView_giftDescription;
    public TextView textView_giftStatus;
    public ImageView giftImage;
    public ImageView gift_indicator;
    public Button button_buyGift;


    public GiftsViewHolder(View itemView) {
        super(itemView);

        textView_giftTitle = (TextView) itemView.findViewById(R.id.textView_giftName);
        textView_giftDescription = (TextView) itemView.findViewById(R.id.textView_giftDescription);
        textView_giftStatus = (TextView) itemView.findViewById(R.id.textView_giftStatus);
        giftImage = (ImageView) itemView.findViewById(R.id.myImageView);
        gift_indicator = (ImageView) itemView.findViewById(R.id.imageView_indicator);

        button_buyGift = (Button) itemView.findViewById(R.id.button_buyGift);


    }

    public void bindToPost(UserGifts userGifts, boolean buyGift, View.OnClickListener buttonBuyGiftListener) {

        if (buyGift) {
            button_buyGift.setVisibility(View.VISIBLE);
        } else {
            button_buyGift.setVisibility(View.GONE);
        }
        textView_giftTitle.setText(userGifts.giftTitle);
        textView_giftDescription.setText(userGifts.giftStoreDetails);
        if (userGifts.purchsed) {
            textView_giftStatus.setText("Purchased");
            gift_indicator.setImageResource(R.color.green);
        } else {
            textView_giftStatus.setText("Not Purchased");
            gift_indicator.setImageResource(R.color.squarecamera__red);
        }

        if (!TextUtils.isEmpty(userGifts.giftImage)) {
            Picasso.with(giftImage.getContext()).load(userGifts.giftImage)
                    .resize((int) Utils.convertDpToPixel(130),
                            (int) Utils.convertDpToPixel(150))
                    .into(giftImage);

        }


        button_buyGift.setOnClickListener(buttonBuyGiftListener);


    }
}
