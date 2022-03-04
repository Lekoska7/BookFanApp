package com.example.bookfanapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookfanapp.R;
import com.example.bookfanapp.listeners.BookClickedListener;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.util.BookApi;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private List<Book> bookList;
    private List<Book> allBooks;
    private final BookClickedListener bookClickedListener;


    public BookRecyclerViewAdapter(Context context, List<Book> bookList, BookClickedListener bookClickedListener) {
        this.context = context;
        this.bookList = bookList;
        this.bookClickedListener = bookClickedListener;
    }

    @NonNull
    @Override
    public BookRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.book_row, parent, false);
        return new ViewHolder(view, context);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BookRecyclerViewAdapter.ViewHolder holder, int position) {
        Book book=bookList.get(position);
        String imageUrl=book.getImage();
        Log.d("Url", "onBindViewHolder: " + imageUrl);

        holder.title.setText("Title: " + book.getBookTitle());
        holder.author.setText("Author: " + book.getBookAuthor());
        holder.category.setText("Category: " +  book.getBookCategory());
        holder.name.setText("User: " + book.getCurrentUserName());

        Glide.with(context).load(imageUrl).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image;
        public TextView title;
        private TextView author;
        private TextView category;
        private TextView name;

        BookClickedListener onBookClickedListener;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context=ctx;

            this.image=itemView.findViewById(R.id.bg_image_row);
            this.title=itemView.findViewById(R.id.title_row);
            this.author=itemView.findViewById(R.id.author_row);
            this.category=itemView.findViewById(R.id.category_row);
            this.onBookClickedListener = BookRecyclerViewAdapter.this.bookClickedListener;
            this.name=itemView.findViewById(R.id.name_row);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Book currBook = bookList.get(getAdapterPosition());
            bookClickedListener.bookClicked(currBook);
        }
    }
}
