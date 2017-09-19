package com.android.weddingplanner.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.weddingplanner.R;
import com.android.weddingplanner.models.Invitation;
import com.android.weddingplanner.models.StaticValues;


public class WeddingInvitationViewHolder extends RecyclerView.ViewHolder {

    public TextView textView_senderName;
    public TextView textView_receiverName;
    public ImageView invitationStatus;
    public TextView textView_inviatationContent;
    public LinearLayout linearLayout_buttons;
    public Button buttonAccept;
    public Button buttonReject;


    public WeddingInvitationViewHolder(View itemView) {
        super(itemView);

        textView_senderName = (TextView) itemView.findViewById(R.id.textView_senderName);
        textView_receiverName = (TextView) itemView.findViewById(R.id.textView_receiverName);
        invitationStatus = (ImageView) itemView.findViewById(R.id.imageView_notificationDrawable);
        textView_inviatationContent = (TextView) itemView.findViewById(R.id.textView_notificationContentMessage);
        linearLayout_buttons = (LinearLayout) itemView.findViewById(R.id.linearLayout_buttons);
        buttonAccept = (Button) itemView.findViewById(R.id.button_acceptInvitation);
        buttonReject = (Button) itemView.findViewById(R.id.button_rejectInvitation);


    }

    public void bindToPost(Invitation invitation, boolean showHideEditButtons, View.OnClickListener buttonAcceptClickListener, View.OnClickListener buttonRejectClickListener) {

        if (showHideEditButtons) {
            linearLayout_buttons.setVisibility(View.VISIBLE);
        } else {
            linearLayout_buttons.setVisibility(View.GONE);
        }
        textView_senderName.setText(invitation.senderIdentity);
        textView_receiverName.setText(invitation.receiverIdentity);
        if (invitation.state.equals(StaticValues.acceptedInvitation)) {
            invitationStatus.setImageResource(R.drawable.confirmed_drawable);
        } else if (invitation.state.equals(StaticValues.rejectedInvitation)) {
            invitationStatus.setImageResource(R.drawable.not_confirmed_drawable);
        } else if (invitation.state.equals(StaticValues.pendingInvitation)) {
            invitationStatus.setImageResource(R.drawable.pending_drawable);
        }

        textView_inviatationContent.setText(invitation.senderIdentity + " would like to invite you to the wedding of "
                + invitation.weddingDetails.groomName + " and " + invitation.weddingDetails.brideName
                + " in " + invitation.weddingDetails.weddingLocation + " at " + invitation.weddingDetails.weddingtime);
        buttonAccept.setOnClickListener(buttonAcceptClickListener);
        buttonReject.setOnClickListener(buttonRejectClickListener);

    }
}
