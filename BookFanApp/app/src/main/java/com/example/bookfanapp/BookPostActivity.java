package com.example.bookfanapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.model.BookViewModel;
import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class BookPostActivity extends AppCompatActivity {

    private ImageView imageBackground;
    private ImageView addImage_bt;
    private EditText titleBookPost;
    private EditText authorBokPost;
    private EditText descriptionBookPost;
    private Button savePost;
    private TextView namePost;
    private Uri dataUri;
    private ProgressBar progressBar;
    private Button editPost;

    private String currentUserName;
    private String currentUserId;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Books");

    private StorageReference storageReference;

    private Spinner category;
    private String selectedItem;
    private Intent intent;
    //2 edit
    private String currentDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_post);

        editPost=findViewById(R.id.edit_button);
        imageBackground = findViewById(R.id.background_image);
        addImage_bt = findViewById(R.id.addImage_bt);
        titleBookPost = findViewById(R.id.bookPost_title);
        authorBokPost = findViewById(R.id.bookPost_author);
        descriptionBookPost = findViewById(R.id.description_et);
        savePost = findViewById(R.id.edit_bt);
        progressBar=findViewById(R.id.progressBar_post);
        namePost = findViewById(R.id.name_post);
        namePost.setText(currentUserName);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                //3
                editBook();
            }
        });

        category=findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.category_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedItem=category.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(BookApi.getInstance()!=null) {
            currentUserName = BookApi.getInstance().getUsername();
            currentUserId = BookApi.getInstance().getUserId();
            //2
            currentDocRef=BookApi.getInstance().getDocumentReference();

        }

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();

                            if(data!=null){
                                dataUri=data.getData();
                                imageBackground.setImageURI(dataUri);
                            }
                        }
                    }
                });


        addImage_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                someActivityResultLauncher.launch(intent);
            }
        });

        savePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                saveBook();
            }
        });

    }

    private void saveBook() {
        String newTitle=titleBookPost.getText().toString().trim();
        String newAuthor=authorBokPost.getText().toString();
        String newDescription=descriptionBookPost.getText().toString().trim();

        if(!newTitle.isEmpty()&&
                !newAuthor.isEmpty()&&
                !selectedItem.isEmpty()&&
                dataUri!=null){

            final StorageReference filePath=storageReference
                    .child("images")
                    .child("my_image"+Timestamp.now().getSeconds());

            filePath.putFile(dataUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl =uri.toString();
                                            Log.d("uri", "onSuccess: " + uri);

                                            Book book=new Book();

                                            book.setBookTitle(newTitle);
                                            book.setBookAuthor(newAuthor);
                                            book.setDescription(newDescription);
                                            book.setBookCategory(selectedItem);
                                            book.setDataAdded(new Timestamp(new Date()));
                                            book.setImage(imageUrl);
                                            book.setCurrentUserId(currentUserId);
                                            book.setCurrentUserName(currentUserName);

                                            collectionReference.add(book)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            progressBar.setVisibility(View.INVISIBLE);

                                                            //1 dodavame vo modelot documentId pole
                                                            //2
                                                            String documentRefId =documentReference.getId();
                                                            documentReference.update("documentId", documentRefId);

                                                            startActivity(new Intent(BookPostActivity.this, BaseActivity.class));

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                    })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }
    //4
    private void editBook() {

        String editedTitle = titleBookPost.getText().toString().trim();
        String editedAuthor = authorBokPost.getText().toString();
        String editedDescription = descriptionBookPost.getText().toString().trim();


        if (!editedTitle.isEmpty() &&
                !editedAuthor.isEmpty() &&
                !selectedItem.isEmpty()) {

            Book book = new Book();

            book.setBookTitle(editedTitle);
            book.setBookAuthor(editedAuthor);
            book.setDescription(editedDescription);
            book.setBookCategory(selectedItem);
            book.setDataAdded(new Timestamp(new Date()));
            book.setImage(intent.getStringExtra("imageView"));
            book.setDocumentId(currentDocRef);
            book.setCurrentUserId(currentUserId);
            book.setCurrentUserName(currentUserName);

            Log.d("bookTitle", "onSuccess: " + book.getBookTitle());

            collectionReference.document(currentDocRef).set(book)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(BookPostActivity.this,"Book successfully updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BookPostActivity.this, BaseActivity.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();

                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        intent =getIntent();


        if(intent!=null) {
            String imageUrl = intent.getStringExtra("imageView");
            String title = intent.getStringExtra("title");
            String author = intent.getStringExtra("author");
            String description = intent.getStringExtra("description");

            titleBookPost.setText(title);
            authorBokPost.setText(author);
            descriptionBookPost.setText(description);

//            savePost.setVisibility(View.GONE);

            if (imageUrl != null) {
                Glide.with(BookPostActivity.this).load(imageUrl).into(imageBackground);
            } else {
                Glide.with(BookPostActivity.this).load(R.drawable.src).into(imageBackground);

            }
        }

    }
}