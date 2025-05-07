package com.cmloopy.quizzi.adapter.QuizCreate.before;
import com.cmloopy.quizzi.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.models.QuizCreate.before.QCQuestionType;

import java.util.List;

public class QCQuestionTypeAdapter extends RecyclerView.Adapter<QCQuestionTypeAdapter.QuestionTypeViewHolder> {
    private final List<QCQuestionType> questionTypes;
    private OnQuestionTypeClickListener listener;

    public interface OnQuestionTypeClickListener {
        void onQuestionTypeClick(QCQuestionType questionType);
    }

    public QCQuestionTypeAdapter(List<QCQuestionType> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public void setOnQuestionTypeClickListener(OnQuestionTypeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_qc_item_question_type, parent, false);
        return new QuestionTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionTypeViewHolder holder, int position) {
        QCQuestionType questionType = questionTypes.get(position);
        holder.bind(questionType);
    }

    @Override
    public int getItemCount() {
        return questionTypes.size();
    }

    class QuestionTypeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImageView;
        private final TextView nameTextView;

        public QuestionTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iv_question_type_icon);
            nameTextView = itemView.findViewById(R.id.tv_question_type_name);
        }

        public void bind(final QCQuestionType questionType) {
            iconImageView.setImageResource(questionType.getIconResourceId());
            nameTextView.setText(questionType.getName());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuestionTypeClick(questionType);
                }
            });
        }
    }
}