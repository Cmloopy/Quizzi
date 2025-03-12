package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.TopCollectionsCategory;

public class TopCollectionsCategoryAdapter extends RecyclerView.Adapter<TopCollectionsCategoryAdapter.ViewHolder> {
    private List<TopCollectionsCategory> categories;
    private Context context;

    public TopCollectionsCategoryAdapter(Context context, List<TopCollectionsCategory> categories) {
        this.context = context;
        this.categories = categories;
    }

    public List<TopCollectionsCategory> getCategoryList(){
        return this.categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.top_collections_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TopCollectionsCategory category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryImage.setImageResource(category.getImageResId());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryImage = itemView.findViewById(R.id.categoryImage);
        }
    }


}
