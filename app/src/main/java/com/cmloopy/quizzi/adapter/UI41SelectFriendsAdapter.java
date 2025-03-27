package com.cmloopy.quizzi.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI41Friends;

import java.util.List;

public class UI41SelectFriendsAdapter extends RecyclerView.Adapter<UI41SelectFriendsAdapter.ViewHolder> {

    private List<UI41Friends> selectedFriends;

    public UI41SelectFriendsAdapter(List<UI41Friends> selectedFriends) {
        this.selectedFriends = selectedFriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_41_select_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UI41Friends friend = selectedFriends.get(position);
        holder.avatar.setImageResource(friend.getAvatar());
    }

    @Override
    public int getItemCount() {
        return selectedFriends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.UI41imgSelectedAvatar);
        }
    }
}
