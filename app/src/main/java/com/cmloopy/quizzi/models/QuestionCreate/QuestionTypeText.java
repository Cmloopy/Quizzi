package com.cmloopy.quizzi.models.QuestionCreate;

import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionTypeText extends Question implements Serializable {
    private List<TypeTextOption> acceptedAnswers;
    private boolean caseSensitive;

    public QuestionTypeText() {
        super();
        this.acceptedAnswers = new ArrayList<>();
        this.caseSensitive = false;
    }

    public QuestionTypeText(int position, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<TypeTextOption> acceptedAnswers, boolean caseSensitive) {
        super(position, content, imageUrl, audioUrl, point, timeLimit, description);
        this.acceptedAnswers = acceptedAnswers;
        this.caseSensitive = caseSensitive;
    }

    public QuestionTypeText(int position, Long id, String content, String imageUrl, String audioUrl, int point, int timeLimit, String description, List<TypeTextOption> acceptedAnswers, boolean caseSensitive) {
        super(position, id, content, imageUrl, audioUrl, point, timeLimit, description);
        this.acceptedAnswers = acceptedAnswers;
        this.caseSensitive = caseSensitive;
    }

    public List<TypeTextOption> getAcceptedAnswers() {
        return acceptedAnswers;
    }

    public void setAcceptedAnswers(List<TypeTextOption> acceptedAnswers) {
        this.acceptedAnswers = acceptedAnswers;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
}
