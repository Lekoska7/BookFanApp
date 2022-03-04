package com.example.bookfanapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookfanapp.R;
import com.example.bookfanapp.listeners.BookClickedListener;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.model.BookViewModel;
import com.example.bookfanapp.model.Comment;
import com.example.bookfanapp.ui.BookRecyclerViewAdapter;
import com.example.bookfanapp.ui.CommentRecyclerViewAdapter;
import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.List;


public class MyListFragment extends Fragment implements BookClickedListener {
    private RecyclerView rv;
    private RecyclerView commentRv;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    private List<Book> bookList;
    private List<Comment> commentList;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Books");

    private BookViewModel bookViewModel;
    private TextView emptyList;

    public MyListFragment()  {
        // Required empty public constructor
    }

    public static MyListFragment newInstance() {
        MyListFragment fragment = new MyListFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_my_list, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        bookList=new ArrayList<>();
        commentList=new ArrayList<>();
        emptyList=view.findViewById(R.id.empty_list);

        rv=view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        collectionReference
                .whereEqualTo("currentUserId", BookApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot books: queryDocumentSnapshots){
                                Book book=books.toObject(Book.class);
                                bookList.add(book);
                            }
                            bookRecyclerViewAdapter=new BookRecyclerViewAdapter(requireContext(), bookList, MyListFragment.this::bookClicked);
                            rv.setAdapter(bookRecyclerViewAdapter);
                            rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

                            bookRecyclerViewAdapter.notifyDataSetChanged();

                            if(bookList.isEmpty()){
                                emptyList.setVisibility(View.VISIBLE);
                            }else {
                                emptyList.setVisibility(View.INVISIBLE);
                            }

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

    @Override
    public void bookClicked(Book bookclicked) {
        bookViewModel.setSelectedBook(bookclicked);

        String bookId=bookclicked.getDocumentId();
        Log.d("bookId", "bookClicked: " + bookId);
        //1
        bookViewModel.setSelectedDocumentRef(bookId);

        BookApi bookApi=BookApi.getInstance();
        bookApi.setDocumentReference(bookId);


        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.book_fragment, MyBookDetailsFragment.newInstance());
        ft.commit();

    }

}