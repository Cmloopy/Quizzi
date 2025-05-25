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
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.cmloopy.quizzi.views.QuizzDetails;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private List<QuizResponse> items;
    private int userId;

    public QuizAdapter(List<QuizResponse> items, int userId) {
        this.items = items;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResponse quiz = items.get(position);

        holder.titleText.setText(quiz.getTitle());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, QuizzDetails.class);
                intent.putExtra("quizId", items.get(position).getId());
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });

        View.OnClickListener authorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, AuthorDetailsActivity.class);
                context.startActivity(intent);
            }
        };
        holder.authorAvatar.setOnClickListener(authorClickListener);
        holder.authorName.setOnClickListener(authorClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView podcastImage;
        TextView titleText;
        TextView dateAndPlaysText;
        ImageView authorAvatar;
        TextView authorName;
        TextView questionsText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            podcastImage = itemView.findViewById(R.id.podcast_image);
            titleText = itemView.findViewById(R.id.title_text);
            dateAndPlaysText = itemView.findViewById(R.id.date_plays_text);
            authorAvatar = itemView.findViewById(R.id.author_avatar);
            authorName = itemView.findViewById(R.id.author_name);
            questionsText = itemView.findViewById(R.id.questions_text);
        }
    }
    public void setData(List<QuizResponse> ne){
        items = ne;
        notifyDataSetChanged();
    }
}