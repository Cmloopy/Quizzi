package com.cmloopy.quizzi.views.playquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    private long quizId;
    private int isChoose = -1;
    private SingleChoiceQuestion singleChoiceQuestion;
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
        quizId = getIntent().getLongExtra("quizId",-1);
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
                        singleChoiceQuestion = (SingleChoiceQuestion) q;
                        List<ChoiceOption> options = singleChoiceQuestion.choiceOptions;
                        binding.txtSingleChoiceOp1.setText(options.get(0).text);
                        binding.txtSingleChoiceOp2.setText(options.get(1).text);
                        binding.txtSingleChoiceOp3.setText(options.get(2).text);
                        binding.txtSingleChoiceOp4.setText(options.get(3).text);

                        setupOptionClickListeners();
                        setupSubmitButton(questionId, listQuesId, listQuesType);
                    }
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                Log.e("SingleChoiceActivity", "Failed to load question", t);
            }
        });
    }

    private void setupOptionClickListeners() {
        binding.cardOp1.setOnClickListener(v -> {
            selectOption(0);
        });

        binding.cardOp2.setOnClickListener(v -> {
            selectOption(1);
        });

        binding.cardOp3.setOnClickListener(v -> {
            selectOption(2);
        });

        binding.cardOp4.setOnClickListener(v -> {
            selectOption(3);
        });

        // Also add click listeners to checkboxes to prevent conflicts
        binding.cbSingleChoiceOp1.setOnClickListener(v -> selectOption(0));
        binding.cbSingleChoiceOp2.setOnClickListener(v -> selectOption(1));
        binding.cbSingleChoiceOp3.setOnClickListener(v -> selectOption(2));
        binding.cbSingleChoiceOp4.setOnClickListener(v -> selectOption(3));
    }

    private void selectOption(int optionIndex) {
        // Clear all checkboxes first
        binding.cbSingleChoiceOp1.setChecked(false);
        binding.cbSingleChoiceOp2.setChecked(false);
        binding.cbSingleChoiceOp3.setChecked(false);
        binding.cbSingleChoiceOp4.setChecked(false);

        // Set the selected option
        switch(optionIndex) {
            case 0:
                binding.cbSingleChoiceOp1.setChecked(true);
                break;
            case 1:
                binding.cbSingleChoiceOp2.setChecked(true);
                break;
            case 2:
                binding.cbSingleChoiceOp3.setChecked(true);
                break;
            case 3:
                binding.cbSingleChoiceOp4.setChecked(true);
                break;
        }

        isChoose = optionIndex;
        Log.d("SingleChoiceActivity", "Selected option: " + isChoose);
    }

    private void setupSubmitButton(int questionId, int[] listQuesId, String[] listQuesType) {
        binding.materialButtonSubmitSinglechoice.setOnClickListener(v -> {
            Log.d("SingleChoiceActivity", "Submit clicked, isChoose: " + isChoose);

            if(isChoose == -1) {
                Toast.makeText(SingleChoiceActivity.this, "Please select an option", Toast.LENGTH_SHORT).show();
                return;
            }

            if(singleChoiceQuestion == null || singleChoiceQuestion.choiceOptions == null) {
                Toast.makeText(SingleChoiceActivity.this, "Question data not loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ChoiceOption> options = singleChoiceQuestion.choiceOptions;
            if(isChoose >= 0 && isChoose < options.size() && options.get(isChoose).isCorrect){
                totalScore += singleChoiceQuestion.getPoint();
                int myColor = ContextCompat.getColor(SingleChoiceActivity.this, R.color.correct_green);
                binding.notiStatusSinglechoice.setBackgroundTintList(ColorStateList.valueOf(myColor));
                binding.textCountPointSg.setText("+"+ (singleChoiceQuestion.point+"") + "pts");
                binding.txtStatusSc.setText("Correct!");
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

            // Disable submit button to prevent multiple clicks
            binding.materialButtonSubmitSinglechoice.setEnabled(false);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (questionId + 1 < listQuesId.length) {
                    nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                } else {
                    Intent intent = new Intent(SingleChoiceActivity.this, ReviewActivity.class);
                    intent.putExtra("userId",userId);
                    intent.putExtra("quizId", quizId);
                    intent.putExtra("totalPoint",totalScore);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
        });
    }

    private void nextQuestion(int questionId, int totalScore, int[] listQuesId, String[] listQuesType) {
        Intent intent = null;
        Class<?> targetActivity = null;

        switch(listQuesType[questionId]) {
            case "TRUE_FALSE":
                targetActivity = TrueFalseActivity.class;
                break;
            case "SINGLE_CHOICE":
                targetActivity = SingleChoiceActivity.class;
                break;
            case "MULTI_CHOICE":
                targetActivity = MultiChoiceActivity.class;
                break;
            case "TEXT":
                targetActivity = TextActivity.class;
                break;
            case "PUZZLE":
                targetActivity = PuzzleActivity.class;
                break;
            case "SLIDER":
                targetActivity = SliderActivity.class;
                break;
            default:
                Log.e("SingleChoiceActivity", "Unknown question type: " + listQuesType[questionId]);
                return;
        }

        if(targetActivity != null) {
            intent = new Intent(SingleChoiceActivity.this, targetActivity);
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