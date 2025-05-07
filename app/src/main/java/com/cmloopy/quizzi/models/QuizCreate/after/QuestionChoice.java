package com.cmloopy.quizzi.models.QuizCreate.after;

import com.cmloopy.quizzi.models.QuizCreate.after.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuizCreate.after.Option.Option;

import java.io.Serializable;
import java.util.List;

public class QuestionChoice extends Question implements Serializable {
    private List<ChoiceOption> choiceOptions;

    public QuestionChoice() {
        super();
        this.choiceOptions =  Option.initializeDefaultOptions(Option.TYPE_CHOICE, 4);
    }

    public QuestionChoice(int position, String id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<ChoiceOption> choiceOptions) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.choiceOptions = choiceOptions;
    }

    public List<ChoiceOption> getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(List<ChoiceOption> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }
}