package com.cmloopy.quizzi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.HomeCollection;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HomeCollectionAdapter extends RecyclerView.Adapter<HomeCollectionAdapter.ViewHolder> {

    private List<HomeCollection> cls;
    public HomeCollectionAdapter(List<HomeCollection> cls) {this.cls = cls;}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_collection,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeCollection cl = cls.get(position);
        holder.img_collection.setImageResource(cl.getImageCollection());
        holder.txt_collection.setText(cl.getNameCollection());
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
}
