package com.example.bookfanapp.util;

import android.app.Application;

public class BookApi extends Application {
    private String username;
    private String userId;
    private String documentReference;



    private static BookApi instance;


    public BookApi() {
    }

    public static BookApi getInstance(){
        if(instance==null) {
            instance = new BookApi();
        }
        return instance;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(String documentReference) {
        this.documentReference = documentReference;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
