package com.example.bookfanapp.model;

import android.widget.ImageView;

import com.google.firebase.Timestamp;

public class Comment {
    private String username;
    private String comment;
    private com.google.firebase.Timestamp dateAdded;
    private String postDocumentId;
    private ImageView profileImage;

    public Comment() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPostDocumentId() {
        return postDocumentId;
    }

    public void setPostDocumentId(String postDocumentId) {
        this.postDocumentId = postDocumentId;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }
}
