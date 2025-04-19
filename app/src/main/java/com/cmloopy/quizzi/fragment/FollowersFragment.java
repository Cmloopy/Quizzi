package com.cmloopy.quizzi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.FollowFraAdapter;
import com.cmloopy.quizzi.models.RecommendUser;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {
    private RecyclerView recyclerView;
    private FollowFraAdapter ad;
    public FollowersFragment() {
        // Required empty public constructor
    }
    public static FollowersFragment newInstance(String param1, String param2) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_follower);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ad = new FollowFraAdapter(requireContext(),CreateSampleData2());
        recyclerView.setAdapter(ad);

        return view;
    }
    private List<RecommendUser> CreateSampleData2() {
        List<RecommendUser> friendsList = new ArrayList<>();
        friendsList.add(new RecommendUser("Darron Kulikowski", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Maryland Winkles", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Lauralee Quintero", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Scales", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Darron Kulikowski", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Maryland Winkles", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Lauralee Quintero", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Schuessler", R.drawable.ic_launcher_background));
        friendsList.add(new RecommendUser("Alfonzo Scales", R.drawable.ic_launcher_background));
        return friendsList;
    }
}