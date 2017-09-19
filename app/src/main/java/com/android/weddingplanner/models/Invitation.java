package com.android.weddingplanner.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Invitation {

    public String senderId;
    public String receiverId;
    public String senderIdentity = "";
    public String receiverIdentity = "";
    public String state = StaticValues.pendingInvitation;
    public WeddingDetails weddingDetails;


    public Invitation() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Invitation(String senderId, String senderIdentity, String receiverId, String receiverIdentity, WeddingDetails weddingDetails) {
        this.senderId = senderId;
        this.senderIdentity = senderIdentity;
        this.receiverId = receiverId;
        this.receiverIdentity = receiverIdentity;
        this.weddingDetails = weddingDetails;

    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("senderId", senderId);
        result.put("senderIdentity", senderIdentity);
        result.put("receiverId", receiverId);
        result.put("receiverIdentity", receiverIdentity);
        result.put("weddingDetails", weddingDetails);
        result.put("state", state);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
