package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.views.RecommendAuthorActivity;
import com.cmloopy.quizzi.models.RecommendUser;

import java.util.List;

public class RecommendUserAdapter extends RecyclerView.Adapter<RecommendUserAdapter.FriendViewHolder> {
    private List<RecommendUser> friendsList;

    public RecommendUserAdapter(List<RecommendUser> friendsList) {
        this.friendsList = friendsList;
    }

    public List<RecommendUser> getFriendsList() {
        return this.friendsList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_user, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        RecommendUser friend = friendsList.get(position);
        holder.nameTextView.setText(friend.getName());
        holder.profileImageView.setImageResource(friend.getProfileImageResource());

        // Initialize button state
        if (friend.isFollowing()) {
            setFollowingState(holder);
        } else {
            setUnfollowedState(holder);
        }

        holder.followButton.setOnClickListener(v -> {
            if (friend.isFollowing()) {
                // Change to unfollowed state
                friend.setFollowing(false);
                Toast.makeText(holder.itemView.getContext(),
                        "Unfollowed " + friend.getName(), Toast.LENGTH_SHORT).show();
                setUnfollowedState(holder);
            } else {
                // Change to following state
                friend.setFollowing(true);
                Toast.makeText(holder.itemView.getContext(),
                        "Following " + friend.getName(), Toast.LENGTH_SHORT).show();
                setFollowingState(holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, RecommendAuthorActivity.class);
                context.startActivity(intent);
            }
        });

    }

    private void setFollowingState(@NonNull FriendViewHolder holder) {
        holder.followButton.setText("Following");
        holder.followButton.setBackgroundResource(R.drawable.bg_button_following_state);
        holder.followButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple));
    }

    private void setUnfollowedState(@NonNull FriendViewHolder holder) {
        holder.followButton.setText("Follow");
        holder.followButton.setBackgroundResource(R.drawable.bg_button_follow_state);
        holder.followButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        AppCompatButton followButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            followButton = itemView.findViewById(R.id.followButton);
        }
    }
}