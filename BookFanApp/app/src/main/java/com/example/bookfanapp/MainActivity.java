package com.example.bookfanapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.bookfanapp.util.BookApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView logo;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
                if(currentUser!=null){
                    currentUser=firebaseAuth.getCurrentUser();
                    String userId=currentUser.getUid();

                    collectionReference
                            .whereEqualTo("userId", userId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(error!=null){

                                    }

                                    if(!value.isEmpty()){
                                        for(QueryDocumentSnapshot documentSnapshot : value){
                                            BookApi bookApi=BookApi.getInstance();
                                            bookApi.setUserId(documentSnapshot.getString("userId"));
                                            bookApi.setUsername(documentSnapshot.getString("username"));
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(MainActivity.this, BaseActivity.class));
                                            }
                                        }, 1000);


//                                        finish();
                                    }

                                }
                            });

                }else{

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(MainActivity.this, CreateAccount.class));
                        }
                    }, 1000);

                }
            }
        };

        logo=findViewById(R.id.logo);




    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}