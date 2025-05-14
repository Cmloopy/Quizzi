package com.cmloopy.quizzi.utils.QuestionCreate.tracker;

import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to track changes to questions in the quiz creation process
 */
public class QCQuestionChangeTracker {

    public enum ChangeState {
        UNCHANGED,
        MODIFIED,
        NEW,
        DELETED
    }

    private Map<Integer, ChangeState> questionStates = new HashMap<>();

    private Map<Integer, QuestionCreate> originalQuestions = new HashMap<>();

    private List<Integer> deletedPositions = new ArrayList<>();

    private boolean hasChanges = false;
    public void initialize(List<QuestionCreate> questionCreates) {
        questionStates.clear();
        originalQuestions.clear();
        deletedPositions.clear();
        hasChanges = false;

        if (questionCreates != null) {
            for (QuestionCreate questionCreate : questionCreates) {
                int position = questionCreate.getPosition();
                questionStates.put(position, ChangeState.UNCHANGED);

                originalQuestions.put(position, createDeepCopy(questionCreate));
            }
        }
    }

    private QuestionCreate createDeepCopy(QuestionCreate questionCreate) {
        return questionCreate;
    }
    public void questionModified(int position, QuestionCreate questionCreate) {
        if (questionStates.containsKey(position)) {
            if (questionStates.get(position) != ChangeState.NEW) {
                questionStates.put(position, ChangeState.MODIFIED);
            }
        } else {
            questionStates.put(position, ChangeState.NEW);
        }
        hasChanges = true;
    }

    public void questionAdded(int position) {
        questionStates.put(position, ChangeState.NEW);
        hasChanges = true;
    }

    public void questionDeleted(int position) {
        if (questionStates.containsKey(position)) {
            if (questionStates.get(position) == ChangeState.NEW) {
                // If it was a new question, just remove it from tracking
                questionStates.remove(position);
            } else {
                questionStates.put(position, ChangeState.DELETED);
                deletedPositions.add(position);
            }
        }
        hasChanges = true;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public ChangeState getQuestionState(int position) {
        return questionStates.getOrDefault(position, ChangeState.UNCHANGED);
    }

    public List<Integer> getDeletedPositions() {
        return deletedPositions;
    }

    public void resetAfterSave(List<QuestionCreate> currentQuestionCreates) {
        initialize(currentQuestionCreates);
    }
}