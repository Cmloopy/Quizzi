package com.cmloopy.quizzi.fragment.QuizCreate.after;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.after.QCQuestionBNVAdapter;
import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.after.QCQuestionDataGenerator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QCQuestionBNVFragment extends Fragment
        implements QCQuestionBNVAdapter.OnQuestionClickListener,
                    QCBaseResponseFragment.OnChangeListener {
    private RecyclerView questionRecyclerView;
    private QCQuestionBNVAdapter questionBottomAdapter;
    private HorizontalScrollView questionScrollView;
    private FloatingActionButton fabAdd;

    private OnQuestionBNVListener listener;
    private List<Question> questions;

    public QCQuestionBNVFragment() {
        questions = QCQuestionDataGenerator.generateQuestions(2);
    }

    public QCQuestionBNVFragment(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public void onUpdateQuestion(int position, Question question) {
        questions.set(position, question);
//        if(listener != null) {
//            listener.onClickListener(question);
//        }
        Log.d("QCQuestionBNVFragment", "Updating question at position: " + position);
        questionBottomAdapter.notifyItemChanged(position);
    }

    public void addQuestion(Question question) {
        questions.add(question);
        questionBottomAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDeleteQuestion(int position, Question question) {
        questions.remove(position);

        for (int i = position; i < questions.size(); i++) {
            questions.get(i).setPosition(i);
        }

        questionBottomAdapter.notifyDataSetChanged();

        if (listener != null) {
            listener.onDeleteQuestion(position, question, questions);
        }

        if (!questions.isEmpty()) {
            int newPosition = Math.min(position, questions.size() - 1);
            listener.onClickListener(questions.get(newPosition));
        }

        Log.d("QCQuestionBNVFragment", "Deleted question at position: " + position);
    }

    public interface OnQuestionBNVListener {
        void onClickListener(Question question);
        void onDeleteQuestion(int position, Question question, List<Question> questions);
    }

    public void setListener(OnQuestionBNVListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_qc_bnv, container, false);
        questionRecyclerView = view.findViewById(R.id.question_recycler_view);
        questionScrollView = view.findViewById(R.id.question_scroll_view);
        fabAdd = view.findViewById(R.id.fab_add);
        Context context = getContext();
        questionBottomAdapter = new QCQuestionBNVAdapter(context, questions, this);
        questionBottomAdapter.setListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionRecyclerView.setLayoutManager(linearLayoutManager);
        questionRecyclerView.setAdapter(questionBottomAdapter);
        questionRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 20;

                outRect.right = spacing;
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QCHelper.showQuestionTypeBottomSheet2(
                        getContext(),
                        getActivity().getSupportFragmentManager(),
                        (questionType) -> {
//                            QCHelper.navigateToQuestionCreation(
//                                    getActivity().getSupportFragmentManager(),
//                                    questionType
//                            );
                            String name = questionType.getName();
                            Question question = QCHelper.QuestionTypeMapper.createQuestionInstance2(questions, name);
                            question.setQuestionType(questionType);
                            int newPosition = questions.size();
                            question.setPosition(newPosition);
                            questions.add(question);
                            questionBottomAdapter.notifyItemInserted(newPosition);
                            listener.onClickListener(questions.get(newPosition));
                        }

                );
            }
        });

        if(!questions.isEmpty() && listener != null) {
            listener.onClickListener(questions.get(0));
        }
        return view;
    }
    @Override
    public void onQuestionClick(Question question, int position) {
        listener.onClickListener(question);
    }


    public List<Question> getQuestions() {
        if(questions == null) questions = new ArrayList<>();
        return questions;
    }
}
