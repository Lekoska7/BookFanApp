package com.example.bookfanapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookfanapp.BaseActivity;
import com.example.bookfanapp.BookPostActivity;
import com.example.bookfanapp.R;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.model.BookViewModel;
import com.example.bookfanapp.util.BookApi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyBookDetailsFragment extends Fragment{
    private BookViewModel bookViewModel;
    private ImageView imageView;
    private TextView titleDetails;
    private TextView authorDetails;
    private TextView categoryDetails;
    private TextView descriptionDetails;
    private ImageButton addComment;

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Books");

    private String imageUrl;
    private String title;
    private String author;
    private String description;
    private String category;
    private String currentDocRef;


    public MyBookDetailsFragment() {
    }

    public static MyBookDetailsFragment newInstance() {
        MyBookDetailsFragment fragment = new MyBookDetailsFragment();

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

        View view=inflater.inflate(R.layout.fragment_my_book_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(BookApi.getInstance()!=null) {
            currentDocRef=BookApi.getInstance().getDocumentReference();
        }

        imageView=view.getRootView().findViewById(R.id.bg_image_details);
        titleDetails=view.getRootView().findViewById(R.id.title_details);
        authorDetails=view.getRootView().findViewById(R.id.author_details);
        categoryDetails=view.getRootView().findViewById(R.id.category_details);
        descriptionDetails=view.getRootView().findViewById(R.id.description_details);

        bookViewModel=new ViewModelProvider(requireActivity()).get(BookViewModel.class);

        bookViewModel.getSelectedBook().observe(getViewLifecycleOwner(), new Observer<Book>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(Book book) {
                Log.d("details", "onChanged: " +book.getBookTitle());

                imageUrl=book.getImage();
                title=book.getBookTitle();
                author=book.getBookAuthor();
                description=book.getDescription();
                category=book.getBookCategory();

                titleDetails.setText("TITLE: " +  title);
                authorDetails.setText("AUTHOR: " + author);
                categoryDetails.setText("CATEGORY: " + category);
                descriptionDetails.setText(description);

                Glide.with(requireContext()).load(imageUrl).into(imageView);

            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu2, menu);
        menu.findItem(R.id.add_bt).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        //prv za edit
        if(id==R.id.edit_menu_bt){
            Intent intent=new Intent();
            intent.putExtra("imageView", imageUrl);
            intent.putExtra("title", title);
            intent.putExtra("author", author);
            intent.putExtra("description", description);
            intent.setClass(getActivity(), BookPostActivity.class);
            getActivity().startActivity(intent);
        }
        //samo ovoj cekor za delete
        if(id==R.id.delete_menu_bt){
            collectionReference.document(currentDocRef)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "The post is deleted", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(getActivity(), BaseActivity.class);
                            startActivity(intent);
                        }
                    });
        }



        return super.onOptionsItemSelected(item);
    }
}