package com.example.bookfanapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookfanapp.R;
import com.example.bookfanapp.listeners.CommentClickedListener;
import com.example.bookfanapp.model.Book;
import com.example.bookfanapp.model.Comment;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;
    private final CommentClickedListener commentClickedListener;

    public CommentRecyclerViewAdapter(Context context, List<Comment> commentList, CommentClickedListener commentClickedListener) {
        this.context = context;
        this.commentList = commentList;
        this.commentClickedListener=commentClickedListener;
    }

    @NonNull
    @Override
    public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.comment_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewAdapter.ViewHolder holder, int position) {
        Comment comment=commentList.get(position);
        String  currentImageUrl="";

        holder.name.setText(comment.getUsername());
        holder.data.setText(comment.getDateAdded().toString());
//        Glide.with(context).load(currentImageUrl).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ShapeableImageView imageView;
        private TextView name;
        private TextView data;
        private TextView comment;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context=ctx;

            this.imageView=itemView.findViewById(R.id.image_person_comment);
            this.name=itemView.findViewById(R.id.name_commentRow);
            this.data=itemView.findViewById(R.id.date_commentRow);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Comment currentComment=commentList.get(getAdapterPosition());
            commentClickedListener.commentClicked(currentComment);
        }
    }
}
