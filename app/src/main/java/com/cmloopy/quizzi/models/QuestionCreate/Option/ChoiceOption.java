package com.cmloopy.quizzi.models.QuestionCreate.Option;

import java.io.Serializable;

public class ChoiceOption extends Option implements Serializable {
    private boolean isCorrect;

    public ChoiceOption() {
        super();
        this.isCorrect = false;
    }

    public ChoiceOption(int position, Long id, String text, String imageUrl, String audioUrl, boolean isCorrect) {
        super(position, id, text, imageUrl, audioUrl);
        this.isCorrect = isCorrect;
    }

    public ChoiceOption(int position, String text, String imageUrl, String audioUrl, boolean isCorrect) {
        super(position, text, imageUrl, audioUrl);
        this.isCorrect = isCorrect;
    }


    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}
