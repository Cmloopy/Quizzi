package com.cmloopy.quizzi.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;

public class CreateNewPassword extends AppCompatActivity {

    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private ImageView passwordToggle;
    private ImageView confirmPasswordToggle;
    private CheckBox rememberMeCheckbox;
    private Button continueButton;
    private ImageButton backButton;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_password);

        // Initialize views
        initViews();

        // Set up listeners
        setupListeners();
    }

    private void initViews() {
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        continueButton = findViewById(R.id.continueButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupListeners() {
        // Password visibility toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, passwordToggle));

        // Confirm password visibility toggle
        confirmPasswordToggle.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, confirmPasswordToggle));

        // Back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Continue button click
        continueButton.setOnClickListener(v -> validateAndProceed());

        // Add text watchers to enable/disable continue button based on field values
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                updateContinueButtonState();
            }
        };

        passwordEditText.addTextChangedListener(textWatcher);
        confirmPasswordEditText.addTextChangedListener(textWatcher);
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleButton) {
        boolean isVisible = editText.getTransformationMethod() instanceof HideReturnsTransformationMethod;

        if (isVisible) {
            // Hide password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleButton.setImageResource(R.drawable.ic_visibility);
        }

        // Move cursor to the end of the text
        editText.setSelection(editText.getText().length());
    }

    private void updateContinueButtonState() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Enable button if both fields have text (more validation can be added)
        boolean fieldsNotEmpty = !password.isEmpty() && !confirmPassword.isEmpty();
        continueButton.setEnabled(fieldsNotEmpty);

        // Optional: Change button appearance based on enabled state
        if (fieldsNotEmpty) {
            continueButton.setAlpha(1.0f);
        } else {
            continueButton.setAlpha(0.5f);
        }
    }

    private void validateAndProceed() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password strength (minimum 8 characters, contains number and special character)
        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }

            if (hasNumber && hasSpecial) {
                break;
            }
        }

        if (!hasNumber || !hasSpecial) {
            Toast.makeText(this, "Password must contain at least one number and one special character", Toast.LENGTH_SHORT).show();
            return;
        }

        // If remember me is checked, save to SharedPreferences
        if (rememberMeCheckbox.isChecked()) {
            saveCredentials(password);
        }

        // All validations passed, proceed to next screen
        // For example: startActivity(new Intent(CreateNewPassword.this, HomeActivity.class));
        Toast.makeText(this, "Password created successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveCredentials(String password) {
        // Save credentials to SharedPreferences
        getSharedPreferences("QuizziPrefs", MODE_PRIVATE)
                .edit()
                .putString("password", password)
                .putBoolean("remember_me", true)
                .apply();
    }
}