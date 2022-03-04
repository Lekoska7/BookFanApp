package com.example.bookfanapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class LogIn extends AppCompatActivity {

    private AutoCompleteTextView email;
    private EditText password;
    private Button logIn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email=findViewById(R.id.email_logIn);
        password=findViewById(R.id.password_logIn);
        logIn=findViewById(R.id.signIn_LogIn);

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser=firebaseAuth.getCurrentUser();
            }
        };

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail=email.getText().toString().trim();
                String newPass=password.getText().toString().trim();

                if(!newEmail.isEmpty()&&
                        !newPass.isEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(newEmail, newPass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    currentUser=firebaseAuth.getCurrentUser();
                                    String currentUserId=currentUser.getUid();

                                    collectionReference
                                            .whereEqualTo("userId", currentUserId)
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                    if(value!=null){
                                                        for (DocumentSnapshot snapshots: value){
                                                            BookApi bookApi =BookApi.getInstance();
                                                            bookApi.setUsername(snapshots.getString("username"));
                                                            bookApi.setUserId(snapshots.getString("userId"));
                                                        }

                                                        startActivity(new Intent(LogIn.this, MyProfile.class));
                                                    }
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(e -> Log.d("Login", "onFailure: " +e.getMessage()));

                    startActivity(new Intent(LogIn.this, BookPostActivity.class));
                }else {
                    Toast.makeText(LogIn.this, "Please fill the empty space",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}