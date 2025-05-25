package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeAuthorAdapter extends RecyclerView.Adapter<HomeAuthorAdapter.ViewHolder> {
    private List<User> items;
    private int userId;
    private Context context;

    public HomeAuthorAdapter(List<User> items, Context context, int userId) {
        this.items = items;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = items.get(position);

        Glide.with(context).load(user.getAvatar()).into(holder.img_author);
        holder.txt_name_author.setText(user.getFullName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, AuthorDetailsActivity.class);
                intent.putExtra("authorId", user.getId());
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView img_author;
        MaterialTextView txt_name_author;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            img_author = itemView.findViewById(R.id.img_author);
            txt_name_author = itemView.findViewById(R.id.txt_nameauthor);
        }
    }
    public void setData(List<User> users){
        items = users;
        notifyDataSetChanged();
    }
}