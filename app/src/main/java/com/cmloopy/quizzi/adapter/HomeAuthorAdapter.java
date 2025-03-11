package com.cmloopy.quizzi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.RecommendUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeAuthorAdapter extends RecyclerView.Adapter<HomeAuthorAdapter.ViewHolder> {
    private List<RecommendUser> items;
    public HomeAuthorAdapter(List<RecommendUser> items) {this.items = items;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendUser user = items.get(position);
        holder.img_author.setImageResource(user.getProfileImageResource());
        holder.txt_name_author.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView img_author;
        MaterialTextView txt_name_author;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            img_author = itemView.findViewById(R.id.img_author);
            txt_name_author = itemView.findViewById(R.id.txt_nameauthor);
        }
    }
}
