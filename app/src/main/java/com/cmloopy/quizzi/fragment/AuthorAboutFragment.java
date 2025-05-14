package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;

public class AuthorAboutFragment extends Fragment {

    private String authorId;
    private String authorName;
    private String authorUsername;
    private int authorAvatar;
    private String authorAvatarUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy dữ liệu tác giả từ arguments
        if (getArguments() != null) {
            authorId = getArguments().getString("AUTHOR_ID");
            authorName = getArguments().getString("AUTHOR_NAME");
            authorUsername = getArguments().getString("AUTHOR_USERNAME");
            authorAvatar = getArguments().getInt("AUTHOR_AVATAR");
            if (getArguments().containsKey("AUTHOR_AVATAR_URL")) {
                authorAvatarUrl = getArguments().getString("AUTHOR_AVATAR_URL");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_about, container, false);

        // Giả sử view gốc là một ViewGroup (như LinearLayout)
        if (view instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view;

            // Tạo TextView mới
            TextView aboutText = new TextView(getContext());
            aboutText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            aboutText.setTextSize(16);

            // Đặt nội dung
            String aboutContent = authorName != null ? authorName : "This author";
            aboutContent += " is a popular quiz creator on Quizzi. " +
                    "They specialize in educational and entertaining quizzes on various topics. " +
                    "Follow them to stay updated with their latest quizzes!";
            aboutText.setText(aboutContent);

            // Thêm vào view cha
            parentView.addView(aboutText);
        }

        return view;
    }
}