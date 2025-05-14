package com.cmloopy.quizzi.fragment.QuestionCreate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSayWord;

public class QCQuestionSayWordFragment extends QCBaseQuestionFragment {

    private static final String ARG_RESPONSE_SAY_WORD = "response_choice";
    private QuestionCreateSayWord questionSayWord;

    public static QCQuestionSayWordFragment newInstance(QuestionCreateSayWord questionSayWord) {
        QCQuestionSayWordFragment fragment = new QCQuestionSayWordFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_SAY_WORD, questionSayWord);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionSayWord = (QuestionCreateSayWord) getArguments().getSerializable(ARG_RESPONSE_SAY_WORD);
            if (questionSayWord == null) {
                questionSayWord = new QuestionCreateSayWord();
            }
        } else {
            questionSayWord = new QuestionCreateSayWord();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_say_word, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(getCurrentQuestion());
        return view;
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public QuestionCreate getCurrentQuestion() {
        return questionSayWord;
    }
}