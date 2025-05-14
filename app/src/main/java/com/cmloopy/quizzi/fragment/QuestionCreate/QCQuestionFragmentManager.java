package com.cmloopy.quizzi.fragment.QuestionCreate;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSayWord;

public class QCQuestionFragmentManager {
    private FragmentManager fragmentManager;
    private int containerId;

    public QCQuestionFragmentManager(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void showQuestionFragment(QuestionCreate questionCreate, QCBaseQuestionFragment.OnChangeListener listener) {
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

        switch (questionCreate.getQuestionType().getName()) {
            case "SLIDER":
                if (questionCreate instanceof QuestionCreateSlider) {
                    fragment = QCQuestionSliderFragment.newInstance((QuestionCreateSlider) questionCreate);
                    ((QCQuestionSliderFragment) fragment).setListener(listener);
                }
                break;
            case "SINGLE_CHOICE":
                if (questionCreate instanceof QuestionCreateChoice) {
                    fragment = QCQuestionQuizFragment.newInstance((QuestionCreateChoice) questionCreate);
                    ((QCQuestionQuizFragment) fragment).setListener(listener);
                }
                break;
            case "MULTI_CHOICE":
                if (questionCreate instanceof QuestionCreateChoice) {
                    fragment = QCQuestionCheckboxFragment.newInstance((QuestionCreateChoice) questionCreate);
                    ((QCQuestionCheckboxFragment) fragment).setListener(listener);
                }
                break;
            case "PUZZLE":
                if (questionCreate instanceof QuestionCreatePuzzle) {
                    fragment = QCQuestionPuzzleFragment.newInstance((QuestionCreatePuzzle) questionCreate);
                    ((QCQuestionPuzzleFragment) fragment).setListener(listener);
                }
                break;
            case "TEXT":
                if (questionCreate instanceof QuestionCreateTypeText) {
                    fragment = QCQuestionTypeTextFragment.newInstance((QuestionCreateTypeText) questionCreate);
                    ((QCQuestionTypeTextFragment) fragment).setListener(listener);
                }
                break;
            case "AUDIO_SINGLE_CHOICE":
                if (questionCreate instanceof QuestionCreateChoice) {
                    fragment = QCQuestionQuizAudioFragment.newInstance((QuestionCreateChoice) questionCreate);
                    ((QCQuestionQuizAudioFragment) fragment).setListener(listener);
                }
                break;
            case "TRUE_FALSE":
                if (questionCreate instanceof QuestionCreateTrueFalse) {
                    fragment = QCQuestionTrueFalseFragment.newInstance((QuestionCreateTrueFalse) questionCreate);
                    ((QCQuestionTrueFalseFragment) fragment).setListener(listener);
                }
                break;
            case "SAY_WORD":
                if (questionCreate instanceof QuestionCreateSayWord) {
                    fragment = QCQuestionSayWordFragment.newInstance((QuestionCreateSayWord) questionCreate);
                    ((QCQuestionSayWordFragment) fragment).setListener(listener);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown question type: " + questionCreate.getQuestionType().getName());
        }

        if (fragment == null) {
            throw new ClassCastException("Question object is not of the expected type for question type: " +
                    questionCreate.getQuestionType().getName());
        }

        fragmentManager.beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}
