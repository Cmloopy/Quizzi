package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizAdapter;
import com.cmloopy.quizzi.adapter.TopCollectionsCategoryAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.CollectionApi;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.databinding.FragmentMyQuizzoBinding;
import com.cmloopy.quizzi.models.TopCollections.QuizCollection;
import com.cmloopy.quizzi.models.quiz.QuizCollectionResponse;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.google.android.material.textview.MaterialTextView;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuizzoFragment extends Fragment {
    private FragmentMyQuizzoBinding binding;
    private QuizzApi quizzApi = RetrofitClient.getQuizzApi();
    private CollectionApi collectionApi = RetrofitClient.getCollectionApi();

    private int userId = -1;

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
        binding = FragmentMyQuizzoBinding.inflate(inflater,container, false);

        binding.searchOptions.radioLibQuizzoBtn.setChecked(true);
        initListQuiz();

        binding.searchOptions.radioLibQuizzoBtn.setOnClickListener(v->{
            binding.searchOptions.radioLibQuizzoBtn.setChecked(true);
            binding.searchOptions.radioLibCollectionBtn.setChecked(false);
            initListQuiz();
        });
        binding.searchOptions.radioLibCollectionBtn.setOnClickListener(v->{
            binding.searchOptions.radioLibQuizzoBtn.setChecked(false);
            binding.searchOptions.radioLibCollectionBtn.setChecked(true);
            initListCollection();
        });
        return binding.getRoot();
    }

    private void initListCollection() {
        Call<List<QuizCollection>> call = collectionApi.getAllCollections();
        call.enqueue(new Callback<List<QuizCollection>>() {
            @Override
            public void onResponse(Call<List<QuizCollection>> call, Response<List<QuizCollection>> response) {
                List<QuizCollection> list = response.body();
                if(list!=null && !list.isEmpty()){
                    binding.txtTitleLibMyQuizzo.setText("" + list.size() + "Collections");
                    TopCollectionsCategoryAdapter adapter = new TopCollectionsCategoryAdapter(requireContext(), list, userId);
                    binding.rclViewQuizzoCls.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                    binding.rclViewQuizzoCls.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<QuizCollection>> call, Throwable t) {
                Log.e("err", "vug");
                t.printStackTrace();
            }
        });
    }

    private void initListQuiz() {
        Call<List<QuizResponse>> call = quizzApi.getAllQuiz();
        call.enqueue(new Callback<List<QuizResponse>>() {
            @Override
            public void onResponse(Call<List<QuizResponse>> call, Response<List<QuizResponse>> response) {
                List<QuizResponse> quizResponses = response.body();
                if(quizResponses!= null){
                    binding.txtTitleLibMyQuizzo.setText("" + quizResponses.size() + "Quizz");
                    QuizAdapter adapter = new QuizAdapter(quizResponses, userId);
                    binding.rclViewQuizzoCls.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.rclViewQuizzoCls.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<QuizResponse>> call, Throwable t) {
                binding.txtTitleLibMyQuizzo.setText("0");
                QuizAdapter adapter = new QuizAdapter(Collections.emptyList(), userId);
                binding.rclViewQuizzoCls.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.rclViewQuizzoCls.setAdapter(adapter);
                Log.e("err", "vug");
                t.printStackTrace();
            }
        });
    }
}