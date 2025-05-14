package com.cmloopy.quizzi.models.question.choice;

import com.cmloopy.quizzi.models.question.Question;
import com.cmloopy.quizzi.models.question.QuestionType;

import java.util.List;

public class MultiChoiceQuestion extends Question {
    public List<ChoiceOption> choiceOptions;

    public MultiChoiceQuestion(List<ChoiceOption> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }

    public MultiChoiceQuestion(int id, int quizId, QuestionType questionType, String image, String audio, String content, int point, int timeLimit, String description, String createdAt, String updatedAt, List<ChoiceOption> choiceOptions) {
        super(id, quizId, questionType, image, audio, content, point, timeLimit, description, createdAt, updatedAt);
        this.choiceOptions = choiceOptions;
    }

    public List<ChoiceOption> getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(List<ChoiceOption> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }
}
