package com.cmloopy.quizzi.adapter.QuestionCreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCHelper;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class QCChoiceOptionAdapter extends RecyclerView.Adapter<QCChoiceOptionAdapter.AnswerViewHolder> {
    private List<ChoiceOption> answers;
    private OnAnswerClickListener listener;
    private Context context;
    private int layoutType = R.layout.ui_qc_item_answer_quiz_type;

    public interface OnAnswerClickListener {
        void onAnswerClick(int position);
        void onAnswerLongClick(int position);
    }

    public QCChoiceOptionAdapter(Context context, List<ChoiceOption> answers, OnAnswerClickListener listener) {
        this.context = context;
        this.answers = answers;
        this.listener = listener;
    }

    public QCChoiceOptionAdapter(Context context, List<ChoiceOption> answers, OnAnswerClickListener listener, int layoutType) {
        this.context = context;
        this.answers = answers;
        this.listener = listener;
        this.layoutType = layoutType;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutType, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        ChoiceOption answer = answers.get(position);
        holder.answerButton.setText(answer.getTextOrDefault());
        holder.buttonWrap.setBackgroundResource(answer.getBackground());
        QCHelper.applyLayerColorToIcon(context, answer.getBackground(), holder.checkIcon);

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

    public void addAnswer(ChoiceOption answer) {
        answers.add(answer);
        notifyItemInserted(answers.size() - 1);
    }

    public void removeAnswer(int position) {
        answers.remove(position);
        notifyItemRemoved(position);
    }


}