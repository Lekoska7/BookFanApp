//package com.example.bookfanapp.fragments;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.example.bookfanapp.MyProfile;
//import com.example.bookfanapp.R;
//import com.example.bookfanapp.model.Book;
//import com.example.bookfanapp.model.BookViewModel;
//import com.example.bookfanapp.util.BookApi;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//
//public class EditProfile extends Fragment {
//    private ShapeableImageView profileImage;
//    private ImageView editImage;
//    private EditText editUsername;
//    private Button updateProfile;
//
//    private FirebaseFirestore db=FirebaseFirestore.getInstance();
//    private CollectionReference collectionReference=db.collection("Users");
//    private StorageReference storageReference;
//
//
//    private BookViewModel bookViewModel;
//    private Uri imageUri;
//    private String currentUserId;
//
//
//    public EditProfile() {
//    }
//
//
//    public static EditProfile newInstance() {
//        EditProfile fragment = new EditProfile();
//
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view=inflater.inflate(R.layout.fragment_edit_profile, container, false);
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        profileImage=view.getRootView().findViewById(R.id.image_person_ep);
//        editImage=view.getRootView().findViewById(R.id.addImage_edit_bt);
//        editUsername=view.getRootView().findViewById(R.id.username_editProfile);
//        updateProfile=view.getRootView().findViewById(R.id.update_profile);
//
//        BookApi bookApi=new BookApi();
//        if(bookApi!=null){
//            currentUserId = bookApi.getUserId();
//        }
//
//        storageReference= FirebaseStorage.getInstance().getReference();
//        bookViewModel=new ViewModelProvider(requireActivity()).get(BookViewModel.class);
//        bookViewModel.getSelectedBook().observe(getViewLifecycleOwner(), new Observer<Book>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onChanged(Book book) {
//                editUsername.setText(book.getCurrentUserName());
//            }
//        });
//
//        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK) {
//                            Intent data = result.getData();
//
//                            if(data!=null){
//                                imageUri=data.getData();
//                                profileImage.setImageURI(imageUri);
//                            }
//                        }
//                    }
//                });
//
//        editImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                someActivityResultLauncher.launch(intent);
//            }
//        });
//
//        updateProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateProfileAccount();
//
//            }
//        });
//
//    }
//
//    private void updateProfileAccount(){
//        String newUsername=editUsername.getText().toString().trim();
//        if(!newUsername.isEmpty()&&
//        imageUri!=null){
//
//            final StorageReference filePath=storageReference
//                    .child("profileImages")
//                    .child("my_image"+currentUserId);
//
//            filePath.putFile(imageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String imageUrl =uri.toString();
//                                    String newUsername=editUsername.getText().toString().trim();
//
//                                    Intent intent = new Intent(getActivity(), MyProfile.class);
//                                    intent.putExtra("imageUrl", imageUrl);
//                                    intent.putExtra("newUsername", newUsername);
//                                    startActivity(intent);
//
//                                }
//                            });
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//        }
//    }
//}