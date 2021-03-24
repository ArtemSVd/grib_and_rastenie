package com.example.gribyandrasteniyamap.view.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.dto.CommentDto;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MViewHolder> {

    @Inject
    public CommentsAdapter() {
    }

    private Context mContext;
    private List<CommentDto> comments;

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }


    public void addComment(CommentDto commentDto) {
        comments.add(commentDto);
        //notifyDataSetChanged();
        notifyItemInserted(comments.size()-1);
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    @NonNull
    @Override
    public CommentsAdapter.MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentView = inflater.inflate(R.layout.comment_layout, parent, false);
        return new CommentsAdapter.MViewHolder(commentView);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.MViewHolder holder, int position) {
        CommentDto comment = comments.get(position);
        holder.username.setText(comment.getUser() != null ? comment.getUser().getName() : "Аnonymous");
        holder.text.setText(comment.getText());
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
        holder.createdDate.setText(comment.getCreatedDate() != null ? formatter.format(comment.getCreatedDate()) : "");
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView text;
        TextView createdDate;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_userName);
            text = itemView.findViewById(R.id.comment_text);
            createdDate = itemView.findViewById(R.id.comment_date);
        }
    }
}
