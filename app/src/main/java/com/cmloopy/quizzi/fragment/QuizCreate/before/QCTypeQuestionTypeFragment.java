package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.TypeQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCTypeQuestionTypeFragment extends QCBaseQuestionFragment
        implements QCTypeAnswerFieldFragment.OnAnswerAddedListener,
        QCTypeAnswerListFragment.OnAnotherAnswerAdded {

    private static final String ARG_QUESTION = "correct_answer";
    private TypeQuestion typeQuestion;
    private Uri selectedImageUri;
    private List<QCAnswer> answers = new ArrayList<>();

    private QCTypeAnswerFieldFragment qcTypeAnswerFieldFragment;
    private QCTypeAnswerListFragment qcTypeAnswerListFragment;

    public static QCTypeQuestionTypeFragment newInstance(TypeQuestion question) {
        QCTypeQuestionTypeFragment fragment = new QCTypeQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putStringArrayList(ARG_QUESTION, new ArrayList<>(question.getCorrectAnswer()));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeQuestion = new TypeQuestion();
        if (getArguments() != null) {
            initializeTypeQuestion(getArguments());
            if(typeQuestion.isDefaultInstance()) {
                setDefaultTypeQuestion();
            }
        } else {
            setDefaultTypeQuestion();
        }
    }

    private void initializeTypeQuestion(Bundle args) {
        super.initializeBaseQuestion(typeQuestion, args);
        typeQuestion.setCorrectAnswer(args.getStringArrayList(ARG_QUESTION));

        List<String> correctAnswers = typeQuestion.getCorrectAnswer() != null ? typeQuestion.getCorrectAnswer() : Collections.emptyList();
        for (String answerText : correctAnswers) {
            answers.add(new QCAnswer(answerText, true, answerBackgroundColor[answers.size() % 4]));
        }
    }

    private void setDefaultTypeQuestion() {
        super.setDefaultBaseQuestion(typeQuestion);
        typeQuestion.setCorrectAnswer(Collections.emptyList());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_text, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(typeQuestion);
        setupFragments();
        setupImageSelection();

        if (answers.isEmpty()) {
            showTypeAnswerFieldFragment();
        } else {
            showTypeAnswerListFragment();
        }

        return view;
    }

    private void setupFragments() {
        qcTypeAnswerFieldFragment = new QCTypeAnswerFieldFragment();
        qcTypeAnswerListFragment = new QCTypeAnswerListFragment();

        qcTypeAnswerFieldFragment.setOnAnswerAddedListener(this);
        qcTypeAnswerListFragment.setOnAnotherAnswerAddedListener(this);
    }

    private void setupImageSelection() {
        coverImageContainer.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    protected void onCoverImageClicked() {
        setupImageSelection();
    }

    @Override
    public Question getCurrentQuestion() {
        return typeQuestion;
    }

    private void showTypeAnswerFieldFragment() {
        qcTypeAnswerFieldFragment.setAnswers(answers);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.answers_fragment_container, qcTypeAnswerFieldFragment)
                .commit();
    }

    private void showTypeAnswerListFragment() {
        qcTypeAnswerListFragment.setAnswers(answers);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.answers_fragment_container, qcTypeAnswerListFragment)
                .commit();
    }

    @Override
    public void onAnswerAdded(QCAnswer answer) {
        answers.add(answer);
        typeQuestion.setCorrectAnswer(getAnswerTexts());
        showTypeAnswerListFragment();
        notifyQuestionUpdated2();
    }

    @Override
    public void addAnotherAnswer() {
        showTypeAnswerFieldFragment();
    }

    @Override
    public void onDeleteAnswer(int position) {
        if (position >= 0 && position < answers.size()) {
            answers.remove(position);
            typeQuestion.setCorrectAnswer(getAnswerTexts());
            notifyQuestionUpdated2();

            if (answers.isEmpty()) {
                showTypeAnswerFieldFragment();
            } else {
                qcTypeAnswerListFragment.setAnswers(answers);
                if (qcTypeAnswerListFragment.getAnswerAdapter() != null) {
                    qcTypeAnswerListFragment.getAnswerAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    private List<String> getAnswerTexts() {
        List<String> answerTexts = new ArrayList<>();
        for (QCAnswer answer : answers) {
            answerTexts.add(answer.getText());
        }
        return answerTexts;
    }

    private void notifyQuestionUpdated2() {
        if (listener != null) {
            listener.onUpdateQuestion(typeQuestion.getPosition(), typeQuestion);
        }
    }
}
