package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.views.AuthorDetailsActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeAuthorAdapter extends RecyclerView.Adapter<HomeAuthorAdapter.ViewHolder> {
    private List<RecommendUser> items;

    public HomeAuthorAdapter(List<RecommendUser> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendUser user = items.get(position);

        // Kiểm tra xem người dùng có avatarUrl hay không
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            // Tải ảnh từ URL bằng Picasso
            Picasso.get()
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.img_author);
        } else {
            // Sử dụng hình ảnh local
            holder.img_author.setImageResource(user.getProfileImageResource());
        }

        holder.txt_name_author.setText(user.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, AuthorDetailsActivity.class);
                // Truyền thông tin tác giả sang màn hình chi tiết
                intent.putExtra("authorId", user.getId());
                intent.putExtra("authorName", user.getName());
                intent.putExtra("authorUsername", user.getUsername());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView img_author;
        MaterialTextView txt_name_author;
        ViewHolder(@NonNull View itemView){
            super(itemView);
            img_author = itemView.findViewById(R.id.img_author);
            txt_name_author = itemView.findViewById(R.id.txt_nameauthor);
        }
    }
}