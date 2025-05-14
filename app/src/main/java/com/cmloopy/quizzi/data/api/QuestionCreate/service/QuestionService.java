package com.cmloopy.quizzi.data.api.QuestionCreate.service;

import android.content.Context;
import android.util.Log;

import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.models.QuestionCreate.BatchQuestionDTO;
import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;
import com.cmloopy.quizzi.utils.QuestionCreate.tracker.QCQuestionChangeTracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionService {
    private static final String TAG = "QuestionSaveService";
    private final Context context;
    private final QCQuestionChangeTracker changeTracker;

    private QuestionAPI apiService;

    public QuestionService(Context context) {
        this.context = context;
        this.changeTracker = new QCQuestionChangeTracker();
        this.apiService = RetrofitClient.getQuestionApi();
    }

    public void initializeChangeTracker(List<Question> questions) {
        changeTracker.initialize(questions);
    }

    public void registerQuestionChange(int position, Question question) {
        changeTracker.questionModified(position, question);
    }

    public void registerNewQuestion(int position) {
        changeTracker.questionAdded(position);
    }

    public void registerDeletedQuestion(int position) {
        changeTracker.questionDeleted(position);
    }

    public boolean hasUnsavedChanges() {
        return changeTracker.hasChanges();
    }

    public void saveAllChanges(List<Question> questions, Long quizId, final SaveOperationListener listener) {
        if (!changeTracker.hasChanges()) {
            listener.onSaveComplete(true, "No changes to save");
            return;
        }

        final AtomicBoolean success = new AtomicBoolean(true);
        final AtomicInteger remainingOperations = new AtomicInteger(0);
        final List<String> errors = new ArrayList<>();

        for (Question question : questions) {
            QCQuestionChangeTracker.ChangeState state = changeTracker.getQuestionState(question.getPosition());
            if (state == QCQuestionChangeTracker.ChangeState.MODIFIED ||
                    state == QCQuestionChangeTracker.ChangeState.NEW) {
                remainingOperations.incrementAndGet();
            }
        }

        remainingOperations.addAndGet(changeTracker.getDeletedPositions().size());

        if (remainingOperations.get() == 0) {
            listener.onSaveComplete(true, "All changes saved successfully");
            return;
        }

        for (Integer position : changeTracker.getDeletedPositions()) {
            Question deletedQuestion = findQuestionByPosition(questions, position);
            if (deletedQuestion != null && deletedQuestion.getId() != null) {
                deleteQuestion(deletedQuestion.getId(), new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to delete question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questions);
                        }
                    }
                });
            } else {
                if (remainingOperations.decrementAndGet() == 0) {
                    finalizeSaveOperation(listener, success.get(), errors, questions);
                }
            }
        }

        for (Question question : questions) {
            QCQuestionChangeTracker.ChangeState state = changeTracker.getQuestionState(question.getPosition());

            if (state == QCQuestionChangeTracker.ChangeState.NEW) {
                createQuestion(question, quizId, new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to create question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questions);
                        }
                    }
                });
            } else if (state == QCQuestionChangeTracker.ChangeState.MODIFIED) {
                updateQuestion(question, quizId, new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to update question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questions);
                        }
                    }
                });
            }
        }
    }

    private void finalizeSaveOperation(SaveOperationListener listener, boolean success, List<String> errors, List<Question> currentQuestions) {
        if (success) {
            changeTracker.resetAfterSave(currentQuestions);
            listener.onSaveComplete(true, "All changes saved successfully");
        } else {
            StringBuilder errorMessage = new StringBuilder("Save operation failed: ");
            for (String error : errors) {
                errorMessage.append(error).append("; ");
            }
            listener.onSaveComplete(false, errorMessage.toString());
        }
    }

    private Question findQuestionByPosition(List<Question> questions, int position) {
        for (Question q : questions) {
            if (q.getPosition() == position) {
                return q;
            }
        }
        return null;
    }

    private void createQuestion(Question question, Long quizId, final OperationCallback callback) {
        if (question instanceof QuestionTrueFalse) {
            createTrueFalseQuestion((QuestionTrueFalse) question, quizId, callback);
        } else if (question instanceof QuestionChoice) {
            createChoiceQuestion((QuestionChoice) question, quizId, callback);
        } else if (question instanceof QuestionSlider) {
            createSliderQuestion((QuestionSlider) question, quizId, callback);
        } else if (question instanceof QuestionPuzzle) {
            createPuzzleQuestion((QuestionPuzzle) question, quizId, callback);
        } else if (question instanceof QuestionTypeText) {
            createTextQuestion((QuestionTypeText) question, quizId, callback);
        } else {
            // Generic question
            createGenericQuestion(question, quizId, callback);
        }
    }

    private void updateQuestion(Question question, Long quizId, final OperationCallback callback) {
        if (question.getId() == null) {
            callback.onComplete(false, "Cannot update question without ID");
            return;
        }

        if (question instanceof QuestionTrueFalse) {
            updateTrueFalseQuestion((QuestionTrueFalse) question, quizId, callback);
        } else if (question instanceof QuestionChoice) {
            updateChoiceQuestion((QuestionChoice) question, quizId, callback);
        } else if (question instanceof QuestionSlider) {
            updateSliderQuestion((QuestionSlider) question, quizId, callback);
        } else if (question instanceof QuestionPuzzle) {
            updatePuzzleQuestion((QuestionPuzzle) question, quizId, callback);
        } else if (question instanceof QuestionTypeText) {
            updateTextQuestion((QuestionTypeText) question, quizId, callback);
        } else {
            // Generic question
            updateGenericQuestion(question, quizId, callback);
        }
    }

    public void deleteQuestionById(Long questionId, final SaveOperationListener listener) {
        if (questionId == null || questionId < 1) {
            listener.onSaveComplete(false, "Invalid question ID");
            return;
        }

        apiService.deleteQuestion(questionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSaveComplete(true, "Question deleted successfully");
                } else {
                    listener.onSaveComplete(false, "Failed to delete question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onSaveComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void deleteQuestion(Long questionId, final OperationCallback callback) {
        apiService.deleteQuestion(questionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Question deleted successfully");
                } else {
                    callback.onComplete(false, "Failed to delete question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }


    private void createGenericQuestion(Question question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody questionTypeIdBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getQuestionType().getId().toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.createQuestion(
                quizIdBody, questionTypeIdBody, contentBody, positionBody, pointBody,
                timeLimitBody, descriptionBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateGenericQuestion(Question question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody questionTypeIdBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getQuestionType().getId().toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));

        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.updateQuestion(
                question.getId(), quizIdBody, questionTypeIdBody, contentBody, positionBody, pointBody,
                timeLimitBody, descriptionBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createTrueFalseQuestion(QuestionTrueFalse question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCorrectAnswer()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.createTrueFalseQuestion(
                quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, correctAnswerBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionTrueFalse>() {
            @Override
            public void onResponse(Call<QuestionTrueFalse> call, Response<QuestionTrueFalse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "True/False question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create True/False question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionTrueFalse> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateTrueFalseQuestion(QuestionTrueFalse question, Long quizId, final OperationCallback callback) {
        // Similar implementation for updating true/false question
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCorrectAnswer()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.updateTrueFalseQuestion(
                question.getId(), quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, correctAnswerBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionTrueFalse>() {
            @Override
            public void onResponse(Call<QuestionTrueFalse> call, Response<QuestionTrueFalse> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "True/False question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update True/False question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionTrueFalse> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createChoiceQuestion(QuestionChoice question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        List<MultipartBody.Part> choiceOptionsParts = new ArrayList<>();
        if (question.getChoiceOptions() != null) {
            for (int i = 0; i < question.getChoiceOptions().size(); i++) {
                ChoiceOption option = question.getChoiceOptions().get(i);

                choiceOptionsParts.add(
                        MultipartBody.Part.createFormData(
                                "choiceOptions[" + i + "].text",
                                option.getText() != null ? option.getText() : ""
                        )
                );

                choiceOptionsParts.add(
                        MultipartBody.Part.createFormData(
                                "choiceOptions[" + i + "].isCorrect",
                                String.valueOf(option.isCorrect())
                        )
                );

                if (option.getImage() != null && !option.getImage().isEmpty()) {
                    choiceOptionsParts.add(
                            MultipartBody.Part.createFormData(
                                    "choiceOptions[" + i + "].image",
                                    option.getImage()
                            )
                    );
                }

                if (option.getAudio() != null && !option.getAudio().isEmpty()) {
                    choiceOptionsParts.add(
                            MultipartBody.Part.createFormData(
                                    "choiceOptions[" + i + "].audio",
                                    option.getAudio()
                            )
                    );
                }

                choiceOptionsParts.add(
                        MultipartBody.Part.createFormData(
                                "choiceOptions[" + i + "].position",
                                String.valueOf(option.getPosition())
                        )
                );
            }
        }

        apiService.createChoiceQuestion(
                quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, choiceOptionsParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionChoice>() {
            @Override
            public void onResponse(Call<QuestionChoice> call, Response<QuestionChoice> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Choice question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Choice question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionChoice> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateChoiceQuestion(QuestionChoice question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        List<MultipartBody.Part> choiceOptionsParts = new ArrayList<>();
        if (question.getChoiceOptions() != null) {
            for (int i = 0; i < question.getChoiceOptions().size(); i++) {
                ChoiceOption option = question.getChoiceOptions().get(i);

                choiceOptionsParts.add(
                        MultipartBody.Part.createFormData(
                                "choiceOptions[" + i + "].text",
                                option.getText() != null ? option.getText() : ""
                        )
                );

                choiceOptionsParts.add(
                        MultipartBody.Part.createFormData(
                                "choiceOptions[" + i + "].isCorrect",
                                String.valueOf(option.isCorrect())
                        )
                );

                if (option.getImage() != null && !option.getImage().isEmpty()) {
                    choiceOptionsParts.add(
                            MultipartBody.Part.createFormData(
                                    "choiceOptions[" + i + "].image",
                                    option.getImage()
                            )
                    );
                }

                if (option.getAudio() != null && !option.getAudio().isEmpty()) {
                    choiceOptionsParts.add(
                            MultipartBody.Part.createFormData(
                                    "choiceOptions[" + i + "].audio",
                                    option.getAudio()
                            )
                    );
                }
            }
        }

        apiService.updateChoiceQuestion(
                question.getId(), quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, choiceOptionsParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionChoice>() {
            @Override
            public void onResponse(Call<QuestionChoice> call, Response<QuestionChoice> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Choice question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Choice question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionChoice> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createSliderQuestion(QuestionSlider question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody minValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getMinValue()));
        RequestBody maxValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getMaxValue()));
        RequestBody defaultValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getDefaultValue()));
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getCorrectAnswer()));
        RequestBody colorBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getColor() != null ? question.getColor() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.createSliderQuestion(
                quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, minValueBody, maxValueBody, defaultValueBody,
                correctAnswerBody, colorBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionSlider>() {
            @Override
            public void onResponse(Call<QuestionSlider> call, Response<QuestionSlider> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Slider question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Slider question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionSlider> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateSliderQuestion(QuestionSlider question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody minValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getMinValue()));
        RequestBody maxValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getMaxValue()));
        RequestBody defaultValueBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getDefaultValue()));
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getCorrectAnswer()));
        RequestBody colorBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getColor() != null ? question.getColor() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        apiService.updateSliderQuestion(
                question.getId(), quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, minValueBody, maxValueBody, defaultValueBody,
                correctAnswerBody, colorBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionSlider>() {
            @Override
            public void onResponse(Call<QuestionSlider> call, Response<QuestionSlider> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Slider question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Slider question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionSlider> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createPuzzleQuestion(QuestionPuzzle question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        List<MultipartBody.Part> puzzlePiecesParts = new ArrayList<>();
        if (question.getPuzzlePieces() != null) {
            for (int i = 0; i < question.getPuzzlePieces().size(); i++) {
                PuzzleOption piece = question.getPuzzlePieces().get(i);

                puzzlePiecesParts.add(
                        MultipartBody.Part.createFormData(
                                "puzzlePieces[" + i + "].text",
                                piece.getText() != null ? piece.getText() : ""
                        )
                );

                puzzlePiecesParts.add(
                        MultipartBody.Part.createFormData(
                                "puzzlePieces[" + i + "].correctPosition",
                                String.valueOf(piece.getCorrectPosition())
                        )
                );
            }
        }

        apiService.createPuzzleQuestion(
                quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, puzzlePiecesParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionPuzzle>() {
            @Override
            public void onResponse(Call<QuestionPuzzle> call, Response<QuestionPuzzle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Puzzle question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Puzzle question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionPuzzle> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updatePuzzleQuestion(QuestionPuzzle question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        List<MultipartBody.Part> puzzlePiecesParts = new ArrayList<>();
        if (question.getPuzzlePieces() != null) {
            for (int i = 0; i < question.getPuzzlePieces().size(); i++) {
                PuzzleOption piece = question.getPuzzlePieces().get(i);

                puzzlePiecesParts.add(
                        MultipartBody.Part.createFormData(
                                "puzzlePieces[" + i + "].text",
                                piece.getText() != null ? piece.getText() : ""
                        )
                );

                puzzlePiecesParts.add(
                        MultipartBody.Part.createFormData(
                                "puzzlePieces[" + i + "].correctPosition",
                                String.valueOf(piece.getCorrectPosition())
                        )
                );
            }
        }

        apiService.updatePuzzleQuestion(
                question.getId(), quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, puzzlePiecesParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionPuzzle>() {
            @Override
            public void onResponse(Call<QuestionPuzzle> call, Response<QuestionPuzzle> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Puzzle question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Puzzle question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionPuzzle> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createTextQuestion(QuestionTypeText question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody caseSensitiveBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCaseSensitive()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        // Create parts for each accepted answer
        List<MultipartBody.Part> acceptedAnswersParts = new ArrayList<>();
        if (question.getAcceptedAnswers() != null) {
            for (int i = 0; i < question.getAcceptedAnswers().size(); i++) {
                TypeTextOption answer = question.getAcceptedAnswers().get(i);

                // Add text field
                acceptedAnswersParts.add(
                        MultipartBody.Part.createFormData(
                                "acceptedAnswers[" + i + "].text",
                                answer.getText() != null ? answer.getText() : ""
                        )
                );
            }
        }

        apiService.createTextQuestion(
                quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, caseSensitiveBody, acceptedAnswersParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionTypeText>() {
            @Override
            public void onResponse(Call<QuestionTypeText> call, Response<QuestionTypeText> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Text question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Text question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionTypeText> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateTextQuestion(QuestionTypeText question, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getContent() != null ? question.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(question.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody caseSensitiveBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCaseSensitive()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudio());

        List<MultipartBody.Part> acceptedAnswersParts = new ArrayList<>();
        if (question.getAcceptedAnswers() != null) {
            for (int i = 0; i < question.getAcceptedAnswers().size(); i++) {
                TypeTextOption answer = question.getAcceptedAnswers().get(i);

                acceptedAnswersParts.add(
                        MultipartBody.Part.createFormData(
                                "acceptedAnswers[" + i + "].text",
                                answer.getText() != null ? answer.getText() : ""
                        )
                );
            }
        }

        apiService.updateTextQuestion(
                question.getId(), quizIdBody, contentBody, positionBody, pointBody, timeLimitBody,
                descriptionBody, caseSensitiveBody, acceptedAnswersParts, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionTypeText>() {
            @Override
            public void onResponse(Call<QuestionTypeText> call, Response<QuestionTypeText> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Text question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Text question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionTypeText> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }


    private MultipartBody.Part createFilePart(String partName, String uriString) {
        if (uriString == null || uriString.isEmpty()) {
            return MultipartBody.Part.createFormData(partName, "", RequestBody.create(MediaType.parse("text/plain"), ""));
        }

        try {
            android.net.Uri uri = android.net.Uri.parse(uriString);
            String fileName = getFileNameFromUri(uri);

            if (fileName == null) {
                fileName = "file" + System.currentTimeMillis();
            }

            android.content.ContentResolver contentResolver = context.getContentResolver();
            java.io.InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream == null) {
                return MultipartBody.Part.createFormData(partName, "", RequestBody.create(MediaType.parse("text/plain"), ""));
            }

            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(contentResolver.getType(uri)), bytes);

            return MultipartBody.Part.createFormData(partName, fileName, requestFile);

        } catch (Exception e) {
            Log.e(TAG, "Error creating file part: " + e.getMessage());
            return MultipartBody.Part.createFormData(partName, "", RequestBody.create(MediaType.parse("text/plain"), ""));
        }
    }

    private String getFileNameFromUri(android.net.Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public void saveBatchQuestionsWithFullReset(List<Question> questions, Long quizId, final SaveOperationListener listener) {
        if (questions == null || questions.isEmpty()) {
            listener.onSaveComplete(true, "No questions to save");
            return;
        }

        // First, delete all existing questions if needed
        boolean needDelete = false;
        for (Question question : questions) {
            if (question.getId() != null && question.getId() >= 1) {
                needDelete = true;
                break;
            }
        }

        if (needDelete) {
            deleteAllQuizQuestions(quizId, true, new OperationCallback() {
                @Override
                public void onComplete(boolean isDeleteSuccessful, String deleteMessage) {
                    if (!isDeleteSuccessful && !deleteMessage.contains("404")) {
                        listener.onSaveComplete(false, "Failed to delete existing questions: " + deleteMessage);
                        return;
                    }
                    // After deletion, create all questions in batch
                    createQuestionsBatch(questions, quizId, listener);
                }
            });
        } else {
            createQuestionsBatch(questions, quizId, listener);
        }
    }

    private void createQuestionsBatch(List<Question> questions, Long quizId, final SaveOperationListener listener) {
        try {
            RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());

            List<BatchQuestionDTO> batchDTOs = new ArrayList<>();
            Map<String, MultipartBody.Part> fileParts = new HashMap<>();

            for (Question question : questions) {
                BatchQuestionDTO batchDTO = convertToBatchDTO(question, fileParts);
                batchDTOs.add(batchDTO);
            }

            String questionsJson = new Gson().toJson(batchDTOs);
            RequestBody questionsJsonBody = RequestBody.create(
                    MediaType.parse("application/json"), questionsJson);

            // Create the list of file parts
            List<MultipartBody.Part> filePartsList = new ArrayList<>(fileParts.values());

            // Make the API call
            apiService.createQuestionsBatch(quizIdBody, questionsJsonBody, filePartsList)
                    .enqueue(new Callback<List<Question>>() {
                        @Override
                        public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // Update the local questions with server-assigned IDs
                                List<Question> createdQuestions = response.body();
                                for (int i = 0; i < Math.min(questions.size(), createdQuestions.size()); i++) {
                                    questions.get(i).setId(createdQuestions.get(i).getId());
                                }
                                changeTracker.resetAfterSave(questions);
                                listener.onSaveComplete(true, "All questions saved successfully");
                            } else {
                                listener.onSaveComplete(false, "Failed to save questions: " +
                                        (response.errorBody() != null ? response.errorBody().toString() : response.code()));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Question>> call, Throwable t) {
                            listener.onSaveComplete(false, "Network error: " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            listener.onSaveComplete(false, "Error preparing batch request: " + e.getMessage());
        }
    }

    private BatchQuestionDTO convertToBatchDTO(Question question, Map<String, MultipartBody.Part> fileParts) {
        BatchQuestionDTO dto = new BatchQuestionDTO();

        dto.setContent(question.getContent());
        dto.setDescription(question.getDescription());
        dto.setTimeLimit(question.getTimeLimit());
        dto.setPoint(question.getPoint());
        dto.setPosition(question.getPosition());

        if (question.getQuestionType() != null) {
            dto.setQuestionType(question.getQuestionType().getName());
        }

        if (question.getImage() != null && !question.getImage().isEmpty()) {
            String fileName = "image_" + System.currentTimeMillis() + "_" + question.getPosition() + ".jpg";
            dto.setImageFileName(fileName);

            MultipartBody.Part imagePart = createFilePart("files", question.getImage());
            if (imagePart != null) {
                fileParts.put(fileName, imagePart);
            }
        }

        if (question.getAudio() != null && !question.getAudio().isEmpty()) {
            String fileName = "audio_" + System.currentTimeMillis() + "_" + question.getPosition() + ".mp3";
            dto.setAudioFileName(fileName);

            MultipartBody.Part audioPart = createFilePart("files", question.getAudio());
            if (audioPart != null) {
                fileParts.put(fileName, audioPart);
            }
        }

        Map<String, Object> data = new HashMap<>();

        if (question instanceof QuestionTrueFalse) {
            QuestionTrueFalse tf = (QuestionTrueFalse) question;
            data.put("correctAnswer", tf.isCorrectAnswer());
        } else if (question instanceof QuestionChoice) {
            QuestionChoice choice = (QuestionChoice) question;
            data.put("choiceOptions", choice.getChoiceOptions());
        } else if (question instanceof QuestionSlider) {
            QuestionSlider slider = (QuestionSlider) question;
            data.put("minValue", slider.getMinValue());
            data.put("maxValue", slider.getMaxValue());
            data.put("defaultValue", slider.getDefaultValue());
            data.put("correctAnswer", slider.getCorrectAnswer());
            data.put("color", slider.getColor());
        } else if (question instanceof QuestionPuzzle) {
            QuestionPuzzle puzzle = (QuestionPuzzle) question;
            data.put("puzzlePieces", puzzle.getPuzzlePieces());
        } else if (question instanceof QuestionTypeText) {
            QuestionTypeText text = (QuestionTypeText) question;
            data.put("caseSensitive", text.isCaseSensitive());
            data.put("acceptedAnswers", text.getAcceptedAnswers());
        }
//        else {
//        }
        data.put("questionTypeId", question.getQuestionType().getId());

        dto.setData(data);
        return dto;
    }

    public void saveAllQuestionsWithFullReset(List<Question> questions, Long quizId, final SaveOperationListener listener) {
//        if (questions == null || questions.isEmpty()) {
//            listener.onSaveComplete(true, "No questions to save");
//            return;
//        }

        final AtomicBoolean success = new AtomicBoolean(true);
        final List<String> errors = new ArrayList<>();
        final AtomicInteger remainingOperations = new AtomicInteger(0);
        if(!questions.isEmpty()) {
            boolean needDelete = false;
            for(Question question : questions) {
                if (question.getId() != null && question.getId() >= 1)
                    needDelete = true;
            }

            if (!needDelete) {
                remainingOperations.set(questions.size());
                for (Question question : questions) {
                    createQuestion(question, quizId, new OperationCallback() {
                        @Override
                        public void onComplete(boolean isSuccessful, String message) {
                            if (!isSuccessful) {
                                success.set(false);
                                errors.add("Failed to create question: " + message);
                            }

                            if (remainingOperations.decrementAndGet() == 0) {
                                if (success.get()) {
                                    listener.onSaveComplete(true, "All questions created successfully");
                                } else {
                                    StringBuilder errorMessage = new StringBuilder("Some questions failed to create: ");
                                    for (String error : errors) {
                                        errorMessage.append(error).append("; ");
                                    }
                                    listener.onSaveComplete(false, errorMessage.toString());
                                }
                            }
                        }
                    });
                }
                return;
            }
        }

        deleteAllQuizQuestions(quizId, true, new OperationCallback() {
            @Override
            public void onComplete(boolean isDeleteSuccessful, String deleteMessage) {
                if (!isDeleteSuccessful && !deleteMessage.contains("404")) {
                    listener.onSaveComplete(false, "Failed to delete existing questions: " + deleteMessage);
                    return;
                }

                remainingOperations.set(questions.size());

                if (questions.isEmpty()) {
                    listener.onSaveComplete(true, "All questions deleted successfully");
                    return;
                }

                for (Question question : questions) {
                    createQuestion(question, quizId, new OperationCallback() {
                        @Override
                        public void onComplete(boolean isSuccessful, String message) {
                            if (!isSuccessful) {
                                success.set(false);
                                errors.add("Failed to create question: " + message);
                            }

                            if (remainingOperations.decrementAndGet() == 0) {
                                if (success.get()) {
                                    listener.onSaveComplete(true, "All questions recreated successfully");
                                } else {
                                    StringBuilder errorMessage = new StringBuilder("Some questions failed to recreate: ");
                                    for (String error : errors) {
                                        errorMessage.append(error).append("; ");
                                    }
                                    listener.onSaveComplete(false, errorMessage.toString());
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void deleteAllQuizQuestions(Long quizId, boolean needDelete, final OperationCallback callback) {
        if (!needDelete) {
            callback.onComplete(true, "No questions to delete");
            return;
        }

        apiService.deleteAllQuizQuestions(quizId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "All questions deleted successfully");
                } else {
                    if (response.code() == 404) {
                        callback.onComplete(true, "No existing questions found");
                    } else {
                        callback.onComplete(false, "Failed to delete questions: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    public interface OperationCallback {
        void onComplete(boolean isSuccessful, String message);
    }

    public interface SaveOperationListener {
        void onSaveComplete(boolean isSuccessful, String message);
    }
}