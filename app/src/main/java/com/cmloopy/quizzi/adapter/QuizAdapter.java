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

import com.bumptech.glide.Glide;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.cmloopy.quizzi.views.DetailTopCollections;
import com.cmloopy.quizzi.views.QuizzDetails;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    private static final String TAG = "QuizAdapter";
    private List<QuizResponse> items;
    private int userId;

    public QuizAdapter(List<QuizResponse> items, int userId) {
        this.items = items;
        this.userId = userId;
    }

    public void updateQuizList(List<QuizResponse> newQuizList) {
        this.items.clear();
        this.items.addAll(newQuizList);
        this.notifyDataSetChanged();
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

        // Set quiz title
        holder.titleText.setText(quiz.getTitle());

        // Set date and plays information
        LocalDateTime dateTime = LocalDateTime.parse(quiz.getCreatedAt());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMM, dd");
        String formatted = dateTime.format(formatter);
        holder.dateAndPlaysText.setText(formatted);

        // Load user information
        Call<User> call = RetrofitClient.getUserApi().getInfoUserById(quiz.getUserId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if(user != null) {
                    holder.authorName.setText(user.getFullName());
                    Glide.with(holder.itemView.getContext()).load(user.getAvatar()).placeholder(R.drawable.bus).into(holder.authorAvatar);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });

        holder.questionsText.setText(quiz.getNumberQuestion() + (quiz.getNumberQuestion() > 1 ? " questions" : " question"));

        Picasso.get()
                .load(quiz.getCoverPhoto())
                .fit()
                .centerInside()
                .into(holder.podcastImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                if (context instanceof DetailTopCollections) {
                    DetailTopCollections activity = (DetailTopCollections) context;
                    Long quizId = items.get(position).getId();
                    int tmpId = quizId.intValue();
                    activity.startQuizDetailsActivity(quizId);
                } else {
                    Intent intent = new Intent(context, QuizzDetails.class);
                    intent.putExtra("quizId", items.get(position).getId());
                    intent.putExtra("userId", userId);
                    context.startActivity(intent);
                }
            }
        });

        // Add click listener for author
        View.OnClickListener authorClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, AuthorDetailsActivity.class);
                context.startActivity(intent);
            }
        };

        // Uncomment these if you want author click functionality
        // holder.authorAvatar.setOnClickListener(authorClickListener);
        // holder.authorName.setOnClickListener(authorClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<QuizResponse> getListQuiz() {
        return items;
    }

    // Method to update data
    public void updateQuizzes(List<QuizResponse> newQuizzes) {
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

    public void setData(List<QuizResponse> quizzes){
        items = quizzes;
        notifyDataSetChanged();
    }
}