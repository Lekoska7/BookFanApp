package com.example.bookfanapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookfanapp.R;
import com.example.bookfanapp.listeners.CommentClickedListener;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.model.BookViewModel;
import com.example.bookfanapp.model.Comment;
import com.example.bookfanapp.ui.BookRecyclerViewAdapter;
import com.example.bookfanapp.ui.CommentRecyclerViewAdapter;
import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AllBooksDetailFragment extends Fragment implements CommentClickedListener {
    private ImageView imageView;
    private TextView titleDetails;
    private TextView authorDetails;
    private TextView categoryDetails;
    private TextView descriptionDetails;
    private Button submitComment;

    private BookViewModel bookViewModel;

    private String currentUserName;
    private String currentUserId;

    private TextInputEditText editText;
    private TextInputLayout inputLayout;
    private ImageButton imageButton;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private final CollectionReference collectionReference = db.collection("Books");

    private CollectionReference collectionReference=db.collection("Posts");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private final CollectionReference commentCollectionReference = db.collection("Posts");

    private RecyclerView commentRv;
    private List<Comment> commentList;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;

    public AllBooksDetailFragment() {
        // Required empty public constructor
    }

    public static AllBooksDetailFragment newInstance() {
        AllBooksDetailFragment fragment = new AllBooksDetailFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books_detail, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        commentList=new ArrayList<>();

        if (BookApi.getInstance() != null) {
            currentUserName = BookApi.getInstance().getUsername();
            currentUserId = BookApi.getInstance().getUserId();
        }

        commentRv=view.findViewById(R.id.rv_comment);
        commentRv.setHasFixedSize(true);
        commentRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputLayout=view.getRootView().findViewById(R.id.textInput);
        editText = view.getRootView().findViewById(R.id.edit_comment);
        imageView = view.getRootView().findViewById(R.id.bg_image_allBookDetails);
        titleDetails = view.getRootView().findViewById(R.id.title_allBookDetails);
        authorDetails = view.getRootView().findViewById(R.id.author_allBookDetails);
        categoryDetails = view.getRootView().findViewById(R.id.category_AllBookDetails);
        descriptionDetails = view.getRootView().findViewById(R.id.description_allBookDetails);
        submitComment = view.getRootView().findViewById(R.id.submit_comment);
        imageButton=view.getRootView().findViewById(R.id.addComment);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getVisibility()==View.VISIBLE && submitComment.getVisibility()==View.VISIBLE
                &&inputLayout.getVisibility()==View.VISIBLE){
                    editText.setVisibility(View.INVISIBLE);
                    submitComment.setVisibility(View.INVISIBLE);
                    inputLayout.setVisibility(View.INVISIBLE);
                }else{
                    editText.setVisibility(View.VISIBLE);
                    submitComment.setVisibility(View.VISIBLE);
                    inputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveComment();

            }
        });

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        bookViewModel.getSelectedBook().observe(getViewLifecycleOwner(), new Observer<Book>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Book book) {
                Log.d("details", "onChanged: " + book.getBookTitle());

                String imageUrl = book.getImage();

                titleDetails.setText("Ttile: " + book.getBookTitle());
                authorDetails.setText("Details: " + book.getBookAuthor());
                categoryDetails.setText("Category: " + book.getBookCategory());
                descriptionDetails.setText(book.getDescription());

                Glide.with(requireContext()).load(imageUrl).into(imageView);

            }
        });

        collectionReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot comments: queryDocumentSnapshots){
                                Comment comment=comments.toObject(Comment.class);
                                commentList.add(comment);
                            }
                            commentRecyclerViewAdapter=new CommentRecyclerViewAdapter(requireContext(), commentList,AllBooksDetailFragment.this::commentClicked);
                            commentRv.setAdapter(commentRecyclerViewAdapter);
                            commentRv.addItemDecoration(new DividerItemDecoration(commentRv.getContext(), DividerItemDecoration.VERTICAL));

                            commentRecyclerViewAdapter.notifyDataSetChanged();
                        }else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void saveComment() {

        String commentText = editText.getText().toString().trim();

        if (!commentText.isEmpty()) {

            Comment comment = new Comment();

            comment.setComment(commentText);
            comment.setDateAdded(new Timestamp(new Date()));
            comment.setProfileImage(null);
            comment.setUsername(currentUserName);

            commentCollectionReference.add(comment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            String documentRefId = documentReference.getId();
                            documentReference.update("postDocumentId", documentRefId);
                            Toast.makeText(requireContext(), "Comment added!", Toast.LENGTH_SHORT).show();
                            editText.setVisibility(View.INVISIBLE);
                            submitComment.setVisibility(View.INVISIBLE);
                            inputLayout.setVisibility(View.INVISIBLE);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("myBookDetails", "onFailure: " + e.getMessage());
                            Toast.makeText(requireContext(), "Comment was not submitted", Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {
            Toast.makeText(requireActivity(), "Please enter comment", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void commentClicked(Comment commentClicked) {

    }
}