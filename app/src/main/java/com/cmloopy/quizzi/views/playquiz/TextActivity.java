package com.cmloopy.quizzi.views.playquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.databinding.ActivityTextBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.puzzle.PuzzleQuestion;
import com.cmloopy.quizzi.models.question.text.AcceptAnswer;
import com.cmloopy.quizzi.models.question.text.TextQuestion;
import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextActivity extends AppCompatActivity {
    private ActivityTextBinding binding;
    private int totalScore;
    private int userId;
    private int quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        int questionId = getIntent().getIntExtra("questionId", -1);
        int[] listQuesId = getIntent().getIntArrayExtra("listIdQues");
        String []listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getIntExtra("quizId",-1);

        binding.txtNumAnswerText.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimeText.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);

        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Question q = response.body();
                    if (q instanceof TextQuestion) {
                        binding.txtTitleText.setText(q.content);
                        TextQuestion textQuestion = (TextQuestion) q;
                        List<AcceptAnswer> ans = textQuestion.acceptedAnswers;
                        binding.materialButtonSubmitText.setOnClickListener(v->{
                            String anss = binding.edtAnswerText.getText().toString().trim();
                            for(AcceptAnswer i : ans){
                                if(i.text.equals(anss)){
                                    totalScore += textQuestion.getPoint();
                                    int myColor = ContextCompat.getColor(TextActivity.this, R.color.correct_green);
                                    binding.notiStatusText.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                    binding.textCountPointText.setText("+"+ (textQuestion.point+"") + "pts");
                                    binding.notiStatusText.setVisibility(View.VISIBLE);
                                } else {
                                    Log.e("Puzzle", "Wrong");
                                    int myColor = ContextCompat.getColor(TextActivity.this, R.color.incorrect_light_red);
                                    binding.notiStatusText.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                    binding.textCountPointText.setText("Try it out!");
                                    binding.txtSttText.setText("Incorrect!");
                                    binding.notiStatusText.setVisibility(View.VISIBLE);
                                }
                                Log.e("totalPoint", totalScore+ "");
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    if (questionId + 1 < listQuesId.length) {
                                        nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                                    } else {
                                        Intent intent = new Intent(TextActivity.this, ReviewActivity.class);
                                        intent.putExtra("userId",userId);
                                        intent.putExtra("quizId", quizId);
                                        intent.putExtra("totalPoint",totalScore);
                                        startActivity(intent);
                                    }
                                }, 3000);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {

            }
        });
    }
    private void nextQuestion(int questionId, int totalScore, int[] listQuesId, String[] listQuesType) {
        if(listQuesType[questionId].equals("TRUE_FALSE")){
            Intent intent = new Intent(TextActivity.this, TrueFalseActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("SINGLE_CHOICE")){
            Intent intent = new Intent(TextActivity.this, SingleChoiceActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("MULTI_CHOICE")){
            Intent intent = new Intent(TextActivity.this, MultiChoiceActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("TEXT")){
            Intent intent = new Intent(TextActivity.this, TextActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("PUZZLE")){
            Intent intent = new Intent(TextActivity.this, PuzzleActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("SLIDER")){
            Intent intent = new Intent(TextActivity.this, SliderActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("quizId", quizId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
    }
}