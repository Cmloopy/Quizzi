package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.HomeAuthorAdapter;
import com.cmloopy.quizzi.adapter.HomeCollectionAdapter;
import com.cmloopy.quizzi.adapter.HomeDiscoverAdapter;
import com.cmloopy.quizzi.models.HomeCollection;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.RecommendUser;
import com.cmloopy.quizzi.views.DetailTopCollections;
import com.cmloopy.quizzi.views.DiscoveryActivity;
import com.cmloopy.quizzi.views.RecommendAuthorActivity;
import com.cmloopy.quizzi.views.SearchActivity;
import com.cmloopy.quizzi.views.TopCollections;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView DiscoverRcl;
    private RecyclerView TrendingQuiz;
    private RecyclerView TopPick;
    private RecyclerView TopAuthor;
    private RecyclerView Collectjon;
    public HomeFragment() {}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        List<Quiz> ListDisCover = CreateSampleData();
        List<RecommendUser> ListUser = CreateSampleData2();
        List<HomeCollection> ListCls = CreateSampleData3();

        //Discover
        DiscoverRcl = view.findViewById(R.id.rcl_home_discover);
        DiscoverRcl.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DiscoverRcl.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Trending Quiz
        TrendingQuiz = view.findViewById(R.id.rcl_home_trending_quiz);
        TrendingQuiz.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TrendingQuiz.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Top Pick
        TopPick = view.findViewById(R.id.rcl_home_top_pick);
        TopPick.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TopPick.setAdapter(new HomeDiscoverAdapter(ListDisCover));

        //Top Author
        TopAuthor = view.findViewById(R.id.rcl_home_top_author);
        TopAuthor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TopAuthor.setAdapter(new HomeAuthorAdapter(ListUser));

        //Collection
        Collectjon = view.findViewById(R.id.rcl_home_top_collection);
        Collectjon.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        Collectjon.setAdapter(new HomeCollectionAdapter(ListCls));

        //Click
        MaterialTextView txt1 = view.findViewById(R.id.view_all_discover);
        MaterialTextView txt2 = view.findViewById(R.id.view_all_top_author);
        MaterialTextView txt3 = view.findViewById(R.id.view_all_top_collection);
        MaterialTextView txt4 = view.findViewById(R.id.view_all_trending);
        MaterialTextView txt5 = view.findViewById(R.id.view_all_top_pick);
        ShapeableImageView shape1 = view.findViewById(R.id.btn_find);
        shape1.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });
        txt1.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });
        txt2.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), RecommendAuthorActivity.class);
            startActivity(intent);
        });
        txt3.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), TopCollections.class);
            startActivity(intent);
        });
        txt4.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });
        txt5.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private List<HomeCollection> CreateSampleData3() {
        List<HomeCollection> cls = new ArrayList<>();
        cls.add(new HomeCollection(R.drawable.ic_launcher_background,"Animals"));
        cls.add(new HomeCollection(R.drawable.ic_launcher_background,"Cars"));
        cls.add(new HomeCollection(R.drawable.ic_launcher_background,"Weathers"));
        cls.add(new HomeCollection(R.drawable.ic_launcher_background,"Water"));
        cls.add(new HomeCollection(R.drawable.ic_launcher_background,"Money"));
        return cls;
    }

    private List<RecommendUser> CreateSampleData2() {
        List<RecommendUser> friendsList = new ArrayList<>();
        friendsList.add(new RecommendUser("Darron Kulikowski", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Maryland Winkles", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Lauralee Quintero", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Scales", R.drawable.ic_launcher_background));
        return friendsList;
    }

    private List<Quiz> CreateSampleData() {
        List<Quiz> items = new ArrayList<>();

        items.add(new Quiz(R.drawable.ic_launcher_background, "Get Smarter with Prod...",
                "2 months ago", "5.5K plays", "Titus Kitamura", R.drawable.ic_launcher_background, "16 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Great Ideas Come from...",
                "6 months ago", "10.3K plays", "Alfonzo Schuessler", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Having Fun & Always S...",
                "2 years ago", "18.5K plays", "Daryl Nehls", R.drawable.ic_launcher_background, "12 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Can You Imagine, Worl...",
                "3 months ago", "4.9K plays", "Edgar Torrey", R.drawable.ic_launcher_background, "20 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "Back to School, Get Sm...",
                "1 year ago", "12.4K plays", "Darcel Ballentine", R.drawable.ic_launcher_background, "10 Qs"));

        items.add(new Quiz(R.drawable.ic_launcher_background, "What is Your Favorite ...",
                "5 months ago", "6.2K plays", "Elmer Laverty", R.drawable.ic_launcher_background, "16 Qs"));

        return items;
    }
}