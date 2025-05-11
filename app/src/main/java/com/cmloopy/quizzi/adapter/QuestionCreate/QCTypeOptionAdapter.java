package com.cmloopy.quizzi.adapter.QuestionCreate;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;

import java.util.List;

public class QCTypeOptionAdapter extends RecyclerView.Adapter<QCTypeOptionAdapter.AnswerViewHolder> {
    private List<TypeTextOption> answers;
    private OnAnswerClickListener listener;
    private Context context;

    public interface OnAnswerClickListener {
//        void onAnswerClick(int position);
//        void onAnswerLongClick(int position);
        void onDeleteAnswer(int position);
    }

    public QCTypeOptionAdapter(Context context, List<TypeTextOption> answers, OnAnswerClickListener listener) {
        this.context = context;
        this.answers = answers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_qc_item_answer_type_text_type, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        TypeTextOption answer = answers.get(position);
        holder.answerText.setText(answer.getText());
        holder.cardContainer.setBackgroundResource(answer.getBackground());
        holder.answerText.setMovementMethod(new ScrollingMovementMethod());
        holder.trashButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteAnswer(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    class AnswerViewHolder extends RecyclerView.ViewHolder {
        FrameLayout cardContainer;
        TextView answerText;
        ImageButton trashButtonImage;

        AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.card_container);
            answerText = itemView.findViewById(R.id.answer_text);
            trashButtonImage = itemView.findViewById(R.id.trash_btn_image);

            // Enable nested scrolling for the TextView
            answerText.setMovementMethod(new ScrollingMovementMethod());
            answerText.setVerticalScrollBarEnabled(true);

            // Make the TextView focusable to enable scrolling
            answerText.setFocusable(true);
            answerText.setClickable(true);

            // Critical: Set up touch listener to handle nested scrolling
            answerText.setOnTouchListener((v, event) -> {
                // Allow TextView to handle its own scrolling
                v.getParent().requestDisallowInterceptTouchEvent(true);

                // Let the TextView handle the event
                return false;
            });
        }
    }

    public void addAnswer(TypeTextOption answer) {
        answers.add(answer);
        notifyItemInserted(answers.size() - 1);
    }

    public void removeAnswer(int position) {
        answers.remove(position);
        notifyItemRemoved(position);
    }


}