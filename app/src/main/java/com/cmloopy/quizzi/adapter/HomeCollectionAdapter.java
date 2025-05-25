package com.cmloopy.quizzi.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.views.TopCollections;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeCollectionAdapter extends RecyclerView.Adapter<HomeCollectionAdapter.ViewHolder> {

    private List<QuizCollection> cls;
    private int userId;
    public HomeCollectionAdapter(List<QuizCollection> cls, int userId) {this.cls = cls; this.userId = userId;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_collection,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizCollection cl = cls.get(position);
        Picasso.get().load(cl.getCoverPhoto()).into(holder.img_collection);
        holder.txt_collection.setText(cl.getDescription());
        holder.itemView.setOnClickListener(v->
        {
            Intent intent = new Intent(v.getContext(), TopCollections.class);
            intent.putExtra("userId", userId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView img_collection;
        MaterialTextView txt_collection;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            img_collection = itemView.findViewById(R.id.img_bia_collection);
            txt_collection = itemView.findViewById(R.id.txt_cate);
        }
    }
    public void setData(List<QuizCollection> quizCollections){
        cls = quizCollections;
        notifyDataSetChanged();
    }
}
