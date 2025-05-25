package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.DetailTopCollectionAdapter;
import com.cmloopy.quizzi.models.DetailTopCollectionItem;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthorCollectionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DetailTopCollectionAdapter adapter;
    private TextView collectionsCountText;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_collections, container, false);

        collectionsCountText = view.findViewById(R.id.collections_count_text);
        recyclerView = view.findViewById(R.id.recycler_collections);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new DetailTopCollectionAdapter(Collections.emptyList(), -1);
        recyclerView.setAdapter(adapter);

        return view;
    }

}