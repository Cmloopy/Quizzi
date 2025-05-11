package com.cmloopy.quizzi.fragment.QuestionCreate;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSayWord;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;

public class QCQuestionFragmentManager {
    private FragmentManager fragmentManager;
    private int containerId;

    public QCQuestionFragmentManager(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void showQuestionFragment(Question question, QCBaseQuestionFragment.OnChangeListener listener) {
        Fragment fragment = null;

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


        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
