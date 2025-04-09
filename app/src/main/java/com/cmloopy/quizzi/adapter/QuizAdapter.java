package com.cmloopy.quizzi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private List<Quiz> items;

    public QuizAdapter(List<Quiz> items) {
        this.items = items;
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
        Quiz item = items.get(position);

        holder.podcastImage.setImageResource(item.getImageResource());
        holder.titleText.setText(item.getTitle());
        holder.dateAndPlaysText.setText(item.getDate() + " • " + item.getPlays());
        holder.authorName.setText(item.getAuthor());
        holder.authorAvatar.setImageResource(item.getAuthorAvatarResource());
        holder.questionsText.setText(item.getQuestions().size()+" Qs");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Quiz> getListQuiz() {
        return items;
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
}

