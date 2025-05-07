package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;

public class UI43 extends AppCompatActivity {

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_43);

        progressBar = findViewById(R.id.UI43progressBar);

        // Giả lập tiến trình loading
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 5;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Chuyển sang màn hình chính khi loading xong
            startActivity(new Intent(UI43.this, MainActivity.class));
            finish();
        }).start();
    }
}

