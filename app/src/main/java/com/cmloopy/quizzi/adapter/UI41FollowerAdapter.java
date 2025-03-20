package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI41Follower;

import java.util.List;

public class UI41FollowerAdapter extends RecyclerView.Adapter<UI41FollowerAdapter.ViewHolder> {

    private List<UI41Follower> followerList;
    private Context context;

    public UI41FollowerAdapter(Context context, List<UI41Follower> followerList) {
        this.context = context;
        this.followerList = followerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_41_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UI41Follower follower = followerList.get(position);
        holder.txtUsername.setText(follower.getUsername());

        // Kiểm tra trạng thái Follow ban đầu
        if (follower.isFollowing()) {
            setFollowingState(holder.btnFollow);
        } else {
            setFollowState(holder.btnFollow);
        }

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFollowing = follower.isFollowing();
                follower.setFollowing(!isFollowing); // Đảo trạng thái

                if (follower.isFollowing()) {
                    setFollowingState(holder.btnFollow);
                } else {
                    setFollowState(holder.btnFollow);
                }
            }
        });
    }

    private void setFollowingState(Button button) {
        button.setBackgroundResource(R.drawable.quizz_details_button_friends_background);
        button.setText("Following");
        button.setTextColor(Color.WHITE);
    }

    private void setFollowState(Button button) {
        button.setBackgroundResource(R.drawable.quizz_details_button_solo_background);
        button.setText("Follow");
        button.setTextColor(Color.parseColor("#7A48E3"));
    }

    @Override
    public int getItemCount() {
        return followerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername;
        Button btnFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.ui41Username);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}