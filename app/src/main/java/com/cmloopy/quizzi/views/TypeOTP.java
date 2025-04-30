package com.cmloopy.quizzi.views;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cmloopy.quizzi.R;

public class TypeOTP extends AppCompatActivity {

    private EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4;
    private TextView resendCodeText;
    private CountDownTimer resendTimer;
    private static final long RESEND_TIMER_DURATION = 60000; // 60 giây
    private static final long RESEND_TIMER_INTERVAL = 1000; // 1 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_otp);

        // Thiết lập để bàn phím hiển thị khi activity được mở
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Khởi tạo các EditText
        otpDigit1 = findViewById(R.id.otpDigit1);
        otpDigit2 = findViewById(R.id.otpDigit2);
        otpDigit3 = findViewById(R.id.otpDigit3);
        otpDigit4 = findViewById(R.id.otpDigit4);

        // Khởi tạo TextView đếm ngược
        resendCodeText = findViewById(R.id.resendCodeText);

        // Thiết lập focus và hiển thị bàn phím cho ô đầu tiên
        otpDigit1.requestFocus();

        // Thiết lập các sự kiện focus change và input
        setupOtpInputs();

        // Bắt đầu đếm ngược thời gian
        startResendTimer();
    }

    private void setupOtpInputs() {
        // Tạo mảng chứa tất cả các ô OTP để dễ quản lý
        EditText[] otpDigits = {otpDigit1, otpDigit2, otpDigit3, otpDigit4};

        // Thiết lập sự kiện focus change cho từng ô OTP
        View.OnFocusChangeListener otpFocusChangeListener = (view, hasFocus) -> {
            EditText currentEditText = (EditText) view;
            if (hasFocus) {
                // Khi có focus, đổi background thành selected
                currentEditText.setBackgroundResource(R.drawable.otp_box_selected);
                // Hiển thị bàn phím
                showKeyboard(currentEditText);
            } else {
                // Khi mất focus, đổi về background mặc định
                currentEditText.setBackgroundResource(R.drawable.otp_box_background);
            }
        };

        // Thiết lập OnFocusChangeListener cho tất cả các ô OTP
        for (EditText digit : otpDigits) {
            digit.setOnFocusChangeListener(otpFocusChangeListener);

            // Thiết lập sự kiện click để yêu cầu focus
            digit.setOnClickListener(v -> {
                EditText clickedEditText = (EditText) v;
                clickedEditText.requestFocus();
            });
        }

        // Ban đầu, đặt background của ô đầu tiên là selected vì nó có focus
        otpDigit1.setBackgroundResource(R.drawable.otp_box_selected);

        // Thiết lập tự động chuyển focus khi nhập xong một ô
        otpDigit1.addTextChangedListener(new OtpTextWatcher(otpDigit1, otpDigit2));
        otpDigit2.addTextChangedListener(new OtpTextWatcher(otpDigit2, otpDigit3));
        otpDigit3.addTextChangedListener(new OtpTextWatcher(otpDigit3, otpDigit4));
        otpDigit4.addTextChangedListener(new OtpTextWatcher(otpDigit4, null));
    }

    private void startResendTimer() {
        // Khởi tạo và bắt đầu bộ đếm thời gian
        resendTimer = new CountDownTimer(RESEND_TIMER_DURATION, RESEND_TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật text hiển thị thời gian còn lại (chuyển đổi từ milli giây sang giây)
                long secondsRemaining = millisUntilFinished / 1000;
                resendCodeText.setText(getString(R.string.resend_code_timer_seconds, secondsRemaining));
            }

            @Override
            public void onFinish() {
                // Khi hết thời gian, cho phép gửi lại mã
                resendCodeText.setText(R.string.resend_code_now);
                resendCodeText.setTextColor(getResources().getColor(R.color.purple_500, null));
                // Thêm khả năng click để gửi lại mã
                resendCodeText.setClickable(true);
                resendCodeText.setOnClickListener(v -> resendCode());
            }
        }.start();
    }

    private void resendCode() {
        // Logic để gửi lại mã OTP
        // ...

        // Sau khi gửi lại, reset đếm ngược
        resendCodeText.setClickable(false);
        resendCodeText.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
        startResendTimer();
    }

    private void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bộ đếm thời gian để tránh memory leak
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }

    // Lớp để tự động chuyển focus sang ô tiếp theo khi nhập xong
    private class OtpTextWatcher implements android.text.TextWatcher {
        private EditText currentEditText;
        private EditText nextEditText;

        public OtpTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Không cần thực hiện gì
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Không cần thực hiện gì
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
        }
    }
}