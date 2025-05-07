package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.RecommendUser;

import java.util.List;

public class FollowFraAdapter extends RecyclerView.Adapter<FollowFraAdapter.FollowViewHolder> {

    private Context context;
    private List<RecommendUser> userList;

    public FollowFraAdapter(Context context, List<RecommendUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follower, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        RecommendUser user = userList.get(position);
        holder.txtName.setText(user.getName());
        holder.txtUsername.setText("@" + user.getUsername());
    }

    private void updateFollowButton(Button button, boolean isFollowing) {
        if (isFollowing) {
            button.setText("Following");
            button.setBackgroundTintList(context.getResources().getColorStateList(R.color.gray));
        } else {
            button.setText("Follow");
            button.setBackgroundTintList(context.getResources().getColorStateList(R.color.purple));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class FollowViewHolder extends RecyclerView.ViewHolder {
        //ImageView imgProfile;
        TextView txtName, txtUsername;
        //Button btnFollow;

        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            //imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.user_name);
            txtUsername = itemView.findViewById(R.id.user_tag);
            //btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}
