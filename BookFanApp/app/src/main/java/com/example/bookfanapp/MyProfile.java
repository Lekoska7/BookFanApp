package com.example.bookfanapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyProfile extends AppCompatActivity {
    private ShapeableImageView profileImage;
    private TextView nameProfile;
    private TextView emailProfile;
    private TextView membershipDate;
    private TextView numberOfPosts;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private StorageReference storageRef;

    private String currentUsername;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        storageRef = FirebaseStorage.getInstance().getReference();

        profileImage=findViewById(R.id.image_person);
        nameProfile=findViewById(R.id.username_ep);
        emailProfile=findViewById(R.id.email_ep);
        membershipDate=findViewById(R.id.membership_date);
        numberOfPosts=findViewById(R.id.number_of_posts);
        ImageButton back = findViewById(R.id.arrow_back);
        ImageButton edit = findViewById(R.id.edit_my_profile);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfile.this, EditActivity.class));

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfile.this, BaseActivity.class));
            }
        });

        BookApi bookApi=BookApi.getInstance();
        if(bookApi!=null){
            currentUsername = bookApi.getUsername();
            currentUserId=bookApi.getUserId();
        }

        getImageFromFirebase();
    }

    private void getImageFromFirebase(){
        storageRef.child("profileImages/my_image"+currentUserId)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl=uri.toString();
                        Log.d("MyProfileImage", "onSuccess: " + imageUrl);

                        Glide.with(MyProfile.this).load(imageUrl).into(profileImage);
                        nameProfile.setText(currentUsername);
                    }
                })
                .addOnFailureListener(e -> Log.d("MyProfile", "onFailure: " +e.getMessage()));
    }
}