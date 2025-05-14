package com.cmloopy.quizzi.views.playquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.databinding.ActivitySliderBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.slider.SliderQuestion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderActivity extends AppCompatActivity {
    private ActivitySliderBinding binding;
    private int totalScore;
    private int userId;
    private long quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        int questionId = getIntent().getIntExtra("questionId", -1);
        int[] listQuesId = getIntent().getIntArrayExtra("listIdQues");
        String []listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId", -1);
        quizId = getIntent().getLongExtra("quizId", -1);
        binding.txtNumAnswerSlider.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimeSlider.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);
        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Question q = response.body();
                    if (q instanceof SliderQuestion){
                        binding.txtTitleSlider.setText(q.content);
                        SliderQuestion sliderQuestion = (SliderQuestion) q;
                        binding.customSliderQues.setMinValue(sliderQuestion.minValue);
                        binding.customSliderQues.setMaxValue(sliderQuestion.maxValue);
                        binding.customSliderQues.setCurrentValue(sliderQuestion.defaultValue);

                        binding.materialButtonSubmitSlider.setOnClickListener(v->{
                            int currentAnswer = (int) binding.customSliderQues.getCurrentValue();
                            binding.crrAnsSlider.setText("Correct Answer: "+ (sliderQuestion.correctAnswer + ""));
                            binding.crrAnsSlider.setVisibility(View.VISIBLE);
                            if(currentAnswer == sliderQuestion.correctAnswer){
                                totalScore += sliderQuestion.point;
                                int myColor = ContextCompat.getColor(SliderActivity.this, R.color.correct_green);
                                binding.notiStatusSlider.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointSlider.setText("+"+ (sliderQuestion.point+"") + "pts");
                                binding.notiStatusSlider.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Incorrect", "false");
                                int myColor = ContextCompat.getColor(SliderActivity.this, R.color.incorrect_light_red);
                                binding.notiStatusSlider.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointSlider.setText("Try it out!");
                                binding.sttSld.setText("Incorrect!");
                                binding.notiStatusSlider.setVisibility(View.VISIBLE);
                            }
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (questionId + 1 < listQuesId.length) {
                                    nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                                } else {
                                    Intent intent = new Intent(SliderActivity.this, ReviewActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, TrueFalseActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, SingleChoiceActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, MultiChoiceActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, TextActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, PuzzleActivity.class);
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
            Intent intent = new Intent(SliderActivity.this, SliderActivity.class);
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