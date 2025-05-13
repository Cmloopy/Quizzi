package com.cmloopy.quizzi.adapter.playquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.question.puzzle.PuzzlePiece;
import com.cmloopy.quizzi.models.tracking.QuizGameTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FinalBoardScoreAdapter extends RecyclerView.Adapter<FinalBoardScoreAdapter.FinalViewHolder> {
    public List<QuizGameTracking> answerList;
    public List<String> namee = Arrays.asList("Arda", "Guller", "Gun", "Chestt", "Tomasl", "Chels", "Host", "Goyed");
    private Context context;
    public FinalBoardScoreAdapter(Context context, List<QuizGameTracking> answerList){
        this.context = context;
        this.answerList = answerList;
    }
    @NonNull
    @Override
    public FinalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_44_item_leaderboard_row, parent, false);
        return new FinalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FinalViewHolder holder, int position) {
        holder.tvRank.setText((position + 4)+"");
        holder.name.setText(namee.get(position));
        holder.score.setText(answerList.get(position).getTotalPoints() + "");
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public static class FinalViewHolder extends RecyclerView.ViewHolder {

        TextView tvRank;
        TextView name;
        TextView score;

        public FinalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            name = itemView.findViewById(R.id.tvPlayerName);
            score = itemView.findViewById(R.id.tvPlayerScore);
        }
    }
}
