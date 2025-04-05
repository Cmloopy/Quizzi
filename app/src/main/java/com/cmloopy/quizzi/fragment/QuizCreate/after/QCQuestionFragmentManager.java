package com.cmloopy.quizzi.fragment.QuizCreate.after;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cmloopy.quizzi.models.QuizCreate.after.Question;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionChoice;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionSayWord;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionSlider;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTypeText;

//same newInstance method for the fragment i provide you
//newInstance accpeted that type of question as an argument declare variable for the key when putArgs
//no serializable set each attribute declare the key for each attribute
//and provide default value if get from args is null
//this is for if user create question instead of see the detail question
//if args is null which mean answer you should based on my code init empty answer

public class QCQuestionFragmentManager {
    private FragmentManager fragmentManager;
    private int containerId;

    public QCQuestionFragmentManager(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void showQuestionFragment(Question question, QCBaseResponseFragment.OnChangeListener listener) {
        Fragment fragment = null;

        // Create appropriate fragment based on question type
        switch (question.getType()) {
            case Question.TYPE_SLIDER:
                fragment = QCQuestionSliderFragment.newInstance((QuestionSlider) question);
                ((QCQuestionSliderFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_QUIZ:
                fragment = QCQuestionQuizFragment.newInstance((QuestionChoice) question);
                ((QCQuestionQuizFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_CHECKBOX:
                fragment = QCQuestionCheckboxFragment.newInstance((QuestionChoice) question);
                ((QCQuestionCheckboxFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_PUZZLE:
                fragment = QCQuestionPuzzleFragment.newInstance((QuestionPuzzle) question);
                ((QCQuestionPuzzleFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_TEXT:
                fragment = QCQuestionTypeTextFragment.newInstance((QuestionTypeText) question);
                ((QCQuestionTypeTextFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_QUIZ_AUDIO:
                fragment = QCQuestionQuizAudioFragment.newInstance((QuestionChoice) question);
                ((QCQuestionQuizAudioFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_TRUE_FALSE:
                fragment = QCQuestionTrueFalseFragment.newInstance((QuestionTrueFalse) question);
                ((QCQuestionTrueFalseFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_SAY_WORD:
                fragment = QCQuestionSayWordFragment.newInstance((QuestionSayWord) question);
                ((QCQuestionSayWordFragment) fragment).setListener(listener);
                break;
            default:
                throw new IllegalArgumentException("Unknown question type: " + question.getType());
        }



        // Replace current fragment with new one
        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
