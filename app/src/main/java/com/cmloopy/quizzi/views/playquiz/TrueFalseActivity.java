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
import com.cmloopy.quizzi.databinding.ActivityTrueFalseBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.slider.SliderQuestion;
import com.cmloopy.quizzi.models.question.truefalse.TrueFalseQuestion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrueFalseActivity extends AppCompatActivity {
    private ActivityTrueFalseBinding binding;
    private int totalScore;
    private int userId;
    private int quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrueFalseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        int questionId = getIntent().getIntExtra("questionId", -1);
        int[] listQuesId = getIntent().getIntArrayExtra("listIdQues");
        String []listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getIntExtra("quizId",-1);
        binding.txtNumAnswerTrue.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimeTrue.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);
        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Question q = response.body();
                    if (q instanceof TrueFalseQuestion){
                        binding.txtTitleTrue.setText(q.content);
                        TrueFalseQuestion trueFalseQuestion = (TrueFalseQuestion) q;
                        binding.btnChooseTrue.setOnClickListener(v->{
                            binding.cbTruefalseChoiceOp1.setChecked(true);
                            binding.cbTruefalseChoiceOp2.setChecked(false);
                        });
                        binding.btnChooseFalse.setOnClickListener(v->{
                            binding.cbTruefalseChoiceOp1.setChecked(false);
                            binding.cbTruefalseChoiceOp2.setChecked(true);
                        });
                        binding.materialButtonSubmitTruefalse.setOnClickListener(v->{
                            boolean isCorrect = trueFalseQuestion.correctAnswer;
                            boolean isChoose = false;
                            if(binding.cbTruefalseChoiceOp1.isChecked()) isChoose = true;
                            else isChoose = false;
                            if(isChoose == isCorrect) {
                                totalScore += trueFalseQuestion.point;
                                int myColor = ContextCompat.getColor(TrueFalseActivity.this, R.color.correct_green);
                                binding.notiStatusTrue.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointTrue.setText("+"+ (trueFalseQuestion.point+"") + "pts");
                                binding.notiStatusTrue.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Incorrect", "false");
                                int myColor = ContextCompat.getColor(TrueFalseActivity.this, R.color.incorrect_light_red);
                                binding.notiStatusTrue.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointTrue.setText("Try it out!");
                                binding.txtSttTf.setText("Incorrect!");
                                binding.notiStatusTrue.setVisibility(View.VISIBLE);
                            }
                            Log.e("totalPoint", totalScore+ "");
                            Log.e("totalPoint", totalScore+ "");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (questionId + 1 < listQuesId.length) {
                                    nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                                } else {
                                    Intent intent = new Intent(TrueFalseActivity.this, ReviewActivity.class);
                                    intent.putExtra("userId",userId);
                                    intent.putExtra("quizId", quizId);
                                    intent.putExtra("totalPoint",totalScore);
                                    startActivity(intent);
                                }
                            }, 3000);
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
            Intent intent = new Intent(TrueFalseActivity.this, TrueFalseActivity.class);
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
            Intent intent = new Intent(TrueFalseActivity.this, SingleChoiceActivity.class);
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
            Intent intent = new Intent(TrueFalseActivity.this, MultiChoiceActivity.class);
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
            Intent intent = new Intent(TrueFalseActivity.this, TextActivity.class);
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
            Intent intent = new Intent(TrueFalseActivity.this, PuzzleActivity.class);
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
            Intent intent = new Intent(TrueFalseActivity.this, SliderActivity.class);
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