package com.cmloopy.quizzi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_Player;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UI_44_ScoreboardAdapter extends RecyclerView.Adapter<UI_44_ScoreboardAdapter.ViewHolder> {

    private List<UI_44_Player> playerList;
    private int currentUserScore;
    private int highlightedPosition = -1;

    public UI_44_ScoreboardAdapter(List<UI_44_Player> playerList, int currentUserScore) {
        this.playerList = playerList;
        this.currentUserScore = currentUserScore;

        // Find player position that matches the current user's score
        // Assuming the second player (index 1) with score 945 is the current user
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getScore() == 945) {
                highlightedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_44_item_scoreboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UI_44_Player player = playerList.get(position);

        // Set rank
        holder.tvRank.setText(String.valueOf(player.getRank()));

        // Set player avatar
        holder.ivPlayerAvatar.setImageResource(player.getAvatarResource());

        // Set player name
        holder.tvPlayerName.setText(player.getName());

        // Set score
        holder.tvScore.setText(String.valueOf(player.getScore()));

        // Highlight the current user's row
        if (position == highlightedPosition) {
            holder.viewBackground.setVisibility(View.VISIBLE);
            holder.tvRank.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.purple_500));
            holder.tvPlayerName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.purple_500));
            holder.tvScore.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.purple_500));
        } else {
            holder.viewBackground.setVisibility(View.GONE);
            holder.tvRank.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.tvPlayerName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
            holder.tvScore.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvPlayerName, tvScore;
        CircleImageView ivPlayerAvatar;
        View viewBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivPlayerAvatar = itemView.findViewById(R.id.ivPlayerAvatar);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvScore = itemView.findViewById(R.id.tvScore);
            viewBackground = itemView.findViewById(R.id.viewBackground);
        }
    }
}