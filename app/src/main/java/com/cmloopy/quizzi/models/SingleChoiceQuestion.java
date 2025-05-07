package com.cmloopy.quizzi.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SingleChoiceQuestion extends Question<String> {
    private List<String> choiceOptions;

    public SingleChoiceQuestion( String question, int points, int time_limit, String correctAnswer, List<String> choiceOptions) {
        super( question, points, time_limit, correctAnswer);
        super.setType("single_choice");
        this.choiceOptions = choiceOptions;
    }

    public static List<SingleChoiceQuestion> createSampleData() {
        List<SingleChoiceQuestion> allQuestions = Arrays.asList(
                new SingleChoiceQuestion("What is the capital of France?", 10, 30, "Paris", Arrays.asList("Paris", "London", "Berlin", "Madrid")),
                new SingleChoiceQuestion("Which planet is known as the Red Planet?", 10, 30, "Mars", Arrays.asList("Earth", "Venus", "Mars", "Jupiter")),
                new SingleChoiceQuestion("What is the largest ocean on Earth?", 10, 30, "Pacific", Arrays.asList("Atlantic", "Indian", "Arctic", "Pacific")),
                new SingleChoiceQuestion("Who wrote 'Hamlet'?", 10, 30, "Shakespeare", Arrays.asList("Shakespeare", "Hemingway", "Tolkien", "Austen")),
                new SingleChoiceQuestion("What is the chemical symbol for water?", 10, 30, "H2O", Arrays.asList("H2O", "O2", "CO2", "NaCl")),
                new SingleChoiceQuestion("Which is the fastest land animal?", 10, 30, "Cheetah", Arrays.asList("Lion", "Tiger", "Cheetah", "Leopard")),
                new SingleChoiceQuestion("How many continents are there?", 10, 30, "7", Arrays.asList("5", "6", "7", "8"))
        );

        // Trộn danh sách ngẫu nhiên
        Collections.shuffle(allQuestions);

        // Chọn số lượng câu hỏi ngẫu nhiên (ví dụ: 4 câu)
        int numberOfQuestions = 4;
        List<SingleChoiceQuestion> randomQuestions = new ArrayList<>(allQuestions.subList(0, numberOfQuestions));

        return randomQuestions;
    }

    public List<String> getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(List<String> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }
}
