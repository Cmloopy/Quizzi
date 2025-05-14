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
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.cmloopy.quizzi.views.QuizzDetails;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private static final String TAG = "QuizAdapter";
    private List<Quiz> items;
    private int userId;

    public QuizAdapter(List<Quiz> items, int userId) {
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
        Quiz quiz = items.get(position);

        //holder.podcastImage.setImageResource(item.getImageResource());
        //holder.titleText.setText(item.getTitle());
        //holder.dateAndPlaysText.setText(item.getDate() + " • " + item.getPlays());
        //holder.authorName.setText(item.getAuthor());
        //holder.authorAvatar.setImageResource(item.getAuthorAvatarResource());
        //holder.questionsText.setText(item.getQuestions().size() + " Qs");
        // Thiết lập tiêu đề quiz sử dụng getter
        holder.titleText.setText(quiz.getTitle());

        // Thiết lập thông tin ngày và lượt chơi
        StringBuilder dateAndPlays = new StringBuilder();
        dateAndPlays.append(quiz.getDate());
        if (quiz.getPlays() != null && !quiz.getPlays().isEmpty()) {
            dateAndPlays.append(" • ").append(quiz.getPlays());
        }
        holder.dateAndPlaysText.setText(dateAndPlays.toString());

        // Thiết lập số câu hỏi (nếu có thông tin)
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            holder.questionsText.setText(quiz.getQuestions().size() + " questions");
        } else {
            holder.questionsText.setText("? questions");
        }

        // Thiết lập ảnh quiz
        holder.podcastImage.setImageResource(quiz.getImageResource());

        // Thiết lập thông tin tác giả
        holder.authorName.setText(quiz.getAuthor());

        // Thiết lập avatar tác giả
        holder.authorAvatar.setImageResource(quiz.getAuthorAvatarResource());

        // Thêm click listener cho quiz
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

        // Thêm click listener cho tác giả
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

    public List<Quiz> getListQuiz() {
        return items;
    }

    // Thêm phương thức để cập nhật dữ liệu
    public void updateQuizzes(List<Quiz> newQuizzes) {
        this.items = newQuizzes;
        notifyDataSetChanged();
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