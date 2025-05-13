package com.cmloopy.quizzi.models.QuestionCreate.Option;

import java.io.Serializable;

public class TypeTextOption extends Option implements Serializable {

    public TypeTextOption() {
        super();
    }

    public TypeTextOption(int position, String text, String imageUrl, String audioUrl) {
        super(position, text, imageUrl, audioUrl);
    }

    public TypeTextOption(int position, Long id, String text, String imageUrl, String audioUrl) {
        super(position, id, text, imageUrl, audioUrl);
    }
}