package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.FoodItem;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<FoodItem> foodList;
    private OnItemClickListener listener;

    // Interface for click events
    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem, int position);
        void onDeleteClick(FoodItem foodItem, int position);
    }

    public FoodAdapter(Context context, List<FoodItem> foodList, OnItemClickListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem food = foodList.get(position);

        holder.tvFoodName.setText(food.getName());
        holder.tvQuantity.setText(String.format(Locale.getDefault(), "Số lượng: %d", food.getQuantity()));
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.0fđ", food.getPrice()));
        holder.ratingBar.setRating(food.getRating());

        // Set image based on type
        switch (food.getType()) {
            case "Bánh":
                holder.imgFood.setImageResource(R.drawable.cake);
                break;
            case "Đồ uống":
                holder.imgFood.setImageResource(R.drawable.drink);
                break;
            case "Café":
                holder.imgFood.setImageResource(R.drawable.coffee);
                break;
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(food, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(food, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Update the data in the adapter
    public void updateData(List<FoodItem> newFoodList) {
        this.foodList = newFoodList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, btnDelete;
        TextView tvFoodName, tvQuantity, tvPrice;
        RatingBar ratingBar;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}