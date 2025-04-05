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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.before.QCAnswerAdapter;
import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizAudioQuestion;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.before.QCAnswerCreateDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


//Here’s a code snippet that has the format and logic I want to keep. Below is another code snippet with a different class but similar functionality. Please merge the format of the first code into the second one with second one class remember to implement fully its attribute.
//Note: the anything related to layout of second class should keep except setting the default answer if the fragment use class have list answer  or list of something

public class QCQuizAudioQuestionTypeFragment extends QCBaseQuestionFragment implements QCAnswerAdapter.OnAnswerClickListener {
    private static final String ARG_AUDIO_URL = "audio_url";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_CORRECT_INDEX = "correct_index";

    private QuizAudioQuestion quizAudioQuestion;
    private RecyclerView answersRecyclerView;
    private QCAnswerAdapter answerAdapter;
    private final List<QCAnswer> answers = new ArrayList<>();

    public static QCQuizAudioQuestionTypeFragment newInstance(QuizAudioQuestion question) {
        QCQuizAudioQuestionTypeFragment fragment = new QCQuizAudioQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putString(ARG_AUDIO_URL, question.getAudioUrl());
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(question.getOptions()));
        args.putInt(ARG_CORRECT_INDEX, question.getCorrectOptionIndex());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizAudioQuestion = new QuizAudioQuestion();
        if (getArguments() != null) {
            initializeQuizAudioQuestion(getArguments());
            if (quizAudioQuestion.isDefaultInstance()) {
                setDefaultQuizQuestion();
            }
        } else {
            setDefaultQuizQuestion();
        }
    }

    private void initializeQuizAudioQuestion(Bundle args) {
        super.initializeBaseQuestion(quizAudioQuestion, args);
        quizAudioQuestion.setAudioUrl(args.getString(ARG_AUDIO_URL, ""));
        quizAudioQuestion.setOptions(args.getStringArrayList(ARG_OPTIONS));
        quizAudioQuestion.setCorrectOptionIndex(args.getInt(ARG_CORRECT_INDEX, 0));

        List<String> options = quizAudioQuestion.getOptions() != null ? quizAudioQuestion.getOptions() : Collections.emptyList();
        for (int i = 0; i < options.size(); i++) {
            answers.add(new QCAnswer(options.get(i), i == quizAudioQuestion.getCorrectOptionIndex(), answerBackgroundColor[i % 4]));
        }
    }

    private void setDefaultQuizQuestion() {
        super.setDefaultBaseQuestion(quizAudioQuestion);
        quizAudioQuestion.setAudioUrl("");
        quizAudioQuestion.setOptions(Collections.emptyList());
        quizAudioQuestion.setCorrectOptionIndex(0);

        for (int color : answerBackgroundColor) {
            QCAnswer _answer = new QCAnswer("", false, color);
            answers.add(_answer);
        }
        answers.get(0).setCorrect(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_quiz_audio, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(quizAudioQuestion);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);
        answerAdapter = new QCAnswerAdapter(requireContext(), answers, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        answersRecyclerView.setLayoutManager(gridLayoutManager);
        answersRecyclerView.addItemDecoration(new SpacingItemDecoration(2, 30));

        answersRecyclerView.setAdapter(answerAdapter);
    }

    @Override
    public void onAnswerClick(int position) {
        new QCAnswerCreateDialog.Builder(getContext())
                .setHeaderTitle("Create Answer")
                .setMultipleChoiceQuestion(false)
                .setAnswerMaxLength(20)
                .setListener(new QCAnswerCreateDialog.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, QCAnswer answer) {
                        answers.set(position, answer);
                        answerAdapter.notifyItemChanged(position);
                        List<String> options = answers.stream()
                                .map(QCAnswer::getText)
                                .collect(Collectors.toList());
                        quizAudioQuestion.setOptions(options);
                        if(listener != null) {
                            listener.onUpdateQuestion(quizAudioQuestion.getPosition(), quizAudioQuestion);
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onItemSelectedUpdateAll(List<QCAnswer> updatedAnswers) {
                        for(int i = 0; i < answers.size(); i++) {
                            QCAnswer sourceAnswer = answers.get(i);
                            QCAnswer targetAnswer = updatedAnswers.get(i);
                            sourceAnswer.setCorrect(targetAnswer.isCorrect());
                        }

                        List<String> options = answers.stream()
                                .map(QCAnswer::getText)
                                .collect(Collectors.toList());
                        int correctIndex = IntStream.range(0, answers.size())
                                .filter(i -> answers.get(i).isCorrect())
                                .findFirst()
                                .orElse(-1);
                        quizAudioQuestion.setOptions(options);
                        quizAudioQuestion.setCorrectOptionIndex(correctIndex);
                        if(listener != null) {
                            listener.onUpdateQuestion(quizAudioQuestion.getPosition(), quizAudioQuestion);
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
        return quizAudioQuestion;
    }

    private static class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;

        SpacingItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position < 0) return;

            int column = position % spanCount;
            outRect.left = column == 0 ? 0 : spacing / 2;
            outRect.right = column == spanCount - 1 ? 0 : spacing / 2;
            outRect.top = position < spanCount ? 0 : spacing;
        }
    }
}
