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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

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

import java.util.ArrayList;
import java.util.List;

public class AllBooksListFragment extends Fragment implements BookClickedListener, View.OnClickListener {
    private SearchView searchView;
    private Button all,
            romance,
            crime,
            children,
            sciFi,
            other;


    private RecyclerView rv;
    private BookRecyclerViewAdapter bookRecyclerViewAdapter;
    private List<Book> bookList;
    private BookViewModel bookViewModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Books");

    private String selectedFilter="all";
    private String currentSearchText="";
    private String selectedCategory;

    public AllBooksListFragment() {
    }

    public static AllBooksListFragment newInstance(String param1, String param2) {
        AllBooksListFragment fragment = new AllBooksListFragment();

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
        View view=inflater.inflate(R.layout.fragment_all_books_list, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        bookList=new ArrayList<>();
        //2
        searchView=view.findViewById(R.id.search_bt);

        rv=view.findViewById(R.id.rv_allBooksFragment);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        all=view.getRootView().findViewById(R.id.bt_all);
        romance=view.getRootView().findViewById(R.id.romance_bt);
        crime=view.getRootView().findViewById(R.id.crime_bt);
        children=view.getRootView().findViewById(R.id.children_bt);
        sciFi=view.getRootView().findViewById(R.id.sciFi_bt);
        other =view.getRootView().findViewById(R.id.other_bt);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        all.setOnClickListener(this::onClick);
        romance.setOnClickListener(this::onClick);
        crime.setOnClickListener(this::onClick);
        children.setOnClickListener(this::onClick);
        sciFi.setOnClickListener(this::onClick);
        other.setOnClickListener(this::onClick);

        //3
        searchView.setQueryHint("Search title");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Book> filteredList=new ArrayList<>();
                for (Book book : bookList){
                    if(book.getBookTitle().toLowerCase().contains(s.toLowerCase())){
                        filteredList.add(book);
                    }
                }

                BookRecyclerViewAdapter adapter=new BookRecyclerViewAdapter(getContext(),filteredList, AllBooksListFragment.this::bookClicked);
                rv.setAdapter(adapter);
                return false;
            }
        }); //finito

        collectionReference
                .whereNotEqualTo("currentUserId", BookApi.getInstance().getUserId())
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
                            bookRecyclerViewAdapter=new BookRecyclerViewAdapter(requireContext(), bookList,AllBooksListFragment.this::bookClicked);
                            rv.setAdapter(bookRecyclerViewAdapter);
                            rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

                            bookRecyclerViewAdapter.notifyDataSetChanged();
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

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.all_books_list, AllBooksDetailFragment.newInstance());
        ft.commit();
    }


    @Override
    public void onClick(View view) {

        int id=view.getId();
        Bundle bundle = new Bundle();

        switch (id){
            case R.id.bt_all:
                //
                break;
            case R.id.romance_bt:
                selectedCategory="Romance";

                break;
            case R.id.crime_bt:
                selectedCategory= "Crime";

                break;
            case R.id.children_bt:
                selectedCategory="Children";

                break;
            case R.id.sciFi_bt:
                selectedCategory="Sci-fi";

                break;
            case R.id.other_bt:
                selectedCategory="Other";
                break;
            default:
                selectedCategory="Crime";
        }

        filterList(selectedCategory);
    }

    private void filterList(String category){
        List<Book> filteredList2= new ArrayList<>();
        for(Book book : bookList){
            if(book.getBookCategory().toLowerCase().contains(category.toLowerCase())){
                filteredList2.add(book);
            }
        }
        BookRecyclerViewAdapter adapter2=new BookRecyclerViewAdapter(getContext(),filteredList2, AllBooksListFragment.this::bookClicked);
        rv.setAdapter(adapter2);
    }
}