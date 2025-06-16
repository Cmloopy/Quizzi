package com.cmloopy.quizzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuizAPI;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.User;
import com.cmloopy.quizzi.views.DiscoveryActivity;
import com.cmloopy.quizzi.views.RecommendAuthorActivity;
import com.cmloopy.quizzi.views.SearchActivity;
import com.cmloopy.quizzi.views.TopCollections;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView DiscoverRcl;
    private RecyclerView TrendingQuiz;
    private RecyclerView TopPick;
    private RecyclerView TopAuthor;
    private RecyclerView Collectjon;
    private HomeAuthorAdapter topAuthorAdapter;
    private HomeCollectionAdapter collectionAdapter;
    private HomeDiscoverAdapter discoverAdapter;
    private UserApi userApi;
    private CollectionApi collectionApi;
    private QuizAPI quizApi;
    private HomeDiscoverAdapter trendingAdapter;
    private HomeDiscoverAdapter topPickAdapter;

    public static HomeFragment newInstance(int idUser) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("userId", idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo API client
        userApi = RetrofitClient.getUserApi();
        collectionApi = RetrofitClient.getCollectionApi();
        quizApi = RetrofitClient.getQuizApi();
        int idUser = getArguments().getInt("userId", -1);

        // DISCOVER
        DiscoverRcl = view.findViewById(R.id.rcl_home_discover);
        DiscoverRcl.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        discoverAdapter = new HomeDiscoverAdapter(Collections.emptyList(), requireContext(), idUser);
        DiscoverRcl.setAdapter(discoverAdapter);

        // TRENDING QUIZ
        TrendingQuiz = view.findViewById(R.id.rcl_home_trending_quiz);
        TrendingQuiz.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingAdapter = new HomeDiscoverAdapter(Collections.emptyList(), requireContext(),idUser);
        TrendingQuiz.setAdapter(trendingAdapter);

        // TOP PICK
        TopPick = view.findViewById(R.id.rcl_home_top_pick);
        TopPick.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topPickAdapter = new HomeDiscoverAdapter(Collections.emptyList(), requireContext(),idUser);
        TopPick.setAdapter(topPickAdapter);
        fetchQuizzes();

        // Top Author
        TopAuthor = view.findViewById(R.id.rcl_home_top_author);
        TopAuthor.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topAuthorAdapter = new HomeAuthorAdapter(Collections.emptyList(), requireContext(), idUser);
        TopAuthor.setAdapter(topAuthorAdapter);
        fetchTopAuthors();

        // Collection
        Collectjon = view.findViewById(R.id.rcl_home_top_collection);
        Collectjon.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        collectionAdapter = new HomeCollectionAdapter(Collections.emptyList(), idUser);
        Collectjon.setAdapter(collectionAdapter);
        fetchTopCollections();

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
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt2.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RecommendAuthorActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt3.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), TopCollections.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt4.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });
        txt5.setOnClickListener(v ->{
            Intent intent = new Intent(requireContext(), DiscoveryActivity.class);
            intent.putExtra("userId",idUser);
            startActivity(intent);
        });

        return view;
    }

    private void fetchQuizzes() {
        Call<List<QuizResponse>> call = quizApi.getAllQuizzes();
        call.enqueue(new Callback<List<QuizResponse>>() {
            @RequiresApi(api = 35)
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizResponse> quizResponse = response.body();
                    discoverAdapter.setData(quizResponse);

                    List<QuizResponse> reversed1 = new ArrayList<>(quizResponse);
                    Collections.reverse(reversed1);
                    trendingAdapter.setData(reversed1);

                    List<QuizResponse> reversed2 = new ArrayList<>(quizResponse);
                    Collections.reverse(reversed2);
                    topPickAdapter.setData(reversed2);

                } else {
                    discoverAdapter.setData(Collections.emptyList());
                    trendingAdapter.setData(Collections.emptyList());
                    topPickAdapter.setData(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                discoverAdapter.setData(Collections.emptyList());
                trendingAdapter.setData(Collections.emptyList());
                topPickAdapter.setData(Collections.emptyList());
            }
        });
    }

    private void fetchTopAuthors() {
        Call<List<User>> call = userApi.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().subList(0,8);
                    topAuthorAdapter.setData(users);
                } else {
                    topAuthorAdapter.setData(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                topAuthorAdapter.setData(Collections.emptyList());
            }
        });
    }
    private void fetchTopCollections() {
        Call<List<QuizCollection>> call = collectionApi.getAllCollections();
        call.enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuizCollection> quizCollections = response.body();
                    collectionAdapter.setData(quizCollections);
                } else {
                    collectionAdapter.setData(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                collectionAdapter.setData(Collections.emptyList());
            }
        });
    }
}