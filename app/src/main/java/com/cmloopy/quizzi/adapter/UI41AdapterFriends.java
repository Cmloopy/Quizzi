package com.cmloopy.quizzi.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI41Friends;

import java.util.List;

public class UI41AdapterFriends extends RecyclerView.Adapter<UI41AdapterFriends.ViewHolder> {

    private List<UI41Friends> friendList;
    private OnFriendClickListener listener;

    public interface OnFriendClickListener {
        void onFriendClick(int position);
    }

    public UI41AdapterFriends(List<UI41Friends> friendList, OnFriendClickListener listener) {
        this.friendList = friendList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_41_item_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UI41Friends friend = friendList.get(position);
        holder.name.setText(friend.getName());
        holder.avatar.setImageResource(friend.getAvatar());

        if (friend.isSelected()) {
            holder.checkmark.setVisibility(View.VISIBLE);
        } else {
            holder.checkmark.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            friend.setSelected(!friend.isSelected());
            listener.onFriendClick(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, checkmark;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.UI41imgAvatar);
            name = itemView.findViewById(R.id.UI41txtName);
            checkmark = itemView.findViewById(R.id.UI41imgCheck);
        }
    }
}
