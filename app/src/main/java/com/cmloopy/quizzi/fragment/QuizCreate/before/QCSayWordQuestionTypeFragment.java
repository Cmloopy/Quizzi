package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.SayWordQuestion;

public class QCSayWordQuestionTypeFragment extends QCBaseQuestionFragment {
    private static final String ARGS_WORD = "word";
    private static final String ARGS_PRONUNCIATION = "pronunciation";
    private SayWordQuestion sayWordQuestion;
    public static QCSayWordQuestionTypeFragment newInstance(SayWordQuestion question) {
        QCSayWordQuestionTypeFragment fragment = new QCSayWordQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putString(ARGS_WORD, question != null ? question.getWord() : "");
        args.putString(ARGS_PRONUNCIATION, question != null ? question.getPronunciation() : "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sayWordQuestion = new SayWordQuestion();
        if (getArguments() != null) {
            initializeSayWordQuestion(getArguments());
            if (sayWordQuestion.isDefaultInstance()) {
                setDefaultSayWordQuestion();
            }
        } else {
            setDefaultSayWordQuestion();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_say_word, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(sayWordQuestion);
        setupListeners();
        return view;
    }

    private void initializeSayWordQuestion(Bundle args) {
        super.initializeBaseQuestion(sayWordQuestion, args);
        sayWordQuestion.setWord(args.getString(ARGS_WORD, ""));
        sayWordQuestion.setPronunciation(args.getString(ARGS_PRONUNCIATION, ""));
    }

    private void setDefaultSayWordQuestion() {
        super.setDefaultBaseQuestion(sayWordQuestion);
        sayWordQuestion.setWord("");
        sayWordQuestion.setPronunciation("");
    }

    private void setupListeners() {

    }

    @Override
    protected void onCoverImageClicked() {

    }

    @Override
    public Question getCurrentQuestion() {
        return sayWordQuestion;
    }
}
