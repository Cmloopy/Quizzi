package com.cmloopy.quizzi.adapter.QuizCreate.before;

import com.cmloopy.quizzi.R;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.models.QuizCreate.before.QCAnswer;
import com.cmloopy.quizzi.utils.QuizCreate.dialogs.QCHelper;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QCCheckboxAnswerAdapter extends RecyclerView.Adapter<QCCheckboxAnswerAdapter.AnswerViewHolder> {
    private List<QCAnswer> answers;
    private OnAnswerClickListener listener;
    private Context context;

    public interface OnAnswerClickListener {
        void onAnswerClick(int position);
        void onAnswerLongClick(int position);
    }

    public QCCheckboxAnswerAdapter(Context context, List<QCAnswer> answers, OnAnswerClickListener listener) {
        this.context = context;
        this.answers = answers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_qc_item_answer_checkbox_type, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        QCAnswer answer = answers.get(position);
        holder.answerButton.setText(answer.getTextOrDefault());
        holder.buttonWrap.setBackgroundResource(answer.getBackground());
        QCHelper.applyLayerColorToIcon(context, answer.getBackground(), holder.checkIcon);
        holder.answerButton.setMovementMethod(new ScrollingMovementMethod());
        holder.answerButton.setFocusable(true);
        holder.answerButton.setVerticalScrollBarEnabled(true);
        holder.answerButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        holder.checkIconContainer.setVisibility(answer.isCorrect() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder {
        FrameLayout buttonWrap;
        MaterialButton answerButton;
        CardView checkIconContainer;
        ImageView checkIcon;

        AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonWrap = itemView.findViewById(R.id.card_container);
            answerButton = itemView.findViewById(R.id.text_answer);
            checkIconContainer = itemView.findViewById(R.id.check_icon_container);
            checkIcon = itemView.findViewById(R.id.drag_icon_btn);

            answerButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAnswerClick(getAdapterPosition());
                }
            });

            answerButton.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onAnswerLongClick(getAdapterPosition());
                }
                return true;
            });
        }
    }

    public void addAnswer(QCAnswer answer) {
        answers.add(answer);
        notifyItemInserted(answers.size() - 1);
    }

    public void removeAnswer(int position) {
        answers.remove(position);
        notifyItemRemoved(position);
    }


}