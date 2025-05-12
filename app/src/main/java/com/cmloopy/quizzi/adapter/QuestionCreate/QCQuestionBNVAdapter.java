package com.cmloopy.quizzi.adapter.QuestionCreate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QCQuestionBNVAdapter extends RecyclerView.Adapter<QCQuestionBNVAdapter.ViewHolder> {
    private List<Question> questions;
    private Context context;
    private OnQuestionClickListener listener;
    private int selectedPosition = 0;

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

    public QCQuestionBNVAdapter(Context context, List<Question> questions, OnQuestionClickListener listener, int selectedPosition) {
        this.context = context;
        this.questions = questions;
        this.listener = listener;
        this.selectedPosition = selectedPosition;
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
        holder.bind(question, position, position == selectedPosition);
    }

    // Method to update selected position
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Notify adapter about items that changed to update their appearance
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);
    }

    // ViewHolder for question items
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView questionImage;
        private TextView questionNumber;
        private MaterialCardView cardContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            questionImage = itemView.findViewById(R.id.question_image);
            questionNumber = itemView.findViewById(R.id.question_number);
            cardContainer = itemView.findViewById(R.id.card_container);

        }

        public void bind(final Question question, final int position, boolean isSelected) {
            if(question.getImage() != null && !question.getImage().isEmpty()) {
                Picasso.get()
                        .load(question.getImage())
                        .resize(1080, 720)
                        .centerCrop()
                        .into(questionImage);
            } else {
                questionImage.setImageResource(R.drawable.back_to_school);
            }

            questionNumber.setText(String.valueOf(position + 1));

            if (isSelected) {

                cardContainer.setBackgroundResource(R.drawable.ui_qc_bg_item_question_bnv_click);
                cardContainer.setStrokeColor(ContextCompat.getColor(context, R.color.on_question_bnv_click));
            } else {
                cardContainer.setBackgroundResource(R.drawable.ui_qc_bg_item_question_bnv_unclick);
                cardContainer.setStrokeColor(ContextCompat.getColor(context, R.color.on_question_bnv_unclick));
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuestionClick(question, position);
                    setSelectedPosition(position); // Update selected position when clicked
                }
            });
        }
    }
}