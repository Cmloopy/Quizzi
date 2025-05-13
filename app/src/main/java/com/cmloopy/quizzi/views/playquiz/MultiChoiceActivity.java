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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.playquiz.MultiChoiceAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.databinding.ActivityMultiChoiceBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.choice.ChoiceOption;
import com.cmloopy.quizzi.models.question.choice.MultiChoiceQuestion;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultiChoiceActivity extends AppCompatActivity {
    private ActivityMultiChoiceBinding binding;
    private int questionId;
    private int[] listQuesId;
    private String[] listQuesType;
    private int totalScore;
    private int userId;
    private int quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMultiChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        questionId = getIntent().getIntExtra("questionId", -1);
        listQuesId = getIntent().getIntArrayExtra("listIdQues");
        listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getIntExtra("quizId",-1);

        binding.txtNumAnswerMultiChoice.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimeMultichoice.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);
        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null){
                    Question q = response.body();
                    if (q instanceof MultiChoiceQuestion) {
                        MultiChoiceQuestion mcq = (MultiChoiceQuestion) q;
                        List<ChoiceOption> options = mcq.choiceOptions;
                        binding.txtTitleMultichoice.setText(mcq.content);
                        binding.recyclerViewMultichoice.setLayoutManager(new LinearLayoutManager(MultiChoiceActivity.this));
                        binding.recyclerViewMultichoice.setAdapter(new MultiChoiceAdapter(MultiChoiceActivity.this, options));

                        binding.materialButtonSubmitMultichoice.setOnClickListener(v->{
                            MultiChoiceAdapter adapter = (MultiChoiceAdapter) binding.recyclerViewMultichoice.getAdapter();
                            List<ChoiceOption> optionss = adapter.answerList;

                            boolean isCorrect = isAnswerCorrect(optionss);
                            if (isCorrect) {
                                totalScore += mcq.getPoint();
                                Log.e("Correct", "Crrct");
                                Log.e("Correct", totalScore+ "");
                                int myColor = ContextCompat.getColor(MultiChoiceActivity.this, R.color.correct_green);
                                binding.notiStatusMultichoice.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPoint.setText("+"+ (mcq.point+"") + "pts");
                                binding.notiStatusMultichoice.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Incorrect", "false");
                                int myColor = ContextCompat.getColor(MultiChoiceActivity.this, R.color.incorrect_light_red);
                                binding.notiStatusMultichoice.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPoint.setText("No Problem! Try Again!!");
                                binding.txtStatusMt.setText("Incorrect!");
                                binding.notiStatusMultichoice.setVisibility(View.VISIBLE);
                            }
                            Log.e("totalPoint", totalScore+ "");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (questionId + 1 < listQuesId.length) {
                                    nextQuestion(questionId + 1);
                                } else {
                                    Intent intent = new Intent(MultiChoiceActivity.this, ReviewActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("quizId", quizId);
                                    intent.putExtra("totalPoint", totalScore);
                                    startActivity(intent);
                                }
                            }, 3000);
                        });
                    }
                }
            }
            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void nextQuestion(int questionId) {
        if(listQuesType[questionId].equals("TRUE_FALSE")){
            Intent intent = new Intent(MultiChoiceActivity.this, TrueFalseActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("SINGLE_CHOICE")){
            Intent intent = new Intent(MultiChoiceActivity.this, SingleChoiceActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("MULTI_CHOICE")){
            Intent intent = new Intent(MultiChoiceActivity.this, MultiChoiceActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("TEXT")){
            Intent intent = new Intent(MultiChoiceActivity.this, TextActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("PUZZLE")){
            Intent intent = new Intent(MultiChoiceActivity.this, PuzzleActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
        if(listQuesType[questionId].equals("SLIDER")){
            Intent intent = new Intent(MultiChoiceActivity.this, SliderActivity.class);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId", userId);
            intent.putExtra("questionId", questionId);
            intent.putExtra("listIdQues", listQuesId);
            intent.putExtra("listTypeQues", listQuesType);
            intent.putExtra("totalPoint", totalScore);
            startActivity(intent);
            finish();
        }
    }

    private boolean isAnswerCorrect(List<ChoiceOption> options) {
        for (ChoiceOption option : options) {
            if (option.isCorrect != option.isSelected) {
                return false;
            }
        }
        return true;
    }
}