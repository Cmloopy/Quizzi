package com.cmloopy.quizzi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.models.Quiz;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        /*recyclerView = view.findViewById(R.id.rcl_view_favor);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter();*/

        return view;
    }

}