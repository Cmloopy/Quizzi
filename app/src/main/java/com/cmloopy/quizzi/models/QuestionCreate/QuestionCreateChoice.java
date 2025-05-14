package com.cmloopy.quizzi.models.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.Option;

import java.io.Serializable;
import java.util.List;

public class QuestionCreateChoice extends QuestionCreate implements Serializable {
    private List<ChoiceOption> choiceOptions;

    public QuestionCreateChoice() {
        super();
        this.choiceOptions =  Option.initializeDefaultOptions(Option.TYPE_CHOICE, 4);
    }

    public QuestionCreateChoice(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<ChoiceOption> choiceOptions) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.choiceOptions = choiceOptions;
    }

    public QuestionCreateChoice(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<ChoiceOption> choiceOptions) {
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