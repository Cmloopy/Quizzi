package com.cmloopy.quizzi.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.UserApi;
import com.cmloopy.quizzi.models.user.CheckLoginUser;
import com.cmloopy.quizzi.models.user.LoginResponse;

import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInForm extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageButton backButton;
    private Button signInButton;
    private CheckBox rememberMeCheckbox;
    private TextView forgotPasswordText;
    private ImageView passwordToggle;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "SignInPrefs";
    private static final String EMAIL_KEY = "email";
    private static final String REMEMBER_KEY = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in_form);

        // Initialize UI components
        initializeViews();

        // Set up SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Restore saved email if remember me was checked
        if (sharedPreferences.getBoolean(REMEMBER_KEY, false)) {
            emailEditText.setText(sharedPreferences.getString(EMAIL_KEY, ""));
            rememberMeCheckbox.setChecked(true);
        }

        // Set up click listeners
        setupListeners();

        // Configure edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        backButton = findViewById(R.id.backButton);
        signInButton = findViewById(R.id.signInButton);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        passwordToggle = findViewById(R.id.passwordToggle);
    }

    private void setupListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> finish());

        // Sign In button click listener
        signInButton.setOnClickListener(v -> attemptSignIn());

        // Forgot Password click listener
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());

        // Password visibility toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            // Show password
            passwordToggle.setImageResource(R.drawable.ic_visibility_off);
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // Hide password
            passwordToggle.setImageResource(R.drawable.ic_visibility);
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        // Move cursor to the end of text
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void attemptSignIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input fields
        if (!validateInputs(email, password)) {
            return;
        }

        // Save email if "Remember me" is checked
        if (rememberMeCheckbox.isChecked()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(EMAIL_KEY, email);
            editor.putBoolean(REMEMBER_KEY, true);
            editor.apply();
        } else {
            // Clear saved preferences if not checked
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        performSignIn(email, password);
    }

    private boolean isValidGmail(String email) {
        String gmailPattern = "[a-zA-Z0-9._-]+@gmail\\.com";
        return email.matches(gmailPattern);
    }

    private boolean validateInputs(String email, String password) {
        boolean isValid = true;

        // Check if email is valid
        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            isValid = false;
        } /*else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            isValid = false;
        } else if (!isValidGmail(email)) {
            emailEditText.setError("Please enter a valid Gmail address");
            isValid = false;
        } else {
            emailEditText.setError(null);
        }*/

        // Check if password is valid
        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            isValid = false;
        }
        /*} else if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters");
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            passwordEditText.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
            isValid = false;
        } else {
            passwordEditText.setError(null);
        }*/

        return isValid;
    }

    private boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(passwordPattern);
    }

    private void performSignIn(String email, String password) {
        CheckLoginUser loginRequest = new CheckLoginUser(email, password);

        UserApi userApi = RetrofitClient.getUserApi();

        Call<LoginResponse> call = userApi.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d("LOGIN", "Login success!");
                    Log.d("LOGIN", loginResponse.getUserId() + "");
                    Intent intent = new Intent(SignInForm.this, MainActivity.class);
                    intent.putExtra("idUser",loginResponse.getUserId());
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LOGIN", "Login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN", "Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void handleForgotPassword() {
        Intent intent = new Intent(SignInForm.this, ForgotPassWord.class);
        startActivity(intent);
    }
}