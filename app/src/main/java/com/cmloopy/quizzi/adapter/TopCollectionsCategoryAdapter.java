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
import com.cmloopy.quizzi.models.TopCollectionsCategory;
import com.cmloopy.quizzi.views.DetailTopCollections;

import java.util.List;

public class TopCollectionsCategoryAdapter extends RecyclerView.Adapter<TopCollectionsCategoryAdapter.ViewHolder> {
    private static final String TAG = "TopCollectionsCategoryAdapter";
    private List<TopCollectionsCategory> categories;
    private Context context;

    public TopCollectionsCategoryAdapter(Context context, List<TopCollectionsCategory> categories) {
        this.context = context;
        this.categories = categories;
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
        holder.categoryImage.setImageResource(category.getImageResource());

        // Debug log
        Log.d(TAG, "Binding category: " + category.getName() + " with ID: " + category.getCollectionId());

        // Xử lý sự kiện click - OneClick để mở chi tiết ngay lập tức
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailScreen(category);
            }
        });
    }

    // Phương thức riêng để mở màn hình chi tiết với dữ liệu collection
    private void openDetailScreen(TopCollectionsCategory category) {
        Intent intent = new Intent(context, DetailTopCollections.class);

        // Truyền collectionId qua Intent
        intent.putExtra("collectionId", category.getCollectionId());

        // Truyền thêm tên danh mục để hiển thị ngay lập tức
        intent.putExtra("categoryName", category.getName());

        // Debug log
        Log.d(TAG, "Opening detail for category: " + category.getName() + " with ID: " + category.getCollectionId());

        // Bắt đầu Activity mới ngay lập tức
        context.startActivity(intent);
    }

    public List<TopCollectionsCategory> getCategoryList() {
        return categories;
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