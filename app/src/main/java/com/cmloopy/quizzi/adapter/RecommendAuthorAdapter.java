package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.data.manager.UserApiManager;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.RecommendUser;

import java.util.List;

public class RecommendAuthorAdapter extends RecyclerView.Adapter<RecommendAuthorAdapter.FriendViewHolder> {
    private List<RecommendUser> friendsList;

    public RecommendAuthorAdapter(List<RecommendUser> friendsList) {
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_author, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        RecommendUser friend = friendsList.get(position);
        holder.nameTextView.setText(friend.getName());
        holder.usernameTextView.setText("@" + friend.getUsername());
        holder.profileImageView.setImageResource(friend.getProfileImageResource());

        if (friend.isFollowing()) {
            setFollowingState(holder);
        } else {
            setUnfollowedState(holder);
        }

        holder.followButton.setOnClickListener(v -> {
            if (friend.isFollowing()) {
                friend.setFollowing(false);
                Toast.makeText(holder.itemView.getContext(),
                        "Unfollowed " + friend.getName(), Toast.LENGTH_SHORT).show();
                setUnfollowedState(holder);
            } else {
                friend.setFollowing(true);
                Toast.makeText(holder.itemView.getContext(),
                        "Following " + friend.getName(), Toast.LENGTH_SHORT).show();
                setFollowingState(holder);
            }
        });

        // Sửa đổi phần onClick để truyền dữ liệu tác giả
        // Trong RecommendAuthorAdapter.java, phương thức onBindViewHolder
        // Trong RecommendAuthorAdapter.java, phương thức onBindViewHolder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                // Lấy ID của tác giả
                int authorId = friend.getId();

                // Gọi API để lấy thông tin chi tiết của tác giả (User)
                UserApiManager.getInstance().getUserById(context, authorId, new UserApiManager.OnUserLoadedListener() {
                    @Override
                    public void onUserLoaded(User user) {
                        // Sau khi có dữ liệu chi tiết, tạo intent và chuyển sang AuthorDetailsActivity
                        Intent intent = new Intent(context, AuthorDetailsActivity.class);

                        // Thông tin cơ bản
                        intent.putExtra("AUTHOR_ID", String.valueOf(user.getId()));
                        intent.putExtra("AUTHOR_NAME", user.getFullName());
                        intent.putExtra("AUTHOR_USERNAME", user.getUsername());
                        intent.putExtra("AUTHOR_AVATAR", friend.getProfileImageResource()); // Vẫn giữ hình ảnh cục bộ

                        // Thống kê từ API
                        intent.putExtra("AUTHOR_TOTAL_QUIZS", user.getTotalQuizs());
                        intent.putExtra("AUTHOR_TOTAL_COLLECTIONS", user.getTotalCollections());
                        intent.putExtra("AUTHOR_TOTAL_PLAYS", formatNumber(user.getTotalPlays()));
                        intent.putExtra("AUTHOR_TOTAL_PLAYERS", formatNumber(user.getTotalPlayers()));
                        intent.putExtra("AUTHOR_TOTAL_FOLLOWERS", formatNumber(user.getTotalFollowers()));
                        intent.putExtra("AUTHOR_TOTAL_FOLLOWING", user.getTotalFollowees());

                        // Avatar URL nếu có
                        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                            intent.putExtra("AUTHOR_AVATAR_URL", user.getAvatar());
                        }

                        context.startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        // Xử lý lỗi - có thể hiển thị thông báo hoặc chuyển sang activity với dữ liệu cơ bản
//                        Toast.makeText(context, "Không thể tải thông tin chi tiết: " + message, Toast.LENGTH_SHORT).show();

                        // Chuyển sang activity với chỉ thông tin cơ bản
                        Intent intent = new Intent(context, AuthorDetailsActivity.class);
                        intent.putExtra("AUTHOR_ID", String.valueOf(friend.getId()));
                        intent.putExtra("AUTHOR_NAME", friend.getName());
                        intent.putExtra("AUTHOR_USERNAME", friend.getUsername());
                        intent.putExtra("AUTHOR_AVATAR", friend.getProfileImageResource());

                        // Đặt thống kê mặc định trong trường hợp lỗi
                        intent.putExtra("AUTHOR_TOTAL_QUIZS", 265);
                        intent.putExtra("AUTHOR_TOTAL_COLLECTIONS", 49);
                        intent.putExtra("AUTHOR_TOTAL_PLAYS", "32M");
                        intent.putExtra("AUTHOR_TOTAL_PLAYERS", "274M");
                        intent.putExtra("AUTHOR_TOTAL_FOLLOWERS", "927.3K");
                        intent.putExtra("AUTHOR_TOTAL_FOLLOWING", 128);

                        context.startActivity(intent);
                    }
                });
            }

            // Phương thức định dạng số thành chuỗi với K, M, B
            private String formatNumber(int number) {
                if (number >= 1000000000) {
                    return String.format("%.1fB", number / 1000000000.0);
                } else if (number >= 1000000) {
                    return String.format("%.1fM", number / 1000000.0);
                } else if (number >= 1000) {
                    return String.format("%.1fK", number / 1000.0);
                }
                return String.valueOf(number);
            }
        });
    }

    private void setFollowingState(@NonNull FriendViewHolder holder) {
        holder.followButton.setText("Following");
        holder.followButton.setBackgroundResource(R.drawable.bg_button_following_state);
        holder.followButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple));
    }

    private void setUnfollowedState(@NonNull FriendViewHolder holder) {
        holder.followButton.setText("Follow");
        holder.followButton.setBackgroundResource(R.drawable.bg_button_follow_state);
        holder.followButton.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView usernameTextView;
        Button followButton;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            followButton = itemView.findViewById(R.id.followButton);
        }
    }
}