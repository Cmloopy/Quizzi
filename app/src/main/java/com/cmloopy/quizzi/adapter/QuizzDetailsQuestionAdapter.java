package com.cmloopy.quizzi.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizzDetailsQuestion;

import java.util.List;
public class QuizzDetailsQuestionAdapter extends RecyclerView.Adapter<QuizzDetailsQuestionAdapter.QuestionViewHolder> {
    private Context context;
    private List<QuizzDetailsQuestion> questionList;

    public QuizzDetailsQuestionAdapter(Context context, List<QuizzDetailsQuestion> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quizz_details_full_page_item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuizzDetailsQuestion question = questionList.get(position);
        holder.questionTitle.setText(question.getQuestionNumber());
        holder.questionDescription.setText(question.getQuestionText());
        holder.questionImage.setImageResource(question.getQuestionImageResId());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle, questionDescription;
        ImageView questionImage;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.questionTitle);
            questionDescription = itemView.findViewById(R.id.questionDescription);
            questionImage = itemView.findViewById(R.id.questionImage);
        }
    }
}
