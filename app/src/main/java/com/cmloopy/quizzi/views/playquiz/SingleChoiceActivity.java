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
import com.cmloopy.quizzi.databinding.ActivitySingleChoiceBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.choice.ChoiceOption;
import com.cmloopy.quizzi.models.question.choice.MultiChoiceQuestion;
import com.cmloopy.quizzi.models.question.choice.SingleChoiceQuestion;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleChoiceActivity extends AppCompatActivity {
    private ActivitySingleChoiceBinding binding;
    private int totalScore;
    private int userId;
    private int quizId;
    private int isChoose = -1;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingleChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        int questionId = getIntent().getIntExtra("questionId", -1);
        int[] listQuesId = getIntent().getIntArrayExtra("listIdQues");
        String []listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getIntExtra("quizId",-1);
        binding.txtNumAnswerSingleChoice.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimeSingleChoice.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);
        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Question q = response.body();
                    if (q instanceof SingleChoiceQuestion) {
                        binding.txtTitleSingleChoice.setText(q.content);
                        SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) q;
                        List<ChoiceOption> options = singleChoiceQuestion.choiceOptions;
                        binding.txtSingleChoiceOp1.setText(options.get(0).text);
                        binding.txtSingleChoiceOp2.setText(options.get(1).text);
                        binding.txtSingleChoiceOp3.setText(options.get(2).text);
                        binding.txtSingleChoiceOp4.setText(options.get(3).text);

                        binding.cardOp1.setOnClickListener(v -> {
                            binding.cbSingleChoiceOp1.setChecked(true);
                            binding.cbSingleChoiceOp2.setChecked(false);
                            binding.cbSingleChoiceOp3.setChecked(false);
                            binding.cbSingleChoiceOp4.setChecked(false);
                            isChoose = 0;
                        });
                        binding.cardOp2.setOnClickListener(v -> {
                            binding.cbSingleChoiceOp1.setChecked(false);
                            binding.cbSingleChoiceOp2.setChecked(true);
                            binding.cbSingleChoiceOp3.setChecked(false);
                            binding.cbSingleChoiceOp4.setChecked(false);
                            isChoose = 1;
                        });
                        binding.cardOp3.setOnClickListener(v -> {
                            binding.cbSingleChoiceOp1.setChecked(false);
                            binding.cbSingleChoiceOp2.setChecked(false);
                            binding.cbSingleChoiceOp3.setChecked(true);
                            binding.cbSingleChoiceOp4.setChecked(false);
                            isChoose = 2;
                        });
                        binding.cardOp4.setOnClickListener(v -> {
                            binding.cbSingleChoiceOp1.setChecked(false);
                            binding.cbSingleChoiceOp2.setChecked(false);
                            binding.cbSingleChoiceOp3.setChecked(false);
                            binding.cbSingleChoiceOp4.setChecked(true);
                            isChoose = 3;
                        });

                        binding.materialButtonSubmitSinglechoice.setOnClickListener(v->{
                            if(options.get(isChoose).isCorrect){
                                totalScore += singleChoiceQuestion.getPoint();
                                int myColor = ContextCompat.getColor(SingleChoiceActivity.this, R.color.correct_green);
                                binding.notiStatusSinglechoice.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointSg.setText("+"+ (singleChoiceQuestion.point+"") + "pts");
                                binding.notiStatusSinglechoice.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Incorrect", "false");
                                int myColor = ContextCompat.getColor(SingleChoiceActivity.this, R.color.incorrect_light_red);
                                binding.notiStatusSinglechoice.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointSg.setText("No Problem! Try Again!!");
                                binding.txtStatusSc.setText("Incorrect!");
                                binding.notiStatusSinglechoice.setVisibility(View.VISIBLE);
                            }
                            Log.e("totalPoint", totalScore+ "");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (questionId + 1 < listQuesId.length) {
                                    nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                                } else {
                                    Intent intent = new Intent(SingleChoiceActivity.this, ReviewActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, TrueFalseActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, SingleChoiceActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, MultiChoiceActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, TextActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, PuzzleActivity.class);
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
            Intent intent = new Intent(SingleChoiceActivity.this, SliderActivity.class);
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