package com.cmloopy.quizzi.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI42Player;

import java.util.List;

public class UI42PlayerAdapter extends RecyclerView.Adapter<UI42PlayerAdapter.ViewHolder> {
    private List<UI42Player> players;
    private Context context;

    public UI42PlayerAdapter(Context context, List<UI42Player> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_42_item_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UI42Player player = players.get(position);
        holder.txtName.setText(player.getName());
        holder.imgAvatar.setImageResource(player.getAvatar());

        // Áp dụng animation di chuyển từ trái sang phải
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationX", -500f, 0f);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
