package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.views.playquiz.MultiChoiceActivity;
import com.cmloopy.quizzi.views.playquiz.PuzzleActivity;
import com.cmloopy.quizzi.views.playquiz.SingleChoiceActivity;
import com.cmloopy.quizzi.views.playquiz.TextActivity;
import com.cmloopy.quizzi.views.playquiz.TrueFalseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UI43 extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private int userId;
    private int quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_43);

        progressBar = findViewById(R.id.UI43progressBar);
        quizId = getIntent().getIntExtra("quizId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        if(progressStatus < 60) {
            progressStatus += 60;
            progressBar.setProgress(progressStatus);
        }

        Call<List<Question>> calll = questionApi.getQuestionByQuiz(quizId);
        calll.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Question> questionList = response.body();

                    List<Integer> ids = new ArrayList<>();
                    List<String> types = new ArrayList<>();

                    for (Question q : questionList) {
                        ids.add(q.getId());
                        types.add(q.getQuestionType().getName());
                    }

                    int[] listIdQues = ids.stream()
                            .mapToInt(Integer::intValue)
                            .toArray();
                    String[] listTypeQues = types.toArray(new String[0]);
                    progressBar.setProgress(100);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startGame(listIdQues, listTypeQues);
                    }, 3000);
                } else{
                    Log.e("get list quest", "Failed: ");
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Log.e("get list quest", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void startGame(int[] ids, String[] types) {
        if(types[0].equals("TRUE_FALSE")){
            Intent intent = new Intent(UI43.this, TrueFalseActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("totalPoint", 0);
            intent.putExtra("questionId", 0);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
        if(types[0].equals("SINGLE_CHOICE")){
            Intent intent = new Intent(UI43.this, SingleChoiceActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("totalPoint", 0);
            intent.putExtra("questionId", 0);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
        if(types[0].equals("MULTI_CHOICE")){
            Intent intent = new Intent(UI43.this, MultiChoiceActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("totalPoint", 0);
            intent.putExtra("questionId", 0);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
        if(types[0].equals("SLIDER")){
            Intent intent = new Intent(UI43.this, TrueFalseActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", ids[0]);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
        if(types[0].equals("PUZZLE")){
            Intent intent = new Intent(UI43.this, PuzzleActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("totalPoint", 0);
            intent.putExtra("questionId", 0);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
        if(types[0].equals("TEXT")){
            Intent intent = new Intent(UI43.this, TextActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("totalPoint", 0);
            intent.putExtra("questionId", 0);
            intent.putExtra("listIdQues", ids);
            intent.putExtra("listTypeQues", types);
            startActivity(intent);
            finish();
        }
    }
}

