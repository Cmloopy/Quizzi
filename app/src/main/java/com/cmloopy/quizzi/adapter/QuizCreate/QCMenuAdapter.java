package com.cmloopy.quizzi.adapter.QuizCreate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmloopy.quizzi.R;

import java.util.List;

public class QCMenuAdapter extends RecyclerView.Adapter<QCMenuAdapter.ViewHolder> {

    private List<QCMenuItem> menuItems;
    private OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(QCMenuItem item);
    }

    public QCMenuAdapter(List<QCMenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_qc_item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QCMenuItem item = menuItems.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.imgIcon.setImageResource(item.getIcon());

        // Set Delete color
        if (item.isDestructive()) {
            holder.txtTitle.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMenuItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            imgIcon = itemView.findViewById(R.id.img_icon);
        }
    }
}
