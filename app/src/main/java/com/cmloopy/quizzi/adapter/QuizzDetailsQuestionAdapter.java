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
import com.cmloopy.quizzi.models.QuestionCreate.QuestionType;
import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCQuestionDataGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizzDetailsQuestionAdapter extends RecyclerView.Adapter<QuizzDetailsQuestionAdapter.QuestionViewHolder> {
    private Context context;
    private List<Question> questionList;
    List<QuestionType> questionTypeDefaultList = QCQuestionDataGenerator.initializeQuestionTypes();
    Map<String, String> mapQuestionType = new HashMap<>();


    public QuizzDetailsQuestionAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
        mapQuestionType.put("SINGLE_CHOICE", "Quiz");
        mapQuestionType.put("MULTI_CHOICE", "Checkbox");
        mapQuestionType.put("TRUE_FALSE", "True or False");
        mapQuestionType.put("TEXT", "Type Answer");
        mapQuestionType.put("PUZZLE", "Puzzle");
        mapQuestionType.put("SLIDER", "Slider");
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quizz_details_full_page_item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.questionTitle.setText(question.content);

        holder.questionCategory.setText(mapQuestionType.get(question.questionType.getName()));
        int targetIconId = -1;
        for(QuestionType questionType: questionTypeDefaultList) {
            if(question.questionType.getName().equals(questionType.getName())) {
                targetIconId = questionType.getIconResource();
            }
        }
        if(targetIconId != - 1) {
            holder.questionCategoryIcon.setImageResource(targetIconId);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle, questionCategory;
        ImageView questionCategoryIcon;
        ImageView questionImage;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.questionTitle);
            questionCategory = itemView.findViewById(R.id.questionCategory);
            questionImage = itemView.findViewById(R.id.questionImage);
            questionCategoryIcon = itemView.findViewById(R.id.questionCategoryIcon);
        }
    }
}
