package com.cmloopy.quizzi.adapter.QuizCreate.before;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QCQuestionBNVAdapter extends RecyclerView.Adapter<QCQuestionBNVAdapter.ViewHolder> {
    private List<Question> questions;
    private Context context;
    private OnQuestionClickListener listener;

    // Interface for click handling
    public interface OnQuestionClickListener {
        void onQuestionClick(Question question, int position);
    }

    public void setListener(OnQuestionClickListener listener) {
        this.listener = listener;
    }

    public QCQuestionBNVAdapter(Context context, List<Question> questions, OnQuestionClickListener listener) {
        this.context = context;
        this.questions = questions;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_qc_item_bnv_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onQuestionClick(questions.get(position), position);
            }
        });
    }

    // Single ViewHolder for all question types (showing just the image)
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView questionImage;
        private TextView questionNumber;
        private CardView cardContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            questionImage = itemView.findViewById(R.id.question_image);
            questionNumber = itemView.findViewById(R.id.question_number);
            cardContainer = itemView.findViewById(R.id.card_container);
        }

        public void bind(final Question question, final int position) {
//            Glide.with(context)
//                    .load(question.getImageUrl())
//                    .placeholder(R.drawable.placeholder_question)
//                    .error(R.drawable.error_question)
//                    .into(questionImage);
            questionImage.setImageResource(R.drawable.avt);
            questionNumber.setText(String.valueOf(position + 1));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuestionClick(question, position);
                }
            });
        }
    }
}
