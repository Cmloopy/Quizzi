package com.cmloopy.quizzi.data.api.QuestionCreate.service;

import android.content.Context;
import android.util.Log;

import com.cmloopy.quizzi.data.RetrofitClient;
import com.cmloopy.quizzi.data.api.QuestionCreate.QuestionAPI;
import com.cmloopy.quizzi.models.QuestionCreate.BatchQuestionDTO;
import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
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
        this.apiService = RetrofitClient.getQuestionCreateApi();
    }

    public void initializeChangeTracker(List<QuestionCreate> questionCreates) {
        changeTracker.initialize(questionCreates);
    }

    public void registerQuestionChange(int position, QuestionCreate questionCreate) {
        changeTracker.questionModified(position, questionCreate);
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

    public void saveAllChanges(List<QuestionCreate> questionCreates, Long quizId, final SaveOperationListener listener) {
        if (!changeTracker.hasChanges()) {
            listener.onSaveComplete(true, "No changes to save");
            return;
        }

        final AtomicBoolean success = new AtomicBoolean(true);
        final AtomicInteger remainingOperations = new AtomicInteger(0);
        final List<String> errors = new ArrayList<>();

        for (QuestionCreate questionCreate : questionCreates) {
            QCQuestionChangeTracker.ChangeState state = changeTracker.getQuestionState(questionCreate.getPosition());
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
            QuestionCreate deletedQuestionCreate = findQuestionByPosition(questionCreates, position);
            if (deletedQuestionCreate != null && deletedQuestionCreate.getId() != null) {
                deleteQuestion(deletedQuestionCreate.getId(), new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to delete question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questionCreates);
                        }
                    }
                });
            } else {
                if (remainingOperations.decrementAndGet() == 0) {
                    finalizeSaveOperation(listener, success.get(), errors, questionCreates);
                }
            }
        }

        for (QuestionCreate questionCreate : questionCreates) {
            QCQuestionChangeTracker.ChangeState state = changeTracker.getQuestionState(questionCreate.getPosition());

            if (state == QCQuestionChangeTracker.ChangeState.NEW) {
                createQuestion(questionCreate, quizId, new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to create question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questionCreates);
                        }
                    }
                });
            } else if (state == QCQuestionChangeTracker.ChangeState.MODIFIED) {
                updateQuestion(questionCreate, quizId, new OperationCallback() {
                    @Override
                    public void onComplete(boolean isSuccessful, String message) {
                        if (!isSuccessful) {
                            success.set(false);
                            errors.add("Failed to update question: " + message);
                        }

                        if (remainingOperations.decrementAndGet() == 0) {
                            finalizeSaveOperation(listener, success.get(), errors, questionCreates);
                        }
                    }
                });
            }
        }
    }

    private void finalizeSaveOperation(SaveOperationListener listener, boolean success, List<String> errors, List<QuestionCreate> currentQuestionCreates) {
        if (success) {
            changeTracker.resetAfterSave(currentQuestionCreates);
            listener.onSaveComplete(true, "All changes saved successfully");
        } else {
            StringBuilder errorMessage = new StringBuilder("Save operation failed: ");
            for (String error : errors) {
                errorMessage.append(error).append("; ");
            }
            listener.onSaveComplete(false, errorMessage.toString());
        }
    }

    private QuestionCreate findQuestionByPosition(List<QuestionCreate> questionCreates, int position) {
        for (QuestionCreate q : questionCreates) {
            if (q.getPosition() == position) {
                return q;
            }
        }
        return null;
    }

    private void createQuestion(QuestionCreate questionCreate, Long quizId, final OperationCallback callback) {
        if (questionCreate instanceof QuestionCreateTrueFalse) {
            createTrueFalseQuestion((QuestionCreateTrueFalse) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateChoice) {
            createChoiceQuestion((QuestionCreateChoice) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateSlider) {
            createSliderQuestion((QuestionCreateSlider) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreatePuzzle) {
            createPuzzleQuestion((QuestionCreatePuzzle) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateTypeText) {
            createTextQuestion((QuestionCreateTypeText) questionCreate, quizId, callback);
        } else {
            // Generic question
            createGenericQuestion(questionCreate, quizId, callback);
        }
    }

    private void updateQuestion(QuestionCreate questionCreate, Long quizId, final OperationCallback callback) {
        if (questionCreate.getId() == null) {
            callback.onComplete(false, "Cannot update question without ID");
            return;
        }

        if (questionCreate instanceof QuestionCreateTrueFalse) {
            updateTrueFalseQuestion((QuestionCreateTrueFalse) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateChoice) {
            updateChoiceQuestion((QuestionCreateChoice) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateSlider) {
            updateSliderQuestion((QuestionCreateSlider) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreatePuzzle) {
            updatePuzzleQuestion((QuestionCreatePuzzle) questionCreate, quizId, callback);
        } else if (questionCreate instanceof QuestionCreateTypeText) {
            updateTextQuestion((QuestionCreateTypeText) questionCreate, quizId, callback);
        } else {
            // Generic question
            updateGenericQuestion(questionCreate, quizId, callback);
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


    private void createGenericQuestion(QuestionCreate questionCreate, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody questionTypeIdBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getQuestionType().getId().toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getContent() != null ? questionCreate.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(questionCreate.getPosition()));
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(questionCreate.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(questionCreate.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getDescription() != null ? questionCreate.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", questionCreate.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", questionCreate.getAudio());

        apiService.createQuestion(
                quizIdBody, questionTypeIdBody, contentBody, positionBody, pointBody,
                timeLimitBody, descriptionBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionCreate>() {
            @Override
            public void onResponse(Call<QuestionCreate> call, Response<QuestionCreate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionCreate.setId(response.body().getId());
                    callback.onComplete(true, "Question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreate> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateGenericQuestion(QuestionCreate questionCreate, Long quizId, final OperationCallback callback) {
        RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());
        RequestBody questionTypeIdBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getQuestionType().getId().toString());
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getContent() != null ? questionCreate.getContent() : "");
        RequestBody positionBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(questionCreate.getPosition()));

        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(questionCreate.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(questionCreate.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                questionCreate.getDescription() != null ? questionCreate.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", questionCreate.getImage());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", questionCreate.getAudio());

        apiService.updateQuestion(
                questionCreate.getId(), quizIdBody, questionTypeIdBody, contentBody, positionBody, pointBody,
                timeLimitBody, descriptionBody, imageFilePart, audioFilePart
        ).enqueue(new Callback<QuestionCreate>() {
            @Override
            public void onResponse(Call<QuestionCreate> call, Response<QuestionCreate> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreate> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createTrueFalseQuestion(QuestionCreateTrueFalse question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateTrueFalse>() {
            @Override
            public void onResponse(Call<QuestionCreateTrueFalse> call, Response<QuestionCreateTrueFalse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "True/False question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create True/False question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateTrueFalse> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateTrueFalseQuestion(QuestionCreateTrueFalse question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateTrueFalse>() {
            @Override
            public void onResponse(Call<QuestionCreateTrueFalse> call, Response<QuestionCreateTrueFalse> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "True/False question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update True/False question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateTrueFalse> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createChoiceQuestion(QuestionCreateChoice question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateChoice>() {
            @Override
            public void onResponse(Call<QuestionCreateChoice> call, Response<QuestionCreateChoice> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Choice question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Choice question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateChoice> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateChoiceQuestion(QuestionCreateChoice question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateChoice>() {
            @Override
            public void onResponse(Call<QuestionCreateChoice> call, Response<QuestionCreateChoice> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Choice question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Choice question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateChoice> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createSliderQuestion(QuestionCreateSlider question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateSlider>() {
            @Override
            public void onResponse(Call<QuestionCreateSlider> call, Response<QuestionCreateSlider> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Slider question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Slider question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateSlider> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateSliderQuestion(QuestionCreateSlider question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateSlider>() {
            @Override
            public void onResponse(Call<QuestionCreateSlider> call, Response<QuestionCreateSlider> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Slider question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Slider question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateSlider> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createPuzzleQuestion(QuestionCreatePuzzle question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreatePuzzle>() {
            @Override
            public void onResponse(Call<QuestionCreatePuzzle> call, Response<QuestionCreatePuzzle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Puzzle question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Puzzle question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreatePuzzle> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updatePuzzleQuestion(QuestionCreatePuzzle question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreatePuzzle>() {
            @Override
            public void onResponse(Call<QuestionCreatePuzzle> call, Response<QuestionCreatePuzzle> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Puzzle question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Puzzle question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreatePuzzle> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void createTextQuestion(QuestionCreateTypeText question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateTypeText>() {
            @Override
            public void onResponse(Call<QuestionCreateTypeText> call, Response<QuestionCreateTypeText> response) {
                if (response.isSuccessful() && response.body() != null) {
                    question.setId(response.body().getId());
                    callback.onComplete(true, "Text question created successfully");
                } else {
                    callback.onComplete(false, "Failed to create Text question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateTypeText> call, Throwable t) {
                callback.onComplete(false, "Network error: " + t.getMessage());
            }
        });
    }

    private void updateTextQuestion(QuestionCreateTypeText question, Long quizId, final OperationCallback callback) {
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
        ).enqueue(new Callback<QuestionCreateTypeText>() {
            @Override
            public void onResponse(Call<QuestionCreateTypeText> call, Response<QuestionCreateTypeText> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "Text question updated successfully");
                } else {
                    callback.onComplete(false, "Failed to update Text question: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<QuestionCreateTypeText> call, Throwable t) {
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

    public void saveBatchQuestionsWithFullReset(List<QuestionCreate> questionCreates, Long quizId, final SaveOperationListener listener) {
        if (questionCreates == null || questionCreates.isEmpty()) {
            listener.onSaveComplete(true, "No questions to save");
            return;
        }

        // First, delete all existing questions if needed
        boolean needDelete = false;
        for (QuestionCreate questionCreate : questionCreates) {
            if (questionCreate.getId() != null && questionCreate.getId() >= 1) {
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
                    createQuestionsBatch(questionCreates, quizId, listener);
                }
            });
        } else {
            createQuestionsBatch(questionCreates, quizId, listener);
        }
    }

    private void createQuestionsBatch(List<QuestionCreate> questionCreates, Long quizId, final SaveOperationListener listener) {
        try {
            RequestBody quizIdBody = RequestBody.create(MediaType.parse("text/plain"), quizId.toString());

            List<BatchQuestionDTO> batchDTOs = new ArrayList<>();
            Map<String, MultipartBody.Part> fileParts = new HashMap<>();

            for (QuestionCreate questionCreate : questionCreates) {
                BatchQuestionDTO batchDTO = convertToBatchDTO(questionCreate, fileParts);
                batchDTOs.add(batchDTO);
            }

            String questionsJson = new Gson().toJson(batchDTOs);
            RequestBody questionsJsonBody = RequestBody.create(
                    MediaType.parse("application/json"), questionsJson);

            // Create the list of file parts
            List<MultipartBody.Part> filePartsList = new ArrayList<>(fileParts.values());

            // Make the API call
            apiService.createQuestionsBatch(quizIdBody, questionsJsonBody, filePartsList)
                    .enqueue(new Callback<List<QuestionCreate>>() {
                        @Override
                        public void onResponse(Call<List<QuestionCreate>> call, Response<List<QuestionCreate>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                // Update the local questions with server-assigned IDs
                                List<QuestionCreate> createdQuestionCreates = response.body();
                                for (int i = 0; i < Math.min(questionCreates.size(), createdQuestionCreates.size()); i++) {
                                    questionCreates.get(i).setId(createdQuestionCreates.get(i).getId());
                                }
                                changeTracker.resetAfterSave(questionCreates);
                                listener.onSaveComplete(true, "All questions saved successfully");
                            } else {
                                listener.onSaveComplete(false, "Failed to save questions: " +
                                        (response.errorBody() != null ? response.errorBody().toString() : response.code()));
                            }
                        }

                        @Override
                        public void onFailure(Call<List<QuestionCreate>> call, Throwable t) {
                            listener.onSaveComplete(false, "Network error: " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            listener.onSaveComplete(false, "Error preparing batch request: " + e.getMessage());
        }
    }

    private BatchQuestionDTO convertToBatchDTO(QuestionCreate questionCreate, Map<String, MultipartBody.Part> fileParts) {
        BatchQuestionDTO dto = new BatchQuestionDTO();

        dto.setContent(questionCreate.getContent());
        dto.setDescription(questionCreate.getDescription());
        dto.setTimeLimit(questionCreate.getTimeLimit());
        dto.setPoint(questionCreate.getPoint());
        dto.setPosition(questionCreate.getPosition());

        if (questionCreate.getQuestionType() != null) {
            dto.setQuestionType(questionCreate.getQuestionType().getName());
        }

        if (questionCreate.getImage() != null && !questionCreate.getImage().isEmpty()) {
            String fileName = "image_" + System.currentTimeMillis() + "_" + questionCreate.getPosition() + ".jpg";
            dto.setImageFileName(fileName);

            MultipartBody.Part imagePart = createFilePart("files", questionCreate.getImage());
            if (imagePart != null) {
                fileParts.put(fileName, imagePart);
            }
        }

        if (questionCreate.getAudio() != null && !questionCreate.getAudio().isEmpty()) {
            String fileName = "audio_" + System.currentTimeMillis() + "_" + questionCreate.getPosition() + ".mp3";
            dto.setAudioFileName(fileName);

            MultipartBody.Part audioPart = createFilePart("files", questionCreate.getAudio());
            if (audioPart != null) {
                fileParts.put(fileName, audioPart);
            }
        }

        Map<String, Object> data = new HashMap<>();

        if (questionCreate instanceof QuestionCreateTrueFalse) {
            QuestionCreateTrueFalse tf = (QuestionCreateTrueFalse) questionCreate;
            data.put("correctAnswer", tf.isCorrectAnswer());
        } else if (questionCreate instanceof QuestionCreateChoice) {
            QuestionCreateChoice choice = (QuestionCreateChoice) questionCreate;
            data.put("choiceOptions", choice.getChoiceOptions());
        } else if (questionCreate instanceof QuestionCreateSlider) {
            QuestionCreateSlider slider = (QuestionCreateSlider) questionCreate;
            data.put("minValue", slider.getMinValue());
            data.put("maxValue", slider.getMaxValue());
            data.put("defaultValue", slider.getDefaultValue());
            data.put("correctAnswer", slider.getCorrectAnswer());
            data.put("color", slider.getColor());
        } else if (questionCreate instanceof QuestionCreatePuzzle) {
            QuestionCreatePuzzle puzzle = (QuestionCreatePuzzle) questionCreate;
            data.put("puzzlePieces", puzzle.getPuzzlePieces());
        } else if (questionCreate instanceof QuestionCreateTypeText) {
            QuestionCreateTypeText text = (QuestionCreateTypeText) questionCreate;
            data.put("caseSensitive", text.isCaseSensitive());
            data.put("acceptedAnswers", text.getAcceptedAnswers());
        }
//        else {
//        }
        data.put("questionTypeId", questionCreate.getQuestionType().getId());

        dto.setData(data);
        return dto;
    }

    public void saveAllQuestionsWithFullReset(List<QuestionCreate> questionCreates, Long quizId, final SaveOperationListener listener) {
//        if (questions == null || questions.isEmpty()) {
//            listener.onSaveComplete(true, "No questions to save");
//            return;
//        }

        final AtomicBoolean success = new AtomicBoolean(true);
        final List<String> errors = new ArrayList<>();
        final AtomicInteger remainingOperations = new AtomicInteger(0);
        if(!questionCreates.isEmpty()) {
            boolean needDelete = false;
            for(QuestionCreate questionCreate : questionCreates) {
                if (questionCreate.getId() != null && questionCreate.getId() >= 1)
                    needDelete = true;
            }

            if (!needDelete) {
                remainingOperations.set(questionCreates.size());
                for (QuestionCreate questionCreate : questionCreates) {
                    createQuestion(questionCreate, quizId, new OperationCallback() {
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

                remainingOperations.set(questionCreates.size());

                if (questionCreates.isEmpty()) {
                    listener.onSaveComplete(true, "All questions deleted successfully");
                    return;
                }

                for (QuestionCreate questionCreate : questionCreates) {
                    createQuestion(questionCreate, quizId, new OperationCallback() {
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