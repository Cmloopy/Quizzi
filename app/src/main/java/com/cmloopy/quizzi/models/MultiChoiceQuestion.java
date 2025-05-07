package com.cmloopy.quizzi.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MultiChoiceQuestion extends Question<List<String>>{
    private List<String> choiceOptions;

    public MultiChoiceQuestion( String question, int points, int time_limit, List<String> correctAnswer, List<String> choiceOptions) {
        super( question, points, time_limit, correctAnswer);
        super.setType("multi_choice");
        this.choiceOptions = choiceOptions;
    }
    public static List<MultiChoiceQuestion> createSampleData() {
        List<MultiChoiceQuestion> allQuestions =  Arrays.asList(
                new MultiChoiceQuestion( "Which of the following are programming languages?", 10, 30, Arrays.asList("Java", "Python"), Arrays.asList("Java", "Python", "HTML", "CSS")),
                new MultiChoiceQuestion("Which planets are gas giants?", 10, 30, Arrays.asList("Jupiter", "Saturn"), Arrays.asList("Earth", "Mars", "Jupiter", "Saturn")),
                new MultiChoiceQuestion( "Which of these are primary colors?", 10, 30, Arrays.asList("Red", "Blue", "Yellow"), Arrays.asList("Red", "Green", "Blue", "Yellow")),
                new MultiChoiceQuestion( "Which of the following are mammals?", 10, 30, Arrays.asList("Dolphin", "Elephant"), Arrays.asList("Dolphin", "Elephant", "Crocodile", "Eagle"))
        );
        Collections.shuffle(allQuestions);
        return new ArrayList<>(allQuestions.subList(0, 4));
    }

    public List<String> getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(List<String> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }
}
