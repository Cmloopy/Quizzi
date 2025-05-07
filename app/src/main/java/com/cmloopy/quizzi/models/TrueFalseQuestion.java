package com.cmloopy.quizzi.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TrueFalseQuestion extends Question<Boolean>{
    public TrueFalseQuestion( String question, int points, int time_limit, Boolean correctAnswer) {
        super( question, points, time_limit, correctAnswer);
        super.setType("true_false");
    }
    public static List<TrueFalseQuestion> createSampleData() {
        List<TrueFalseQuestion> allQuestions = Arrays.asList(
                new TrueFalseQuestion("The Earth is flat.", 5, 15, false),
                new TrueFalseQuestion("Water boils at 100 degrees Celsius.", 5, 15, true),
                new TrueFalseQuestion("The Moon is a planet.", 5, 15, false),
                new TrueFalseQuestion("Lightning never strikes the same place twice.", 5, 15, false)
        );
        Collections.shuffle(allQuestions);
        return new ArrayList<>(allQuestions.subList(0, 4));
    }
}
