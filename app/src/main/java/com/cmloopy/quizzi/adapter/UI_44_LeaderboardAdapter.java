package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_LeaderboardEntry;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UI_44_LeaderboardAdapter extends RecyclerView.Adapter<UI_44_LeaderboardAdapter.LeaderboardViewHolder> {
    private Context context;
    private List<UI_44_LeaderboardEntry> leaderboardEntries;

    public UI_44_LeaderboardAdapter(Context context, List<UI_44_LeaderboardEntry> leaderboardEntries) {
        this.context = context;
        this.leaderboardEntries = leaderboardEntries;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_44_item_leaderboard_row, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UI_44_LeaderboardEntry entry = leaderboardEntries.get(position);

        // Set rank
        holder.tvRank.setText(String.valueOf(entry.getRank()));

        // Set player avatar
        holder.ivPlayerAvatar.setImageResource(entry.getAvatarResourceId());

        // Set player name
        holder.tvPlayerName.setText(entry.getPlayerName());

        // Set player score
        holder.tvPlayerScore.setText(String.format("%,d", entry.getScore()));
    }

    @Override
    public int getItemCount() {
        return leaderboardEntries.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvPlayerName, tvPlayerScore;
        CircleImageView ivPlayerAvatar;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRank = itemView.findViewById(R.id.tvRank);
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerScore = itemView.findViewById(R.id.tvPlayerScore);
            ivPlayerAvatar = itemView.findViewById(R.id.ivPlayerAvatar);
        }
    }
}