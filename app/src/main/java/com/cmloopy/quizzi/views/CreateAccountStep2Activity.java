package com.cmloopy.quizzi.views;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.cmloopy.quizzi.R;

import java.util.regex.Pattern;

public class CreateAccountStep2Activity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private CheckBox rememberMeCheckbox;
    private Button signUpButton;
    private CardView googleButton;
    private CardView appleButton;
    private ImageButton backButton;
    private View usernameDivider;
    private View emailDivider;
    private View passwordDivider;
    private ImageView usernameCheck;
    private ImageButton passwordToggle;
    private TextView usernameErrorText;
    private TextView emailErrorText;
    private TextView passwordErrorText;

    // Validation patterns
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    private boolean isFirstSignUpClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_step2);

        // Initialize views
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberMeCheckbox = findViewById(R.id.rememberMeCheckbox);
        signUpButton = findViewById(R.id.signUpButton);
        googleButton = findViewById(R.id.googleButton);
        appleButton = findViewById(R.id.appleButton);
        backButton = findViewById(R.id.backButton);
        usernameDivider = findViewById(R.id.usernameDivider);
        emailDivider = findViewById(R.id.emailDivider);
        passwordDivider = findViewById(R.id.passwordDivider);
        usernameCheck = findViewById(R.id.usernameCheck);
        passwordToggle = findViewById(R.id.passwordToggle);
        usernameErrorText = findViewById(R.id.usernameErrorText);
        emailErrorText = findViewById(R.id.emailErrorText);
        passwordErrorText = findViewById(R.id.passwordErrorText);

        // Setup focus and text change listeners
        // Setup sign up button
        signUpButton.setOnClickListener(v -> {
            isFirstSignUpClick = true;
            if(isFirstSignUpClick) {
                setupValidationListeners();
            }

            if (validateAllFields()) {
                showSuccessDialog();
            }
        });

        // Setup Google sign in
        googleButton.setOnClickListener(v -> {
            Toast.makeText(CreateAccountStep2Activity.this, "Google sign-in would be initiated", Toast.LENGTH_SHORT).show();
        });

        // Setup Apple sign in
        appleButton.setOnClickListener(v -> {
            Toast.makeText(CreateAccountStep2Activity.this, "Apple sign-in would be initiated", Toast.LENGTH_SHORT).show();
        });

        // Setup password toggle
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Setup back button
        backButton.setOnClickListener(v -> finish());
    }

    private void setupValidationListeners() {
        setupEditTextValidation(usernameInput, usernameDivider, usernameCheck, usernameErrorText);
        setupEditTextValidation(emailInput, emailDivider, null, emailErrorText);
        setupEditTextValidation(passwordInput, passwordDivider, passwordToggle, passwordErrorText);
    }

    private void setupEditTextValidation(EditText editText, View divider, View icon, TextView errorText) {
        // Handle focus changes
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateField(editText);
            }
            updateEditTextState(editText, divider, icon, errorText);
        });

        // Handle text changes

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.hasFocus()) {
                    validateField(editText);
                }
                updateEditTextState(editText, divider, icon, errorText);
            }
        });
    }

    private boolean validateField(EditText editText) {
        String text = editText.getText().toString().trim();
        boolean isValid = false;
        String errorMessage = "";

        if (editText == usernameInput) {
            if (text.isEmpty()) {
                errorMessage = "Username cannot be empty";
            } else if (!USERNAME_PATTERN.matcher(text).matches()) {
                errorMessage = "Username must be 4-20 characters using only letters, numbers, and underscores";
            } else {
                isValid = true;
            }
            usernameErrorText.setText(errorMessage);
            usernameErrorText.setVisibility(isValid ? View.GONE : View.VISIBLE);
            usernameCheck.setVisibility(isValid && !text.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (editText == emailInput) {
            if (text.isEmpty()) {
                errorMessage = "Email cannot be empty";
            } else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                errorMessage = "Please enter a valid email address";
            } else {
                isValid = true;
            }
            emailErrorText.setText(errorMessage);
            emailErrorText.setVisibility(isValid ? View.GONE : View.VISIBLE);
        } else if (editText == passwordInput) {
            if (text.isEmpty()) {
                errorMessage = "Password cannot be empty";
            } else if (!PASSWORD_PATTERN.matcher(text).matches()) {
                errorMessage = "Password must be at least 8 characters with uppercase, lowercase, number, and special character";
            } else {
                isValid = true;
            }
            passwordErrorText.setText(errorMessage);
            passwordErrorText.setVisibility(isValid ? View.GONE : View.VISIBLE);
        }

        return isValid;
    }

    private void updateEditTextState(EditText editText, View divider, View icon, TextView errorText) {
        int purpleColor = ContextCompat.getColor(this, R.color.purple);
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);
        int errorColor = ContextCompat.getColor(this, R.color.errorColor);
        int validColor = ContextCompat.getColor(this, R.color.validColor);

        boolean hasText = editText.getText().toString().trim().length() > 0;
        boolean hasFocus = editText.hasFocus();
        boolean hasError = errorText != null && errorText.getVisibility() == View.VISIBLE;
        boolean isValid = hasText && validateField(editText);

        // Choose color based on state
        int color;
        if (hasError) {
            color = errorColor;
        } else if (isValid) {
            color = validColor; // Use valid color for validated fields
        } else if (hasFocus) {
            color = purpleColor;
        } else {
            color = grayColor;
        }

        // Update divider color
        if (divider != null) {
            divider.setBackgroundColor(color);
        }

        // Update icon tint if available
        if (icon != null) {
            if (icon instanceof ImageView) {
                ((ImageView) icon).setColorFilter(color);
                // Show check icon only if field is valid and has text
                if (icon == usernameCheck) {
                    icon.setVisibility(isValid ? View.VISIBLE : View.GONE);
                }
            } else if (icon instanceof ImageButton) {
                ((ImageButton) icon).setColorFilter(hasFocus || hasError ? color : grayColor);
            }
        }
    }

    private void togglePasswordVisibility() {
        if (passwordInput.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Show password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_08_password_visibility_off);
        } else {
            // Hide password
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_08_password_visibility);
        }
        // Move cursor to the end of text
        passwordInput.setSelection(passwordInput.getText().length());

        // Make sure color is appropriate after toggle
        updateEditTextState(passwordInput, passwordDivider, passwordToggle, passwordErrorText);
    }

    private boolean validateAllFields() {
        boolean usernameValid = validateField(usernameInput);
        boolean emailValid = validateField(emailInput);
        boolean passwordValid = validateField(passwordInput);

        // Update UI for all fields to show errors
        updateEditTextState(usernameInput, usernameDivider, usernameCheck, usernameErrorText);
        updateEditTextState(emailInput, emailDivider, null, emailErrorText);
        updateEditTextState(passwordInput, passwordDivider, passwordToggle, passwordErrorText);

        return usernameValid && emailValid && passwordValid;
    }

    private void showSuccessDialog() {
        Dialog successDialog = new Dialog(this);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.ui_09_success_dialog);
        Window window = successDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(layoutParams);
        }
        successDialog.show();
        Toast.makeText(CreateAccountStep2Activity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CreateAccountStep2Activity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 10000);
    }
}