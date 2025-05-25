package com.cmloopy.quizzi.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.cmloopy.quizzi.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecommendAuthorAdapter extends RecyclerView.Adapter<RecommendAuthorAdapter.FriendViewHolder> {
    private List<User> friendsList;
    private int userId;

    public RecommendAuthorAdapter(List<User> friendsList, int userId) {
        this.friendsList = friendsList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_author, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User friend = friendsList.get(position);
        holder.nameTextView.setText(friend.getFullName());
        holder.usernameTextView.setText("@" + friend.getUsername());
        Picasso.get().load(friend.getAvatar()).into(holder.profileImageView);
        holder.itemView.setOnClickListener(v->
        {
            Intent intent = new Intent(holder.itemView.getContext(), AuthorDetailsActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("authorId", friend.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView usernameTextView;
        Button followButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followButton = itemView.findViewById(R.id.followButton);
        }
    }
    public void setData(List<User> usersss)
    {
        friendsList = usersss;
        notifyDataSetChanged();
    }
}