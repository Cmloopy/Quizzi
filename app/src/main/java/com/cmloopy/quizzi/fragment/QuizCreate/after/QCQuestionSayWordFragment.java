package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionSayWord;

public class QCQuestionSayWordFragment extends QCBaseQuestionFragment {

    private static final String ARG_RESPONSE_SAY_WORD = "response_choice";
    private QuestionSayWord questionSayWord;

    public static QCQuestionSayWordFragment newInstance(QuestionSayWord questionSayWord) {
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
            questionSayWord = (QuestionSayWord) getArguments().getSerializable(ARG_RESPONSE_SAY_WORD);
            if (questionSayWord == null) {
                questionSayWord = new QuestionSayWord();
            }
        } else {
            questionSayWord = new QuestionSayWord();
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
    public Question getCurrentQuestion() {
        return questionSayWord;
    }
}