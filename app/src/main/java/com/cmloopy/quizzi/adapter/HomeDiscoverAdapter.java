package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.cmloopy.quizzi.views.QuizzDetails;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDiscoverAdapter extends RecyclerView.Adapter<HomeDiscoverAdapter.ViewHolder> {
    private List<QuizResponse> items;
    private int userId;
    private Context context;
    public HomeDiscoverAdapter(List<QuizResponse> items, Context context, int userId) {this.items = items; this.context = context; this.userId = userId;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_discover, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResponse item = items.get(position);
        Glide.with(context).load(item.getCoverPhoto()).into(holder.podcastImage);
        holder.questionsText.setText(item.getNumberQuestion() + " Qs");
        holder.titleText.setText(item.getTitle());
        Call<User> call = RetrofitClient.getUserApi().getInfoUserById(item.getUserId());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if(user != null) {
                    holder.authorName.setText(user.getFullName());
                    Glide.with(context).load(user.getAvatar()).placeholder(R.drawable.bus).into(holder.authorAvatar);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, QuizzDetails.class);
                intent.putExtra("quizId", item.getId());
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView podcastImage;
        MaterialTextView titleText;
        ShapeableImageView authorAvatar;
        MaterialTextView authorName;
        MaterialTextView questionsText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            podcastImage = itemView.findViewById(R.id.img_category);
            titleText = itemView.findViewById(R.id.txt_title);
            authorAvatar = itemView.findViewById(R.id.img_biaAuthor);
            authorName = itemView.findViewById(R.id.txt_name_author);
            questionsText = itemView.findViewById(R.id.txt_ques);
        }
    }
    public void setData(List<QuizResponse> quizResponses){
        items = quizResponses;
        notifyDataSetChanged();
    }
}
