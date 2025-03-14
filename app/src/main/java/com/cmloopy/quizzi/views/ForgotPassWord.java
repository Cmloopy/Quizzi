package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cmloopy.quizzi.R;

public class ForgotPassWord extends AppCompatActivity {

    private EditText emailEditText;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_word);

        emailEditText = findViewById(R.id.emailEditText);
        continueButton = findViewById(R.id.continueButton);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });
    }

    private void validateEmail() {
        String email = emailEditText.getText().toString().trim();

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email hợp lệ
            continueButton.setEnabled(true);
        } else {
            // Email không hợp lệ
            continueButton.setEnabled(false);
        }
    }
}