package com.example.bookfanapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity {

    private Button createAccount;
    private Button logIn;
    private EditText email;
    private EditText username;
    private EditText password;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        email=findViewById(R.id.email_acc);
        username=findViewById(R.id.username_acc);
        password=findViewById(R.id.password_acc);
        progressBar=findViewById(R.id.progressBar_acc);
        createAccount=findViewById(R.id.createAccount);
        logIn=findViewById(R.id.signIn);

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
                startActivity(new Intent(CreateAccount.this, LogIn.class));
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                String e_mail=email.getText().toString().trim();
                String pass=password.getText().toString().trim();
                String username1=username.getText().toString().trim();

                if(!TextUtils.isEmpty(e_mail)&&
                        !TextUtils.isEmpty(pass)&&
                        !TextUtils.isEmpty(username1)){

                    createAccountWithEmailAndPass(e_mail, pass, username1);
                }

            }
        });
    }

    private void createAccountWithEmailAndPass(String email, String pass, String username){
        if(!TextUtils.isEmpty(email)&&
                !TextUtils.isEmpty(pass)&&
                !TextUtils.isEmpty(username)){

            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                currentUser=firebaseAuth.getCurrentUser();
                                String currentUserId=currentUser.getUid();

                                Map<String, String> userObj =new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);

                                //kreiraj kolekcija so id od momentalniot korisnik i zadadi go objektot
                                db.collection("Users").document(currentUserId).set(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                DocumentReference documentReference=collectionReference.document(currentUserId);
                                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        BookApi bookApi=BookApi.getInstance();
                                                        bookApi.setUserId(value.getString("userId"));
                                                        bookApi.setUsername(value.getString("username"));
                                                    }
                                                });

                                                startActivity(new Intent(CreateAccount.this, BaseActivity.class));

                                            }
                                        });
                            }else {
                                Toast.makeText(CreateAccount.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser=firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}