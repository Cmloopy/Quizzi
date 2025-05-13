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

//        switch (question.getType()) {
//            case Question.TYPE_SLIDER:
//                fragment = QCQuestionSliderFragment.newInstance((QuestionSlider) question);
//                ((QCQuestionSliderFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_QUIZ:
//                fragment = QCQuestionQuizFragment.newInstance((QuestionChoice) question);
//                ((QCQuestionQuizFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_CHECKBOX:
//                fragment = QCQuestionCheckboxFragment.newInstance((QuestionChoice) question);
//                ((QCQuestionCheckboxFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_PUZZLE:
//                fragment = QCQuestionPuzzleFragment.newInstance((QuestionPuzzle) question);
//                ((QCQuestionPuzzleFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_TEXT:
//                fragment = QCQuestionTypeTextFragment.newInstance((QuestionTypeText) question);
//                ((QCQuestionTypeTextFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_QUIZ_AUDIO:
//                fragment = QCQuestionQuizAudioFragment.newInstance((QuestionChoice) question);
//                ((QCQuestionQuizAudioFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_TRUE_FALSE:
//                fragment = QCQuestionTrueFalseFragment.newInstance((QuestionTrueFalse) question);
//                ((QCQuestionTrueFalseFragment) fragment).setListener(listener);
//                break;
//            case Question.TYPE_SAY_WORD:
//                fragment = QCQuestionSayWordFragment.newInstance((QuestionSayWord) question);
//                ((QCQuestionSayWordFragment) fragment).setListener(listener);
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown question type: " + question.getType());
//        }

        switch (question.getQuestionType().getName()) {
            case "SLIDER":
                if (question instanceof QuestionSlider) {
                    fragment = QCQuestionSliderFragment.newInstance((QuestionSlider) question);
                    ((QCQuestionSliderFragment) fragment).setListener(listener);
                }
                break;
            case "SINGLE_CHOICE":
                if (question instanceof QuestionChoice) {
                    fragment = QCQuestionQuizFragment.newInstance((QuestionChoice) question);
                    ((QCQuestionQuizFragment) fragment).setListener(listener);
                }
                break;
            case "MULTI_CHOICE":
                if (question instanceof QuestionChoice) {
                    fragment = QCQuestionCheckboxFragment.newInstance((QuestionChoice) question);
                    ((QCQuestionCheckboxFragment) fragment).setListener(listener);
                }
                break;
            case "PUZZLE":
                if (question instanceof QuestionPuzzle) {
                    fragment = QCQuestionPuzzleFragment.newInstance((QuestionPuzzle) question);
                    ((QCQuestionPuzzleFragment) fragment).setListener(listener);
                }
                break;
            case "TEXT":
                if (question instanceof QuestionTypeText) {
                    fragment = QCQuestionTypeTextFragment.newInstance((QuestionTypeText) question);
                    ((QCQuestionTypeTextFragment) fragment).setListener(listener);
                }
                break;
            case "AUDIO_SINGLE_CHOICE":
                if (question instanceof QuestionChoice) {
                    fragment = QCQuestionQuizAudioFragment.newInstance((QuestionChoice) question);
                    ((QCQuestionQuizAudioFragment) fragment).setListener(listener);
                }
                break;
            case "TRUE_FALSE":
                if (question instanceof QuestionTrueFalse) {
                    fragment = QCQuestionTrueFalseFragment.newInstance((QuestionTrueFalse) question);
                    ((QCQuestionTrueFalseFragment) fragment).setListener(listener);
                }
                break;
            case "SAY_WORD":
                if (question instanceof QuestionSayWord) {
                    fragment = QCQuestionSayWordFragment.newInstance((QuestionSayWord) question);
                    ((QCQuestionSayWordFragment) fragment).setListener(listener);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown question type: " + question.getQuestionType().getName());
        }

        if (fragment == null) {
            throw new ClassCastException("Question object is not of the expected type for question type: " +
                    question.getQuestionType().getName());
        }

        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
