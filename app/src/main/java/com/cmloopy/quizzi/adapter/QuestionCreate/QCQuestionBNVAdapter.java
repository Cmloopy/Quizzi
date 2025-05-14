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
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QCQuestionBNVAdapter extends RecyclerView.Adapter<QCQuestionBNVAdapter.ViewHolder> {
    private List<QuestionCreate> questionCreates;
    private Context context;
    private OnQuestionClickListener listener;
    private int selectedPosition = 0;

    public interface OnQuestionClickListener {
        void onQuestionClick(QuestionCreate questionCreate, int position);
    }

    public void setListener(OnQuestionClickListener listener) {
        this.listener = listener;
    }

    public QCQuestionBNVAdapter(Context context, List<QuestionCreate> questionCreates, OnQuestionClickListener listener) {
        this.context = context;
        this.questionCreates = questionCreates;
        this.listener = listener;
    }

    public QCQuestionBNVAdapter(Context context, List<QuestionCreate> questionCreates, OnQuestionClickListener listener, int selectedPosition) {
        this.context = context;
        this.questionCreates = questionCreates;
        this.listener = listener;
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getItemCount() {
        return questionCreates.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_qc_item_bnv_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionCreate questionCreate = questionCreates.get(position);
        holder.bind(questionCreate, position, position == selectedPosition);
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

        public void bind(final QuestionCreate questionCreate, final int position, boolean isSelected) {
            if(questionCreate.getImage() != null && !questionCreate.getImage().isEmpty()) {
                Picasso.get()
                        .load(questionCreate.getImage())
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
                    listener.onQuestionClick(questionCreate, position);
                    setSelectedPosition(position); // Update selected position when clicked
                }
            });
        }
    }
}