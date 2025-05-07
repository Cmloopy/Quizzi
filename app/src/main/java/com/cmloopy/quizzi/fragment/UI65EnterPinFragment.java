package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;

public class UI65EnterPinFragment extends Fragment {

    private EditText etPin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_65_enter_pin_fragment, container, false);

        etPin = view.findViewById(R.id.etPin);
        etPin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                etPin.setHint(s.length() > 0 ? "" : "Enter PIN");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        view.findViewById(R.id.btnJoinNow).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Joining game...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}

