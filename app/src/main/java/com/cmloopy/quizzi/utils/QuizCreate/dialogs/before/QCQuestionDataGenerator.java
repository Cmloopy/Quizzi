package com.cmloopy.quizzi.utils.QuizCreate.dialogs.before;

import com.cmloopy.quizzi.models.QuizCreate.before.CheckboxQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.PuzzleQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizAudioQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SayWordQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SliderQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TrueFalseQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TypeQuestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Helper class to generate fake question data for testing
 */
public class QCQuestionDataGenerator {
    private static final Random random = new Random();

    /**
     * Generates a shuffled list of questions with at least one of each type
     * @param totalQuestions total number of questions to generate
     * @return List of Question objects
     */
    public static List<Question> generateQuestions(int totalQuestions) {
        // Ensure we have at least one of each type
        if (totalQuestions < 8) {
            totalQuestions = 8; // We have 8 question types
        }

        List<Question> questions = new ArrayList<>();

        // Add one of each type first to ensure all types are represented
        questions.add(generateSliderQuestion(questions.size()));
//        questions.add(generateCheckboxQuestion(questions.size()));
//        questions.add(generatePuzzleQuestion(questions.size()));
//        questions.add(generateQuizQuestion(questions.size()));
//        questions.add(generateTypeQuestion(questions.size()));
//        questions.add(generateQuizAudioQuestion(questions.size()));
//        questions.add(generateTrueFalseQuestion(questions.size()));
//        questions.add(generateSayWordQuestion(questions.size()));

        // Fill the rest with random questions
//        for (int i = 8; i < totalQuestions; i++) {
//            int type = random.nextInt(8) + 1; // 1-8 for question types
//
//            switch (type) {
//                case Question.TYPE_SLIDER:
//                    questions.add(generateSliderQuestion());
//                    break;
//                case Question.TYPE_QUIZ:
//                    questions.add(generateQuizQuestion());
//                    break;
//                case Question.TYPE_CHECKBOX:
//                    questions.add(generateCheckboxQuestion());
//                    break;
//                case Question.TYPE_PUZZLE:
//                    questions.add(generatePuzzleQuestion());
//                    break;
//                case Question.TYPE_TEXT:
//                    questions.add(generateTypeQuestion());
//                    break;
//                case Question.TYPE_QUIZ_AUDIO:
//                    questions.add(generateQuizAudioQuestion());
//                    break;
//                case Question.TYPE_TRUE_FALSE:
//                    questions.add(generateTrueFalseQuestion());
//                    break;
//                case Question.TYPE_SAY_WORD:
//                    questions.add(generateSayWordQuestion());
//                    break;
//            }
//        }

        // Shuffle the list
//        Collections.shuffle(questions);

        return questions;
    }

    // Generate 5 of each question type
    public static List<Question> generateFiveOfEachType() {
        List<Question> questions = new ArrayList<>();

        // Generate 5 of each type
        for (int i = 0; i < 5; i++) {
//            questions.add(generateSliderQuestion());
//            questions.add(generateQuizQuestion());
//            questions.add(generateCheckboxQuestion());
//            questions.add(generatePuzzleQuestion());
//            questions.add(generateTypeQuestion());
//            questions.add(generateQuizAudioQuestion());
//            questions.add(generateTrueFalseQuestion());
//            questions.add(generateSayWordQuestion());
        }

        // Shuffle the list
        Collections.shuffle(questions);

        return questions;
    }

    // Helper method to generate a random ID
    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    // Generate Slider Questions
    private static SliderQuestion generateSliderQuestion(int position) {
        String[] titles = {
                "Adjust the slider to match the temperature shown in the thermometer",
                "Move the slider to indicate the correct angle",
                "Set the slider to match the water level in the container",
                "Adjust the slider to the correct position on the timeline",
                "Set the volume level shown in the image"
        };

        String[] imageUrls = {
                "https://example.com/images/thermometer1.jpg",
                "https://example.com/images/angle_measure.jpg",
                "https://example.com/images/water_container.jpg",
                "https://example.com/images/timeline.jpg",
                "https://example.com/images/volume_meter.jpg"
        };

        String[] lambdas = {
                "Default",
                "Small",
                "Medium",
                "Large",
        };

        int titleIndex = random.nextInt(titles.length);
        int minValue = random.nextInt(50);
        int maxValue = minValue + 50 + random.nextInt(100);
        int correctValue = minValue + random.nextInt(maxValue - minValue + 1);
        int lambdaIndex = random.nextInt(lambdas.length);

        return new SliderQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                minValue,
                maxValue,
                correctValue,
                lambdas[lambdaIndex]
        );
    }

    // Generate Quiz Questions
    private static QuizQuestion generateQuizQuestion(int position) {
        String[] titles = {
                "Which of these animals is shown in the image?",
                "What is the capital city displayed in the photo?",
                "Identify the correct chemical element symbol",
                "Which mathematical formula is represented in the image?",
                "Select the correct historical figure from the portrait"
        };

        String[] imageUrls = {
                "https://example.com/images/animal_quiz.jpg",
                "https://example.com/images/capital_city.jpg",
                "https://example.com/images/periodic_table.jpg",
                "https://example.com/images/math_formula.jpg",
                "https://example.com/images/historical_portrait.jpg"
        };

        String[][] optionSets = {
                {"Lion", "Tiger", "Leopard", "Cheetah"},
                {"Paris", "London", "Berlin", "Madrid"},
                {"Na", "K", "Ca", "Mg"},
                {"Pythagorean Theorem", "E=mc²", "F=ma", "PV=nRT"},
                {"Albert Einstein", "Marie Curie", "Isaac Newton", "Charles Darwin"}
        };

        int titleIndex = random.nextInt(titles.length);
        int correctOption = random.nextInt(4);

        return new QuizQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                Arrays.asList(optionSets[titleIndex]),
                correctOption
        );
    }

    // Generate Checkbox Questions
    private static CheckboxQuestion generateCheckboxQuestion(int position) {
        String[] titles = {
                "Select all the planets visible in the image",
                "Check all the fruits shown in the basket",
                "Identify all the programming languages in the code snippet",
                "Select all the musical instruments shown",
                "Check all the correct mathematical equations"
        };

        String[] imageUrls = {
                "https://example.com/images/solar_system.jpg",
                "https://example.com/images/fruit_basket.jpg",
                "https://example.com/images/code_snippet.jpg",
                "https://example.com/images/musical_instruments.jpg",
                "https://example.com/images/math_equations.jpg"
        };

        String[][] optionSets = {
                {"Mercury", "Venus", "Earth", "Mars", "Jupiter"},
                {"Apple", "Orange", "Banana", "Grapes", "Strawberry"},
                {"Java", "Python", "JavaScript", "C++", "Ruby"},
                {"Guitar", "Piano", "Drums", "Violin", "Flute"},
                {"2+2=4", "3×3=6", "5-2=3", "10÷2=5", "7+3=11"}
        };

        Boolean[][] answerSets = {
                {true, true, false, true, false},
                {true, false, true, true, false},
                {false, true, true, false, true},
                {true, false, true, true, false},
                {true, false, true, true, false}
        };

        int titleIndex = random.nextInt(titles.length);
        List<Boolean> correctAnswers = new ArrayList<>();
        for (Boolean answer : answerSets[titleIndex]) {
            correctAnswers.add(answer);
        }

        return new CheckboxQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                Arrays.asList(optionSets[titleIndex]),
                correctAnswers
        );
    }

    // Generate Puzzle Questions
    private static PuzzleQuestion generatePuzzleQuestion(int position) {
        String[] titles = {
                "Arrange the pieces to complete the historical timeline",
                "Solve the jigsaw puzzle of the landmark",
                "Put the chemical reaction steps in the correct order",
                "Arrange the code blocks to create a working algorithm",
                "Order the events of the story correctly"
        };

        String[] imageUrls = {
                "https://example.com/images/timeline_puzzle.jpg",
                "https://example.com/images/landmark_jigsaw.jpg",
                "https://example.com/images/chemical_reaction.jpg",
                "https://example.com/images/algorithm_blocks.jpg",
                "https://example.com/images/story_sequence.jpg"
        };

        String[] puzzleData = {
                "World War I,Great Depression,World War II,Cold War,Digital Age",
                "9,4,1,6,3,7,2,5,8",
                "Reactants,Activation,Transition State,Products,Energy Release",
                "Initialize,Input,Process,Condition,Loop,Output",
                "Introduction,Rising Action,Climax,Falling Action,Resolution"
        };

        String[] solutions = {
                "World War I,Great Depression,World War II,Cold War,Digital Age",
                "1,2,3,4,5,6,7,8,9",
                "Reactants,Activation,Transition State,Energy Release,Products",
                "Initialize,Input,Process,Condition,Loop,Output",
                "Introduction,Rising Action,Climax,Falling Action,Resolution"
        };

        int titleIndex = random.nextInt(titles.length);

        return new PuzzleQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                Arrays.asList(puzzleData),
                solutions[titleIndex]
        );
    }

    // Generate Text Questions
    private static TypeQuestion generateTypeQuestion(int position) {
        String[] titles = {
                "What is the name of the animal shown in the image?",
                "Enter the chemical formula displayed in the diagram",
                "What is the capital city of the country shown on the map?",
                "Type the solution to the mathematical equation",
                "Enter the name of the historical figure in the portrait"
        };

        String[] imageUrls = {
                "https://example.com/images/rare_animal.jpg",
                "https://example.com/images/chemical_formula.jpg",
                "https://example.com/images/country_map.jpg",
                "https://example.com/images/math_problem.jpg",
                "https://example.com/images/historical_figure.jpg"
        };

        String[] answers = {
                "platypus",
                "H2O",
                "Wellington",
                "42",
                "Marie Curie"
        };

        int titleIndex = random.nextInt(titles.length);

        return new TypeQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                Arrays.asList(answers)
        );
    }

    // Generate Quiz Audio Questions
    private static QuizAudioQuestion generateQuizAudioQuestion(int position) {
        String[] titles = {
                "Listen to the bird call and identify the species",
                "Identify the musical instrument being played",
                "Listen to the language sample and select the correct language",
                "Identify the song from the audio clip",
                "Select the correct composer of this classical piece"
        };

        String[] imageUrls = {
                "https://example.com/images/bird_silhouettes.jpg",
                "https://example.com/images/instruments_collection.jpg",
                "https://example.com/images/world_languages.jpg",
                "https://example.com/images/music_notes.jpg",
                "https://example.com/images/composers.jpg"
        };

        String[] audioUrls = {
                "https://example.com/audio/bird_call.mp3",
                "https://example.com/audio/instrument.mp3",
                "https://example.com/audio/language_sample.mp3",
                "https://example.com/audio/song_clip.mp3",
                "https://example.com/audio/classical_piece.mp3"
        };

        String[][] optionSets = {
                {"Robin", "Blue Jay", "Cardinal", "Sparrow"},
                {"Violin", "Piano", "Flute", "Trumpet"},
                {"French", "Spanish", "Italian", "Portuguese"},
                {"Bohemian Rhapsody", "Stairway to Heaven", "Imagine", "Hey Jude"},
                {"Mozart", "Beethoven", "Bach", "Chopin"}
        };

        int titleIndex = random.nextInt(titles.length);
        int correctOption = random.nextInt(4);

        return new QuizAudioQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                audioUrls[titleIndex],
                Arrays.asList(optionSets[titleIndex]),
                correctOption
        );
    }

    // Generate True/False Questions
    private static TrueFalseQuestion generateTrueFalseQuestion(int position) {
        String[] titles = {
                "Is the statement about this animal correct?",
                "Is this mathematical equation true?",
                "Is the historical fact stated about this image accurate?",
                "Does this geographic feature exist in the country shown?",
                "Is this chemical reaction possible as depicted?"
        };

        String[] imageUrls = {
                "https://example.com/images/animal_fact.jpg",
                "https://example.com/images/math_statement.jpg",
                "https://example.com/images/historical_event.jpg",
                "https://example.com/images/geography.jpg",
                "https://example.com/images/chemistry_diagram.jpg"
        };

        String[] statements = {
                "The blue whale is the largest animal to have ever existed on Earth.",
                "The sum of angles in a triangle is always 180 degrees.",
                "The Declaration of Independence was signed in 1776.",
                "The Amazon River is the longest river in the world.",
                "Water is composed of hydrogen and oxygen atoms."
        };

        boolean[] answers = {
                true,
                true,
                true,
                false,
                true
        };

        int titleIndex = random.nextInt(titles.length);

        return new TrueFalseQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                answers[titleIndex]
        );
    }

    // Generate Say Word Questions
    private static SayWordQuestion generateSayWordQuestion(int position) {
        String[] titles = {
                "Say the name of this animal shown in the image",
                "Pronounce the name of this country",
                "Say this scientific term correctly",
                "Pronounce the name of this historical figure",
                "Say the foreign language word shown in the image"
        };

        String[] imageUrls = {
                "https://example.com/images/exotic_animal.jpg",
                "https://example.com/images/country_flag.jpg",
                "https://example.com/images/scientific_diagram.jpg",
                "https://example.com/images/historical_portrait.jpg",
                "https://example.com/images/foreign_word.jpg"
        };

        String[] words = {
                "hippopotamus",
                "Azerbaijan",
                "photosynthesis",
                "Tchaikovsky",
                "bonjour"
        };

        String[] pronunciations = {
                "hip-uh-pot-uh-muhs",
                "az-uhr-bye-jahn",
                "foh-toh-sin-thuh-sis",
                "chy-kof-skee",
                "bohn-zhoor"
        };

        int titleIndex = random.nextInt(titles.length);

        return new SayWordQuestion(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                10,    // default time value
                200,   // default point value
                words[titleIndex],
                pronunciations[titleIndex]
        );
    }
}
