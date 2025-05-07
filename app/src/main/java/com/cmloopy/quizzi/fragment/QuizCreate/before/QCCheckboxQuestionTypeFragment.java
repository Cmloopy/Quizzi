package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.before.QCCheckboxAnswerAdapter;
import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;
import com.cmloopy.quizzi.models.QuizCreate.before.CheckboxQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.before.QCAnswerCreateDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCCheckboxQuestionTypeFragment extends QCBaseQuestionFragment implements QCCheckboxAnswerAdapter.OnAnswerClickListener {

    private static final String ARG_OPTIONS = "options";
    private static final String ARG_CORRECT_ANSWERS = "correct_answers";

    private CheckboxQuestion checkboxQuestion;
    private RecyclerView answersRecyclerView;
    private QCCheckboxAnswerAdapter answerAdapter;
    private final List<QCAnswer> answers = new ArrayList<>();


    public static QCCheckboxQuestionTypeFragment newInstance(CheckboxQuestion question) {
        QCCheckboxQuestionTypeFragment fragment = new QCCheckboxQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(question.getOptions()));
        args.putBooleanArray(ARG_CORRECT_ANSWERS, toPrimitiveArray(question.getCorrectAnswers()));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkboxQuestion = new CheckboxQuestion();
        if (getArguments() != null) {
            initializeCheckboxQuestion(getArguments());
            if (checkboxQuestion.isDefaultInstance()) {
                setDefaultCheckboxQuestion();
            }
        } else {
            setDefaultCheckboxQuestion();
        }
    }

    private void initializeCheckboxQuestion(Bundle args) {
        super.initializeBaseQuestion(checkboxQuestion, args);
        checkboxQuestion.setOptions(args.getStringArrayList(ARG_OPTIONS));
        checkboxQuestion.setCorrectAnswers(toBooleanList(args.getBooleanArray(ARG_CORRECT_ANSWERS)));

        List<String> options = checkboxQuestion.getOptions() != null ? checkboxQuestion.getOptions() : Collections.emptyList();
        List<Boolean> correctAnswers = checkboxQuestion.getCorrectAnswers() != null ? checkboxQuestion.getCorrectAnswers() : Collections.emptyList();

        for (int i = 0; i < options.size(); i++) {
            boolean isCorrect = i < correctAnswers.size() && correctAnswers.get(i);
            answers.add(new QCAnswer(options.get(i), isCorrect, answerBackgroundColor[i % 4]));
        }
    }

    private void setDefaultCheckboxQuestion() {
        super.setDefaultBaseQuestion(checkboxQuestion);
        checkboxQuestion.setOptions(Collections.emptyList());
        checkboxQuestion.setCorrectAnswers(Collections.emptyList());

        for (int color : answerBackgroundColor) {
            QCAnswer _answer = new QCAnswer("", false, color);
            answers.add(_answer);
        }
        answers.get(0).setCorrect(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_checkbox, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(checkboxQuestion);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);
        answerAdapter = new QCCheckboxAnswerAdapter(requireContext(), answers, this);
        answersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        answersRecyclerView.setAdapter(answerAdapter);
        answersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 20;
                int position = answersRecyclerView.getChildAdapterPosition(view);
                if(position > 0) {
                    outRect.top = spacing;
                }
            }
        });
    }

    @Override
    public void onAnswerClick(int position) {
        new QCAnswerCreateDialog.Builder(getContext())
                .setHeaderTitle("Create Answer")
                .setMultipleChoiceQuestion(true)
                .setListener(new QCAnswerCreateDialog.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, QCAnswer answer) {
                        answers.set(position, answer);
                        answerAdapter.notifyItemChanged(position);

                        List<String> options = new ArrayList<>();
                        List<Boolean> correctAnswers = new ArrayList<>();

                        answers.forEach(_answer -> {
                            options.add(_answer.getText());
                            correctAnswers.add(_answer.isCorrect());
                        });


                        checkboxQuestion.setOptions(options);
                        checkboxQuestion.setCorrectAnswers(correctAnswers);
                        if(listener != null) {
                            listener.onUpdateQuestion(checkboxQuestion.getPosition(), checkboxQuestion);
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemSelectedUpdateAll(List<QCAnswer> updatedAnswers) {
                        for (int i = 0; i < answers.size(); i++) {
                            QCAnswer sourceAnswer = answers.get(i);
                            QCAnswer targetAnswer = updatedAnswers.get(i);
                            sourceAnswer.setCorrect(targetAnswer.isCorrect());
                        }
                        if(listener != null) {
                            listener.onUpdateQuestion(checkboxQuestion.getPosition(), checkboxQuestion);
                        }
                        answerAdapter.notifyDataSetChanged();
                    }
                })
                .setAnswer(answers.get(position))
                .setPosition(position)
                .setAnswers(answers)
                .build()
                .show();
    }

    private void updateQuestionList() {

    }
    @Override
    public void onAnswerLongClick(int position) {
        Toast.makeText(getContext(), "Long click on Answer " + (position + 1), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return checkboxQuestion;
    }

    private static boolean[] toPrimitiveArray(List<Boolean> list) {
        if (list == null) return new boolean[0];
        boolean[] array = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static List<Boolean> toBooleanList(boolean[] array) {
        List<Boolean> list = new ArrayList<>();
        if (array != null) {
            for (boolean b : array) {
                list.add(b);
            }
        }
        return list;
    }
}
