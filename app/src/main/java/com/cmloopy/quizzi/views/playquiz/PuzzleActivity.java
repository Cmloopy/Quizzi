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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.playquiz.PuzzleAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.databinding.ActivityPuzzleBinding;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.puzzle.PuzzlePiece;
import com.cmloopy.quizzi.models.question.puzzle.PuzzleQuestion;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.cmloopy.quizzi.adapter.playquiz.ItemMoveCallback;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PuzzleActivity extends AppCompatActivity {
    private ActivityPuzzleBinding binding;

    private int totalScore;
    private int userId;
    private long quizId;
    QuestionApi questionApi = RetrofitClient.getQuestionApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPuzzleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalScore = getIntent().getIntExtra("totalPoint", 0);
        int questionId = getIntent().getIntExtra("questionId", -1);
        int[] listQuesId = getIntent().getIntArrayExtra("listIdQues");
        String []listQuesType = getIntent().getStringArrayExtra("listTypeQues");
        userId = getIntent().getIntExtra("userId",-1);
        quizId = getIntent().getLongExtra("quizId",-1);

        binding.txtNumAnswerPuzzle.setText(((questionId+1) + "") + "/" + (listQuesId.length + ""));
        binding.progressBarTimePuzzle.setProgress(100);

        Call<Question> ques = questionApi.getQuestionById(listQuesId[questionId]);
        ques.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Question q = response.body();
                    if (q instanceof PuzzleQuestion) {
                        binding.txtTitlePuzzle.setText(q.content);
                        PuzzleQuestion puzzleQuestion = (PuzzleQuestion) q;
                        List<PuzzlePiece> options = puzzleQuestion.puzzlePieces;
                        PuzzleAdapter adapter = new PuzzleAdapter(PuzzleActivity.this, options);
                        binding.txtTitlePuzzle.setText(puzzleQuestion.content);
                        binding.recyclerViewPuzzle.setLayoutManager(new LinearLayoutManager(PuzzleActivity.this));
                        binding.recyclerViewPuzzle.setAdapter(adapter);

                        ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(binding.recyclerViewPuzzle);

                        binding.materialButtonSubmitPuzzle.setOnClickListener(v -> {
                            List<PuzzlePiece> currentList = adapter.answerList;

                            boolean isCorrect = true;
                            for (int i = 0; i < currentList.size(); i++) {
                                if (currentList.get(i).correctPosition != i) {
                                    isCorrect = false;
                                    break;
                                }
                            }
                            if (isCorrect) {
                                totalScore += puzzleQuestion.getPoint();
                                int myColor = ContextCompat.getColor(PuzzleActivity.this, R.color.correct_green);
                                binding.notiStatusPuzzle.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointPuzzle.setText("+"+ (puzzleQuestion.point+"") + "pts");
                                binding.notiStatusPuzzle.setVisibility(View.VISIBLE);
                            } else {
                                Log.e("Puzzle", "Wrong");
                                int myColor = ContextCompat.getColor(PuzzleActivity.this, R.color.incorrect_light_red);
                                binding.notiStatusPuzzle.setBackgroundTintList(ColorStateList.valueOf(myColor));
                                binding.textCountPointPuzzle.setText("No Problem! Try Again!!");
                                binding.txtStatusPz.setText("Incorrect!");
                                binding.notiStatusPuzzle.setVisibility(View.VISIBLE);
                            }
                            Log.e("totalPoint", totalScore+ "");
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (questionId + 1 < listQuesId.length) {
                                    nextQuestion(questionId + 1, totalScore, listQuesId, listQuesType);
                                } else {
                                    Intent intent = new Intent(PuzzleActivity.this, ReviewActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, TrueFalseActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, SingleChoiceActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, MultiChoiceActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, TextActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, PuzzleActivity.class);
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
            Intent intent = new Intent(PuzzleActivity.this, SliderActivity.class);
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