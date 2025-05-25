package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.google.android.material.textview.MaterialTextView;

public class MyQuizzoFragment extends Fragment {

    private static final String TAG = "MyQuizzoFragment";
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MaterialTextView materialTextView;
    private ProgressBar progressBar;
    private QuizzApi quizzAPI;

    private int userId = -1;

    RecyclerView.Adapter<?> adapter = null;

    public MyQuizzoFragment() {}

    public static MyQuizzoFragment newInstance(int userId) {
        MyQuizzoFragment fragment = new MyQuizzoFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_quizzo, container, false);


        return view;
    }
}