package com.android.weddingplanner.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.User;
import com.android.weddingplanner.models.Vendors_categories;
import com.squareup.picasso.Picasso;


public class UsersViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_userFullName;
    public TextView textView_userName;
    public Button button_sendInvite;


    public UsersViewHolder(View itemView) {
        super(itemView);


        textView_userFullName = (TextView) itemView.findViewById(R.id.textView_fullName);
        textView_userName = (TextView) itemView.findViewById(R.id.textView_userName);
        button_sendInvite = (Button) itemView.findViewById(R.id.button_invite);

    }

    public void bindToPost(User user, View.OnClickListener starClickListener) {
        textView_userFullName.setText(user.firstName + " " + user.lastName);
        textView_userName.setText(user.username);
        button_sendInvite.setOnClickListener(starClickListener);


    }
}
