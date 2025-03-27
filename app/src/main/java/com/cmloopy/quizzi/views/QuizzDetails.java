package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizzDetailsQuestionAdapter;
import com.cmloopy.quizzi.models.QuizzDetailsQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuizzDetails extends AppCompatActivity {
    private RecyclerView questionRecyclerView;
    private QuizzDetailsQuestionAdapter questionAdapter;
    private List<QuizzDetailsQuestion> questionList;
    private ImageView btnEdit;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quizz_details_full_page);

        questionRecyclerView = findViewById(R.id.quizDetailsQuestionRecyclerView);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionRecyclerView.setHasFixedSize(true);

        // Tạo danh sách câu hỏi
        questionList = new ArrayList<>();
        questionList.add(new QuizzDetailsQuestion("1 - Quiz", "Do you go to school by bus?", R.drawable.img_02));
        questionList.add(new QuizzDetailsQuestion("2 - True or False", "This is a book? True or False?", R.drawable.img_02));
        questionList.add(new QuizzDetailsQuestion("3 - Puzzle", "Order the following simple words!", R.drawable.img_02));
        questionList.add(new QuizzDetailsQuestion("4 - Type Answer", "What does the illustration above describe?", R.drawable.img_02));
        questionList.add(new QuizzDetailsQuestion("5 - Quiz + Audio", "What does the audio say?", R.drawable.img_02));

        // Gán Adapter cho RecyclerView
        questionAdapter = new QuizzDetailsQuestionAdapter(this, questionList);
        questionRecyclerView.setAdapter(questionAdapter);

        btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
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
                popupWindow.dismiss();
            }
        });
    }
}