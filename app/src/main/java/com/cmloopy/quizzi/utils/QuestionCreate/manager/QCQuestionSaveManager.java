package com.cmloopy.quizzi.utils.QuestionCreate.manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionSaveService;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.utils.QuestionCreate.storage.QCLocalStorageUtils;

import java.util.List;
import java.util.Map;

public class QCQuestionSaveManager {
    private static final String TAG = "QuestionSaveManager";

    private final Context context;
    private final QuestionSaveService saveService;
    private Long quizId;

    public QCQuestionSaveManager(@NonNull Context context, Long quizId) {
        this.context = context;
        this.saveService = new QuestionSaveService(context);

        if(quizId != null) {
            this.quizId = quizId;
        }


        if (this.quizId == null) {
            this.quizId = 1L;
            Log.w(TAG, "Using default quiz ID: " + this.quizId);
        }
    }

    public void initialize(List<Question> questions) {
        saveService.initializeChangeTracker(questions);
    }

    public void onQuestionUpdated(int position, Question question) {
        saveService.registerQuestionChange(position, question);
    }

    public void onQuestionAdded(int position) {
        saveService.registerNewQuestion(position);
    }

    public void onQuestionDeleted(int position) {
        saveService.registerDeletedQuestion(position);
    }

    public boolean hasUnsavedChanges() {
        return saveService.hasUnsavedChanges();
    }

    public void saveAllChanges(List<Question> questions, QuestionSaveService.SaveOperationListener listener) {
        if (quizId == null) {
            listener.onSaveComplete(false, "No quiz ID available");
            return;
        }

        saveService.saveAllChanges(questions, quizId, listener);
    }

    public void showBackConfirmationDialog(
            List<Question> questions,
            Runnable onDiscardCallback,
            QuestionSaveService.SaveOperationListener onSaveCallback) {

        if (!hasUnsavedChanges()) {
            onDiscardCallback.run();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Unsaved Changes");
        builder.setMessage("You have unsaved changes. Would you like to save them before exiting?");

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();

            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Saving changes...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            saveAllChanges(questions, (isSuccessful, message) -> {
                progressDialog.dismiss();
                onSaveCallback.onSaveComplete(isSuccessful, message);
            });
        });

        builder.setNegativeButton("Discard", (dialog, which) -> {
            dialog.dismiss();
            onDiscardCallback.run();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showSaveConfirmationDialog(List<Question> questions) {
        if (!hasUnsavedChanges()) {
            Toast.makeText(context, "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save Changes");
        builder.setMessage("Do you want to save all changes to this quiz?");

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();

            // Show a progress dialog while saving
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Saving");
            progressDialog.setMessage("Saving your changes...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            saveAllChanges(questions, (isSuccessful, message) -> {
                progressDialog.dismiss();

                if (isSuccessful) {
                    Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("Save Error")
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showSaveConfirmationDialog2(List<Question> questions) {
        if (!hasUnsavedChanges()) {
            Toast.makeText(context, "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save Changes");
        builder.setMessage("Do you want to save all changes to this quiz? This will replace all existing questions.");

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();

            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Saving");
            progressDialog.setMessage("Replacing all questions...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            saveService.saveAllQuestionsWithFullReset(questions, quizId, (isSuccessful, message) -> {
                progressDialog.dismiss();

                if (isSuccessful) {
                    Toast.makeText(context, "Questions replaced successfully", Toast.LENGTH_SHORT).show();
                    // Reset change tracker after successful save
                    saveService.initializeChangeTracker(questions);
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("Save Error")
                            .setMessage(message)
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteConfirmationDialog(
            Runnable onConfirmedDelete
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Question");
        builder.setMessage("Are you sure you want to delete this question? This action cannot be undone.");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            dialog.dismiss();
            onConfirmedDelete.run();

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
