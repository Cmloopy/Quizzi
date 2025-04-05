package com.cmloopy.quizzi.models.QuizCreate.after.Option;

import java.io.Serializable;

public class TypeTextOption extends Option implements Serializable {

    public TypeTextOption() {
        super();
    }

    public TypeTextOption(int position, String id, String text, String imageUrl, String audioUrl) {
        super(position, id, text, imageUrl, audioUrl);
    }
}