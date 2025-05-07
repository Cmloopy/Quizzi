package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;

public class SetiingActivity extends AppCompatActivity {

    private LinearLayout lnPerInfo;
    private LinearLayout musicEffect;
    private LinearLayout aboutQuizzo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setiing);

        lnPerInfo = findViewById(R.id.personal_info_layout);
        musicEffect = findViewById(R.id.music_effects_layout);
        aboutQuizzo = findViewById(R.id.about_quizzo_layout);

        lnPerInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonalInfoActivity.class);
            startActivity(intent);
        });
        musicEffect.setOnClickListener(v -> {
            Intent intent = new Intent(this, MusicEffectsActivity.class);
            startActivity(intent);
        });
        aboutQuizzo.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutQuizzoActivity.class);
            startActivity(intent);
        });
    }
}