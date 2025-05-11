package com.cmloopy.quizzi.fragment.QuestionCreate;

import com.cmloopy.quizzi.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QCQuestionEmptyPlaceHolder extends Fragment {
    public QCQuestionEmptyPlaceHolder() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_empty_placeholder, container, false);
    }
}
