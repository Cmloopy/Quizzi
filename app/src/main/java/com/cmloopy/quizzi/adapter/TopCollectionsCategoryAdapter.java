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
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.cmloopy.quizzi.views.DetailTopCollections;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopCollectionsCategoryAdapter extends RecyclerView.Adapter<TopCollectionsCategoryAdapter.ViewHolder> {
    private static final String TAG = "TopCollectionsCategoryAdapter";
    private List<QuizCollection> categories;
    private Context context;
    private int userId;

    public TopCollectionsCategoryAdapter(Context context, List<QuizCollection> categories, int userId) {
        this.context = context;
        this.categories = categories;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.top_collections_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizCollection category = categories.get(position);
        holder.categoryName.setText(category.getCategory());
        Picasso.get()
                .load(category.getCoverPhoto())
                .placeholder(R.drawable.ic_image_placeholder_2)
                .error(R.drawable.ic_image_placeholder_2)
                .into(holder.categoryImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailScreen(category);
            }
        });
    }

    // Phương thức riêng để mở màn hình chi tiết với dữ liệu collection
    private void openDetailScreen(QuizCollection category) {
        Intent intent = new Intent(context, DetailTopCollections.class);

        intent.putExtra("collectionId", category.getId());

        intent.putExtra("userId",userId);

        // Bắt đầu Activity mới ngay lập tức
        context.startActivity(intent);
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