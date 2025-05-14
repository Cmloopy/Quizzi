package com.cmloopy.quizzi.adapter.playquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.question.choice.ChoiceOption;
import com.cmloopy.quizzi.models.question.choice.MultiChoiceQuestion;

import java.util.List;

public class MultiChoiceAdapter extends RecyclerView.Adapter<MultiChoiceAdapter.MultiChoiceViewHolder> {
    public List<ChoiceOption> answerList;
    private Context context;

    public MultiChoiceAdapter(Context context, List<ChoiceOption> answerList) {
        this.context = context;
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public MultiChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_multi_choice, parent, false);
        return new MultiChoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiChoiceViewHolder holder, int position) {
        ChoiceOption choiceOption = answerList.get(position);

        holder.txtAnswer.setText(choiceOption.text);
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(choiceOption.isSelected);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            choiceOption.isSelected = isChecked;
        });
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public static class MultiChoiceViewHolder extends RecyclerView.ViewHolder {
        TextView txtAnswer;
        CheckBox checkBox;

        public MultiChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAnswer = itemView.findViewById(R.id.txt_answer);
            checkBox = itemView.findViewById(R.id.txt_choice_answer);
        }
    }
}
