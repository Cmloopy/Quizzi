package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;

public class UI_40_generate_qr extends AppCompatActivity {
    ImageView ui40BtnBack;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_40_generate_qr);
        ui40BtnBack = findViewById(R.id.ui40BtnBack);
        ui40BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}
