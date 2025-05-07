package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.after.QCTypeOptionAdapter;
import com.cmloopy.quizzi.models.QuizCreate.after.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTypeText;

import java.util.ArrayList;
import java.util.List;

public class QCQuestionTypeTextFragment extends QCBaseQuestionFragment
        implements QCQuestionTypeTextFieldFragment.OnAnswerAddedListener,
         QCQuestionTypeTextListFragment.OnAnotherAnswerAdded{

    private static final String ARG_RESPONSE_TYPE = "response_type";
    private QuestionTypeText questionTypeText;
    private RecyclerView answersRecyclerView;
    private QCTypeOptionAdapter answerAdapter;
    private final List<TypeTextOption> tempAnswers = new ArrayList<>();

    private QCQuestionTypeTextFieldFragment qcQuestionTypeTextFieldFragment;
    private QCQuestionTypeTextListFragment qcQuestionTypeTextListFragment;

    public static QCQuestionTypeTextFragment newInstance(QuestionTypeText responseChoice) {
        QCQuestionTypeTextFragment fragment = new QCQuestionTypeTextFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_TYPE, responseChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionTypeText = (QuestionTypeText) getArguments().getSerializable(ARG_RESPONSE_TYPE);
            if (questionTypeText == null) {
                questionTypeText = new QuestionTypeText();
            }
            initializeTempAnswers();
        } else {
            questionTypeText = new QuestionTypeText();
            initializeTempAnswers();
        }
    }

    private void initializeTempAnswers() {
        tempAnswers.addAll(questionTypeText.getAcceptedAnswers());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_type_text, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(questionTypeText);
        setupFragments();

        if (tempAnswers.isEmpty()) {
            showTypeAnswerFieldFragment();
        } else {
            showTypeAnswerListFragment();
        }

        return view;
    }

    private void setupFragments() {
        qcQuestionTypeTextFieldFragment = new QCQuestionTypeTextFieldFragment();
        qcQuestionTypeTextListFragment = new QCQuestionTypeTextListFragment();

        qcQuestionTypeTextFieldFragment.setOnAnswerAddedListener(this);
        qcQuestionTypeTextListFragment.setOnAnotherAnswerAddedListener(this);
    }

    private void showTypeAnswerFieldFragment() {
        qcQuestionTypeTextFieldFragment.setAnswers(tempAnswers);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.answers_fragment_container, qcQuestionTypeTextFieldFragment)
                .commit();
    }

    private void showTypeAnswerListFragment() {
        qcQuestionTypeTextListFragment.setAnswers(tempAnswers);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.answers_fragment_container, qcQuestionTypeTextListFragment)
                .commit();
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return questionTypeText;
    }

    @Override
    public void addAnotherAnswer() {
        showTypeAnswerFieldFragment();
    }

    @Override
    public void onDeleteAnswer(int position) {
        if (position >= 0 && position < tempAnswers.size()) {
            tempAnswers.remove(position);
            questionTypeText.setAcceptedAnswers(tempAnswers);
            notifyQuestionUpdated2();

            if (tempAnswers.isEmpty()) {
                showTypeAnswerFieldFragment();
            } else {
                qcQuestionTypeTextListFragment.setAnswers(tempAnswers);
                if (qcQuestionTypeTextListFragment.getAnswerAdapter() != null) {
                    qcQuestionTypeTextListFragment.getAnswerAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    private void notifyQuestionUpdated2() {
        if (listener != null) {
            listener.onUpdateQuestion(questionTypeText.getPosition(), questionTypeText);
        }
    }

    @Override
    public void onAnswerAdded(TypeTextOption answer) {
        tempAnswers.add(answer);
        questionTypeText.setAcceptedAnswers(tempAnswers);
        showTypeAnswerListFragment();
        notifyQuestionUpdated2();
    }
}