package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.adapter.QuizCreate.before.QCTypeAnswerAdapter;
import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;

import java.util.ArrayList;
import java.util.List;

public class QCTypeAnswerListFragment extends Fragment
        implements QCTypeAnswerAdapter.OnAnswerClickListener {

    private Button addAnotherAnswerButton;

    private RecyclerView answersRecyclerView;
    private QCTypeAnswerAdapter answerAdapter;
    private List<QCAnswer> answers;
    private OnAnotherAnswerAdded listener;

    public interface OnAnotherAnswerAdded {
        void addAnotherAnswer();
        void onDeleteAnswer(int position);
    }

    public void setOnAnotherAnswerAddedListener(OnAnotherAnswerAdded listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        View view = inflater.inflate(R.layout.fragment_question_type_text_list, container, false);

        answersRecyclerView = view.findViewById(R.id.answers_recycler_view);

        // Initialize answers list if not already initialized
        if (answers == null) {
            answers = new ArrayList<>();
        }

        // Setup RecyclerView
        setupRecyclerView(context);

        return view;
    }

    private void setupRecyclerView(Context context) {
        answerAdapter = new QCTypeAnswerAdapter(context, answers, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        answersRecyclerView.setLayoutManager(linearLayoutManager);

        // Add item decoration for spacing
        answersRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int spacing = 20;

                if(position > 0) {
                    outRect.top = spacing;
                }
            }
        });

        answersRecyclerView.setAdapter(answerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addAnotherAnswerButton = view.findViewById(R.id.btn_add_another_answer);


        // Setup add another answer button
        addAnotherAnswerButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.addAnotherAnswer();
            }
        });
    }

    public void addAnswer(QCAnswer answer) {
        // Ensure answers list is initialized
        if (answers == null) {
            answers = new ArrayList<>();
        }

        // Add the new answer
        answers.add(answer);

        // Update the adapter if it exists
        if (answerAdapter != null) {
            answerAdapter.notifyItemInserted(answers.size() - 1);
        }
    }

    public void setAnswers(List<QCAnswer> newAnswers) {
        // Ensure answers list is initialized
        if (answers == null) {
            answers = new ArrayList<>();
        }

        // Clear existing answers and add new ones
        answers.clear();
        answers.addAll(newAnswers);

        // Update the adapter if it exists
//        if (answerAdapter != null) {
//            answerAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onAnswerClick(int position) {
        // Handle answer click if needed
    }

    @Override
    public void onAnswerLongClick(int position) {
        // Handle long click if needed
    }

    @Override
    public void onDeleteAnswer(int position) {
        listener.onDeleteAnswer(position);
    }

    public QCTypeAnswerAdapter getAnswerAdapter() {
        return answerAdapter;
    }
}