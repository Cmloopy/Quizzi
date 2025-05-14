package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizzDetailsQuestionAdapter;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionApi;
import com.cmloopy.quizzi.data.api.QuizzApi;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.quiz.QuizResponse;
import com.cmloopy.quizzi.models.user.LoginResponse;
import com.cmloopy.quizzi.models.user.User;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizzDetails extends AppCompatActivity {
    private RecyclerView questionRecyclerView;
    private QuizzDetailsQuestionAdapter questionAdapter;
    private ImageView btnEdit;
    private PopupWindow popupWindow;
    private Button btn;
    private int idUser = -1;
    private long idQuizz = -1;
    UserApi userApi = RetrofitClient.getUserApi();
    QuizzApi quizzApi = RetrofitClient.getQuizzApi();
    QuestionApi questionApi = RetrofitClient.getQuestionApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizz_details_full_page);
        idQuizz = getIntent().getLongExtra("quizId", -1);
        idUser = getIntent().getIntExtra("userId", -1);
        Log.e("QuizApi", idQuizz+"");
        Log.e("UdUser", idUser+"");

        Call<QuizResponse> quizResponseCall = quizzApi.getQuizById(idQuizz);
        quizResponseCall.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuizResponse quizResponse = response.body();
                    TextView txtTitileQuiz = findViewById(R.id.quizTitle);
                    TextView quizDescriptionDetail = findViewById(R.id.quizDescriptionDetail);
                    txtTitileQuiz.setText(quizResponse.getTitle());
                    quizDescriptionDetail.setText(quizResponse.getDescription());

                    setUpInfoUser(quizResponse.getUserId());
                } else {
                    Log.e("quizz", "failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable t) {
                Log.e("Quizz", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });

        setUpRecycleView();

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        btn = findViewById(R.id.btn_play);
        btn.setOnClickListener(v->{
            Intent intent = new Intent(this, UI43.class);
            intent.putExtra("userId", idUser);
            intent.putExtra("quizId", idQuizz);
            startActivity(intent);
        });
    }

    private void setUpInfoUser(int idAuthor) {
        Call<User> user = userApi.getInfoUserById(idAuthor);
        user.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User userr = response.body();
                    TextView txtNameAuthor = findViewById(R.id.userName);
                    TextView txtHandler = findViewById(R.id.userHandle);
                    txtNameAuthor.setText(userr.getFullName() + " " + userr.getLastName());
                    txtHandler.setText(userr.getFullName().toLowerCase() + "_" + userr.getLastName().toLowerCase());
                } else {
                    Log.e("get info author", "failed: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("info author", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void setUpRecycleView() {
        Call<List<Question>> calll = questionApi.getQuestionByQuiz(idQuizz);
        calll.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null){
                    List<Question> questionList = response.body();

                    questionRecyclerView = findViewById(R.id.quizDetailsQuestionRecyclerView);
                    questionRecyclerView.setLayoutManager(new LinearLayoutManager(QuizzDetails.this));
                    questionRecyclerView.setHasFixedSize(true);

                    questionAdapter = new QuizzDetailsQuestionAdapter(QuizzDetails.this, questionList);
                    questionRecyclerView.setAdapter(questionAdapter);
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

    private void showPopupMenu(View anchorView) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }

        // Inflate layout của popup menu
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_menu_quiz__details, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(8);

        // Lấy vị trí của btnEdit để đặt menu ngay bên dưới
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        popupWindow.showAsDropDown(anchorView, 0, 10);

        // Bắt sự kiện click vào menu item
        LinearLayout menuShare = popupView.findViewById(R.id.menuShare);
        LinearLayout menuGenerateQR = popupView.findViewById(R.id.menuGenerateQR);

        menuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuizzDetails.this, "Sharing quiz...", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        menuGenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuizzDetails.this, "Generating QR Code...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizzDetails.this, UI_40_generate_qr.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }
}