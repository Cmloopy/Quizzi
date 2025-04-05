package com.cmloopy.quizzi.fragment.QuizCreate.before;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cmloopy.quizzi.models.QuizCreate.before.CheckboxQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.PuzzleQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizAudioQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SayWordQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SliderQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TrueFalseQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TypeQuestion;

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

    public void showQuestionFragment(Question question, QCBaseQuestionFragment.OnChangeListener listener) {
        Fragment fragment = null;

        // Create appropriate fragment based on question type
        switch (question.getType()) {
            case Question.TYPE_SLIDER:
                fragment = QCSliderQuestionTypeFragment.newInstance((SliderQuestion) question);
                ((QCSliderQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_QUIZ:
                fragment = QCQuizQuestionTypeFragment.newInstance((QuizQuestion) question);
                ((QCQuizQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_CHECKBOX:
                fragment = QCCheckboxQuestionTypeFragment.newInstance((CheckboxQuestion) question);
                ((QCCheckboxQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_PUZZLE:
                fragment = QCPuzzleQuestionTypeFragment.newInstance((PuzzleQuestion) question);
                ((QCPuzzleQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_TEXT:
                fragment = QCTypeQuestionTypeFragment.newInstance((TypeQuestion) question);
                ((QCTypeQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_QUIZ_AUDIO:
                fragment = QCQuizAudioQuestionTypeFragment.newInstance((QuizAudioQuestion) question);
                ((QCQuizAudioQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_TRUE_FALSE:
                fragment = QCTrueFalseQuestionTypeFragment.newInstance((TrueFalseQuestion) question);
                ((QCTrueFalseQuestionTypeFragment) fragment).setListener(listener);
                break;
            case Question.TYPE_SAY_WORD:
                fragment = QCSayWordQuestionTypeFragment.newInstance((SayWordQuestion) question);
                ((QCSayWordQuestionTypeFragment) fragment).setListener(listener);
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
