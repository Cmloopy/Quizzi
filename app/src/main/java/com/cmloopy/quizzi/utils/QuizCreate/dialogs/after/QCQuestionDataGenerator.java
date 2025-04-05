package com.cmloopy.quizzi.utils.QuizCreate.dialogs.after;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.after.*;
import com.cmloopy.quizzi.models.QuizCreate.after.Option.*;

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

    // Question types mapping
    private static final List<QuestionType> questionTypes = initializeQuestionTypes();

    /**
     * Initialize all 8 question types
     * @return List of QuestionType objects
     */
    public static List<QuestionType> initializeQuestionTypes() {
        List<QuestionType> types = new ArrayList<>();
        types.add(new QuestionType(UUID.randomUUID().toString(), "Slider", R.drawable.ic_70_silder));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Quiz", R.drawable.ic_70_quiz));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Checkbox", R.drawable.ic_70_checkbox));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Puzzle", R.drawable.ic_70_puzzle));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Text", R.drawable.ic_70_type_answers));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Quiz Audio", R.drawable.ic_70_quiz_and_audio));
        types.add(new QuestionType(UUID.randomUUID().toString(), "True/False", R.drawable.ic_70_true_or_false));
        types.add(new QuestionType(UUID.randomUUID().toString(), "Say Word", R.drawable.ic_70_say_the_word));
        return types;
    }

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
        questions.add(generateQuizQuestion(0));
        questions.add(generateCheckboxQuestion(1));
        questions.add(generatePuzzleQuestion(2));
        questions.add(generateTextQuestion(3));
        questions.add(generateQuizAudioQuestion(4));
        questions.add(generateSliderQuestion(5));
        questions.add(generateTrueFalseQuestion(6));
        questions.add(generateSayWordQuestion(7));

        // Fill the rest with random questions
//        for (int i = 8; i < totalQuestions; i++) {
//            int type = random.nextInt(8); // 0-7 for question types
//
//            switch (type) {
//                case 0:
//                    questions.add(generateSliderQuestion(i));
//                    break;
//                case 1:
//                    questions.add(generateQuizQuestion(i));
//                    break;
//                case 2:
//                    questions.add(generateCheckboxQuestion(i));
//                    break;
//                case 3:
//                    questions.add(generatePuzzleQuestion(i));
//                    break;
//                case 4:
//                    questions.add(generateTextQuestion(i));
//                    break;
//                case 5:
//                    questions.add(generateQuizAudioQuestion(i));
//                    break;
//                case 6:
//                    questions.add(generateTrueFalseQuestion(i));
//                    break;
//                case 7:
//                    questions.add(generateSayWordQuestion(i));
//                    break;
//            }
//        }

        // Shuffle the list
//        Collections.shuffle(questions);
//
//        // Set positions after shuffling
//        for (int i = 0; i < questions.size(); i++) {
//            questions.get(i).setPosition(i + 1);
//        }

        return questions;
    }

    // Generate 5 of each question type
    public static List<Question> generateFiveOfEachType() {
        List<Question> questions = new ArrayList<>();

        // Generate 5 of each type
        for (int i = 0; i < 5; i++) {
            questions.add(generateSliderQuestion(i));
            questions.add(generateQuizQuestion(i));
            questions.add(generateCheckboxQuestion(i));
            questions.add(generatePuzzleQuestion(i));
            questions.add(generateTextQuestion(i));
            questions.add(generateQuizAudioQuestion(i));
            questions.add(generateTrueFalseQuestion(i));
            questions.add(generateSayWordQuestion(i));
        }

        // Shuffle the list
//        Collections.shuffle(questions);
//
//        // Set positions after shuffling
//        for (int i = 0; i < questions.size(); i++) {
//            questions.get(i).setPosition(i + 1);
//        }

        return questions;
    }

    // Helper method to generate a random ID
    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    // Generate Slider Questions (TYPE_SLIDER = 1)
    private static QuestionSlider generateSliderQuestion(int position) {
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

        String[] descriptions = {
                "Find the exact temperature reading",
                "Match the angle shown in degrees",
                "Estimate the correct water level percentage",
                "Position the marker at the correct historical date",
                "Set the volume to match the displayed level"
        };
        String[] lambdas = {
                "Default",
                "Small",
                "Medium",
                "Large"
        };

        int titleIndex = random.nextInt(titles.length);
        int minValue = random.nextInt(50);
        int maxValue = minValue + 50 + random.nextInt(100);
        int correctValue = minValue + random.nextInt(maxValue - minValue + 1);
        int defaultValue = (minValue + maxValue) / 2;
        int lambdaIndex = random.nextInt(lambdas.length); // 0-3 for lambda values

        QuestionSlider question = new QuestionSlider(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio for slider questions
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                minValue,
                maxValue,
                defaultValue,
                correctValue,
                lambdas[lambdaIndex]
        );

        question.setQuestionType(questionTypes.get(0)); // Set the question type

        return question;
    }

    // Generate Quiz Questions (TYPE_QUIZ = 2)
    private static QuestionChoice generateQuizQuestion(int position) {
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

        String[] descriptions = {
                "Choose the animal that matches the image",
                "Identify the capital city shown",
                "Select the matching chemical symbol",
                "Pick the formula that matches the image",
                "Identify the historical figure in the portrait"
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

        List<ChoiceOption> options = new ArrayList<>();

        for (int i = 0; i < optionSets[titleIndex].length; i++) {
            options.add(new ChoiceOption(
                    i,
                    generateId(),
                    optionSets[titleIndex][i],
                    "",  // No image for individual options
                    "",  // No audio for individual options
                    i == correctOption  // Set correct flag
            ));
        }

        QuestionChoice question = new QuestionChoice(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio for the question
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                options
        );

        question.setQuestionType(questionTypes.get(1)); // Set the question type

        return question;
    }

    // Generate Checkbox Questions (TYPE_CHECKBOX = 3)
    private static QuestionChoice generateCheckboxQuestion(int position) {
        String[] titles = {
                "Select all animals that are mammals",
                "Choose all elements that are metals",
                "Identify all countries in South America",
                "Select all prime numbers",
                "Which of these are programming languages?"
        };

        String[] imageUrls = {
                "https://example.com/images/animal_groups.jpg",
                "https://example.com/images/periodic_table_section.jpg",
                "https://example.com/images/south_america_map.jpg",
                "https://example.com/images/number_chart.jpg",
                "https://example.com/images/coding_languages.jpg"
        };

        String[] descriptions = {
                "Check all animals that belong to the mammal class",
                "Select all elements that are classified as metals",
                "Choose all countries located in South America",
                "Identify all prime numbers from the list",
                "Select all programming languages from the options"
        };

        String[][] optionSets = {
                {"Dolphin", "Shark", "Bat", "Penguin", "Eagle", "Cat"},
                {"Iron", "Helium", "Copper", "Oxygen", "Gold", "Neon"},
                {"Brazil", "Spain", "Chile", "Egypt", "Peru", "Mexico"},
                {"2", "4", "7", "9", "11", "15"},
                {"Python", "Excel", "Java", "Word", "JavaScript", "PowerPoint"}
        };

        boolean[][] correctAnswers = {
                {true, false, true, false, false, true},
                {true, false, true, false, true, false},
                {true, false, true, false, true, false},
                {true, false, true, false, true, false},
                {true, false, true, false, true, false}
        };

        int titleIndex = random.nextInt(titles.length);

        List<ChoiceOption> options = new ArrayList<>();

        for (int i = 0; i < optionSets[titleIndex].length; i++) {
            options.add(new ChoiceOption(
                    i,
                    generateId(),
                    optionSets[titleIndex][i],
                    "",  // No image for individual options
                    "",  // No audio for individual options
                    correctAnswers[titleIndex][i]  // Set correct flag based on predefined answers
            ));
        }

        QuestionChoice question = new QuestionChoice(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio for the question
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                options
        );

        question.setQuestionType(questionTypes.get(2)); // Set the question type

        return question;
    }

    // Generate Puzzle Questions (TYPE_PUZZLE = 4)
    private static QuestionPuzzle generatePuzzleQuestion(int position) {
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

        String[] descriptions = {
                "Place the historical events in chronological order",
                "Arrange the puzzle pieces to complete the image",
                "Order the steps of the chemical reaction correctly",
                "Place the code blocks in the correct sequence",
                "Arrange the story events in the right order"
        };

        String[][] puzzlePieces = {
                {"World War I", "Great Depression", "World War II", "Cold War", "Digital Age"},
                {"Foundation", "Walls", "Windows", "Roof", "Doors"},
                {"Reactants", "Activation", "Transition State", "Products", "Energy Release"},
                {"Initialize", "Input", "Process", "Condition", "Output"},
                {"Introduction", "Rising Action", "Climax", "Falling Action", "Resolution"}
        };

        int titleIndex = random.nextInt(titles.length);
        List<String> pieces = Arrays.asList(puzzlePieces[titleIndex]);
        Collections.shuffle(pieces);

        List<PuzzleOption> puzzleOptions = new ArrayList<>();

        for (int i = 0; i < pieces.size(); i++) {
            puzzleOptions.add(new PuzzleOption(
                    i,
                    generateId(),
                    pieces.get(i),
                    "",
                    "",
                    i
            ));
        }

        QuestionPuzzle question = new QuestionPuzzle(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",
                200,
                10,
                descriptions[titleIndex],
                puzzleOptions
        );

        question.setQuestionType(questionTypes.get(3));

        return question;
    }

    // Generate Text Questions (TYPE_TEXT = 5)
    private static QuestionTypeText generateTextQuestion(int position) {
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

        String[] descriptions = {
                "Type the name of the animal",
                "Enter the exact chemical formula",
                "Type the name of the capital city",
                "Enter the numerical solution",
                "Type the full name of the person shown"
        };

        String[][] acceptedAnswers = {
                {"platypus", "duck-billed platypus"},
                {"H2O", "h2o"},
                {"Wellington", "wellington"},
                {"42", "forty-two", "forty two"},
                {"Marie Curie", "marie curie", "Marie Sklodowska Curie"}
        };

        int titleIndex = random.nextInt(titles.length);

        List<TypeTextOption> options = new ArrayList<>();
        int cnt = 0;
        for (String answer : acceptedAnswers[titleIndex]) {
            options.add(new TypeTextOption(
                    cnt,
                    generateId(),
                    answer,
                    "",  // No image for answer options
                    ""  // No audio for answer options
            ));
            cnt++;
        }

        QuestionTypeText question = new QuestionTypeText(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio for the question
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                options,
                false  // Not case sensitive
        );

        question.setQuestionType(questionTypes.get(4)); // Set the question type

        return question;
    }

    // Generate Quiz Audio Questions (TYPE_QUIZ_AUDIO = 6)
    private static QuestionChoice generateQuizAudioQuestion(int position) {
        String[] titles = {
                "Identify the instrument playing in the audio",
                "Which animal makes this sound?",
                "What language is being spoken in the recording?",
                "Identify the musical genre of this clip",
                "What natural phenomenon makes this sound?"
        };

        String[] audioUrls = {
                "https://example.com/audio/instrument_sample.mp3",
                "https://example.com/audio/animal_sound.mp3",
                "https://example.com/audio/language_sample.mp3",
                "https://example.com/audio/music_genre.mp3",
                "https://example.com/audio/nature_sound.mp3"
        };

        String[] descriptions = {
                "Listen carefully and identify the musical instrument",
                "Choose the animal that makes this distinctive sound",
                "Identify the language being spoken in the audio clip",
                "Select the genre that best describes this music sample",
                "Choose the natural phenomenon that creates this sound"
        };

        String[][] optionSets = {
                {"Piano", "Violin", "Flute", "Guitar"},
                {"Lion", "Elephant", "Wolf", "Whale"},
                {"French", "Spanish", "Mandarin", "Russian"},
                {"Classical", "Jazz", "Rock", "Electronic"},
                {"Thunderstorm", "Volcano", "Avalanche", "Earthquake"}
        };

        int titleIndex = random.nextInt(titles.length);
        int correctOption = random.nextInt(4);

        List<ChoiceOption> options = new ArrayList<>();

        for (int i = 0; i < optionSets[titleIndex].length; i++) {
            options.add(new ChoiceOption(
                    i,
                    generateId(),
                    optionSets[titleIndex][i],
                    "",
                    "",
                    i == correctOption
            ));
        }

        QuestionChoice question = new QuestionChoice(
                position,
                generateId(),
                titles[titleIndex],
                "",  // No image for audio questions
                audioUrls[titleIndex],  // Use audio URL
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                options
        );

        question.setQuestionType(questionTypes.get(5)); // Set the question type

        return question;
    }

    // Generate True/False Questions (TYPE_TRUE_FALSE = 7)
    private static QuestionTrueFalse generateTrueFalseQuestion(int position) {
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

        String[] descriptions = {
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

        QuestionTrueFalse question = new QuestionTrueFalse(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio for the question
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                answers[titleIndex]
        );

        question.setQuestionType(questionTypes.get(6)); // Set the question type

        return question;
    }

    // Generate Say Word Questions (TYPE_SAY_WORD = 8)
    private static QuestionSayWord generateSayWordQuestion(int position) {
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

        String[] descriptions = {
                "Pronounce this animal's name clearly",
                "Say the country name with correct pronunciation",
                "Pronounce this scientific term accurately",
                "Say the historical figure's name correctly",
                "Pronounce this foreign word as a native speaker would"
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

        QuestionSayWord question = new QuestionSayWord(
                position,
                generateId(),
                titles[titleIndex],
                imageUrls[titleIndex],
                "",  // No audio URL for the question itself
                200, // default point value
                10,  // default time value
                descriptions[titleIndex],
                words[titleIndex],  // Using the word as the correct answer
                words[titleIndex],
                pronunciations[titleIndex]
        );

        question.setQuestionType(questionTypes.get(7)); // Set the question type

        return question;
    }
}