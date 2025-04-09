package com.cmloopy.quizzi.fragment;

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

        List<Quiz> ListDisCover = Quiz.CreateSampleData();
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
}