// File: com/cmloopy/quizzi/adapter/DetailTopCollectionAdapter.java
package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;
import com.cmloopy.quizzi.views.QuizzDetails;

import java.util.List;

public class DetailTopCollectionAdapter extends RecyclerView.Adapter<DetailTopCollectionAdapter.QuizViewHolder> {
    private static final String TAG = "DetailTopCollectionAdapter";
    private List<DetailTopCollectionItem> quizList;

    public DetailTopCollectionAdapter(List<DetailTopCollectionItem> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_collection_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        DetailTopCollectionItem quiz = quizList.get(position);

        // Debug log
        Log.d(TAG, "Binding quiz: " + quiz.getTitle() + " with ID: " + quiz.getQuizId());

        holder.imgQuizThumbnail.setImageResource(quiz.getImageResId());
        holder.txtQuizTitle.setText(quiz.getTitle());
        holder.txtQuizAuthor.setText(quiz.getAuthor());
        holder.txtQuizTime.setText(quiz.getTimeAgo());
        holder.txtQuizPlays.setText(quiz.getPlayCount());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, QuizzDetails.class);

                // Truyền quizId qua Intent
                intent.putExtra("quizId", quiz.getQuizId());

                // Debug log
                Log.d(TAG, "Clicked on quiz: " + quiz.getTitle() + " with ID: " + quiz.getQuizId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        ImageView imgQuizThumbnail;
        TextView txtQuizTitle, txtQuizAuthor, txtQuizTime, txtQuizPlays;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            imgQuizThumbnail = itemView.findViewById(R.id.imgQuizThumbnail);
            txtQuizTitle = itemView.findViewById(R.id.txtQuizTitle);
            txtQuizAuthor = itemView.findViewById(R.id.txtQuizAuthor);
            txtQuizTime = itemView.findViewById(R.id.txtQuizTime);
            txtQuizPlays = itemView.findViewById(R.id.txtQuizPlays);
        }
    }
}