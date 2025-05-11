package com.cmloopy.quizzi.data.api.QuestionCreate;

import android.content.Context;
import android.util.Log;

import com.cmloopy.quizzi.data.RetrofitClient;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class QuestionSaveService {
    private static final String TAG = "QuestionSaveService";
    private final Context context;
    private final QCQuestionChangeTracker changeTracker;

    public interface QuestionApiService {
        @Multipart
        @POST("questions")
        Call<Question> createQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("questionTypeId") RequestBody questionTypeId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @DELETE("questions/quiz/{quizId}")
        Call<Void> deleteAllQuizQuestions(@Path("quizId") Long quizId);

        @Multipart
        @PUT("questions/{id}")
        Call<Question> updateQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("questionTypeId") RequestBody questionTypeId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @DELETE("questions/{id}")
        Call<Void> deleteQuestion(@Path("id") Long id);

        @Multipart
        @POST("questions/true-false")
        Call<QuestionTrueFalse> createTrueFalseQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("correctAnswer") RequestBody correctAnswer,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @PUT("questions/true-false/{id}")
        Call<QuestionTrueFalse> updateTrueFalseQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("correctAnswer") RequestBody correctAnswer,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @POST("questions/choice")
        Call<QuestionChoice> createChoiceQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part List<MultipartBody.Part> choiceOptionsParts,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @PUT("questions/choice/{id}")
        Call<QuestionChoice> updateChoiceQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part List<MultipartBody.Part> choiceOptionsParts,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @POST("questions/slider")
        Call<QuestionSlider> createSliderQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("minValue") RequestBody minValue,
                @Part("maxValue") RequestBody maxValue,
                @Part("defaultValue") RequestBody defaultValue,
                @Part("correctAnswer") RequestBody correctAnswer,
                @Part("color") RequestBody color,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @PUT("questions/slider/{id}")
        Call<QuestionSlider> updateSliderQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("minValue") RequestBody minValue,
                @Part("maxValue") RequestBody maxValue,
                @Part("defaultValue") RequestBody defaultValue,
                @Part("correctAnswer") RequestBody correctAnswer,
                @Part("color") RequestBody color,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @POST("questions/puzzle")
        Call<QuestionPuzzle> createPuzzleQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part List<MultipartBody.Part> puzzlePieces,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @PUT("questions/puzzle/{id}")
        Call<QuestionPuzzle> updatePuzzleQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part List<MultipartBody.Part> puzzlePieces,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @POST("questions/text")
        Call<QuestionTypeText> createTextQuestion(
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("caseSensitive") RequestBody caseSensitive,
                @Part List<MultipartBody.Part> acceptedAnswersParts,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );

        @Multipart
        @PUT("questions/text/{id}")
        Call<QuestionTypeText> updateTextQuestion(
                @Path("id") Long id,
                @Part("quizId") RequestBody quizId,
                @Part("content") RequestBody content,
                @Part("point") RequestBody point,
                @Part("timeLimit") RequestBody timeLimit,
                @Part("description") RequestBody description,
                @Part("caseSensitive") RequestBody caseSensitive,
                @Part List<MultipartBody.Part> acceptedAnswersParts,
                @Part MultipartBody.Part imageFile,
                @Part MultipartBody.Part audioFile
        );
    }

    private QuestionApiService apiService;

    public QuestionSaveService(Context context) {
        this.context = context;
        this.changeTracker = new QCQuestionChangeTracker();
        this.apiService = RetrofitClient.getRetrofit().create(QuestionApiService.class);
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.createQuestion(
                quizIdBody, questionTypeIdBody, contentBody, pointBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.updateQuestion(
                question.getId(), quizIdBody, questionTypeIdBody, contentBody, pointBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCorrectAnswer()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.createTrueFalseQuestion(
                quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody correctAnswerBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCorrectAnswer()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.updateTrueFalseQuestion(
                question.getId(), quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                question.getId(), quizIdBody, contentBody, pointBody, timeLimitBody,
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

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.createSliderQuestion(
                quizIdBody, contentBody, pointBody, timeLimitBody,
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

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

        apiService.updateSliderQuestion(
                question.getId(), quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                question.getId(), quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody caseSensitiveBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCaseSensitive()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                quizIdBody, contentBody, pointBody, timeLimitBody,
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
        RequestBody pointBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getPoint()));
        RequestBody timeLimitBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.getTimeLimit()));
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"),
                question.getDescription() != null ? question.getDescription() : "");
        RequestBody caseSensitiveBody = RequestBody.create(MediaType.parse("text/plain"),
                String.valueOf(question.isCaseSensitive()));

        MultipartBody.Part imageFilePart = createFilePart("imageFile", question.getImageUri());
        MultipartBody.Part audioFilePart = createFilePart("audioFile", question.getAudioUri());

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
                question.getId(), quizIdBody, contentBody, pointBody, timeLimitBody,
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

    public void saveAllQuestionsWithFullReset(List<Question> questions, Long quizId, final SaveOperationListener listener) {
        final AtomicBoolean success = new AtomicBoolean(true);
        final List<String> errors = new ArrayList<>();
        final AtomicInteger remainingOperations = new AtomicInteger(0);

        deleteAllQuizQuestions(quizId, new OperationCallback() {
            @Override
            public void onComplete(boolean isDeleteSuccessful, String deleteMessage) {
                if (!isDeleteSuccessful) {
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

    private void deleteAllQuizQuestions(Long quizId, final OperationCallback callback) {
        apiService.deleteAllQuizQuestions(quizId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onComplete(true, "All questions deleted successfully");
                } else {
                    callback.onComplete(false, "Failed to delete questions: " + response.code());
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