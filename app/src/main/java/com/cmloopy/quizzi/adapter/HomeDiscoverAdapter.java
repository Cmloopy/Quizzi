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

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.views.QuizzDetails;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeDiscoverAdapter extends RecyclerView.Adapter<HomeDiscoverAdapter.ViewHolder> {
    private List<Quiz> items;
    public HomeDiscoverAdapter(List<Quiz> items) {this.items = items;}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_discover, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz item = items.get(position);
        holder.podcastImage.setImageResource(item.getImageResource());
        holder.questionsText.setText(item.getQuestions().size()+" Qs");
        holder.titleText.setText(item.getTitle());
        holder.authorAvatar.setImageResource(item.getAuthorAvatarResource());
        holder.authorName.setText(item.getAuthor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, QuizzDetails.class);
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
}
