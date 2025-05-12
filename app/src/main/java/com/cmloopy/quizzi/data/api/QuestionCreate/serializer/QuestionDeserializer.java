package com.cmloopy.quizzi.data.api.QuestionCreate.serializer;

import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionType;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionTypeText;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuestionDeserializer implements JsonDeserializer<Question> {
    private static final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Long id = jsonObject.get("id").getAsLong();
        int position = jsonObject.has("position") ? jsonObject.get("position").getAsInt() : 0;
        String content = jsonObject.get("content").getAsString();
        String imageUrl = jsonObject.has("image") && !jsonObject.get("image").isJsonNull() ?
                jsonObject.get("image").getAsString() : "";
        String audioUrl = jsonObject.has("audio") && !jsonObject.get("audio").isJsonNull() ?
                jsonObject.get("audio").getAsString() : "";
        int point = jsonObject.get("point").getAsInt();
        int timeLimit = jsonObject.get("timeLimit").getAsInt();
        String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : "";
        int quizId = jsonObject.get("quizId").getAsInt();

        Date createdAt = new Date();
        Date updatedAt = new Date();
        if (jsonObject.has("createdAt") && !jsonObject.get("createdAt").isJsonNull()) {
            try {
                String dateStr = jsonObject.get("createdAt").getAsString();
                LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
                createdAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
                System.err.println("Error parsing createdAt date: " + e.getMessage());
            }
        }

        if (jsonObject.has("updatedAt") && !jsonObject.get("updatedAt").isJsonNull()) {
            try {
                String dateStr = jsonObject.get("updatedAt").getAsString();
                LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
                updatedAt = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e) {
                System.err.println("Error parsing updatedAt date: " + e.getMessage());
            }
        }

        JsonObject questionTypeObj = jsonObject.getAsJsonObject("questionType");
        QuestionType questionType = context.deserialize(questionTypeObj, QuestionType.class);

        Question result;

        switch (questionType.getName()) {
            case "SINGLE_CHOICE":
            case "MULTI_CHOICE":
            case "AUDIO_SINGLE_CHOICE":
                List<ChoiceOption> choiceOptions = new ArrayList<>();
                JsonElement choiceOptionsElem = jsonObject.get("choiceOptions");
                if (choiceOptionsElem != null && !choiceOptionsElem.isJsonNull()) {
                    int optionPosition = 0;
                    for (JsonElement option : choiceOptionsElem.getAsJsonArray()) {
                        JsonObject optionObj = option.getAsJsonObject();

                        Long optionId = optionObj.get("id").getAsLong();
                        String optionText = optionObj.has("text") ? optionObj.get("text").getAsString() : "";
                        String optionImage = optionObj.has("image") && !optionObj.get("image").isJsonNull() ?
                                optionObj.get("image").getAsString() : "";
                        String optionAudio = optionObj.has("audio") && !optionObj.get("audio").isJsonNull() ?
                                optionObj.get("audio").getAsString() : "";
                        boolean isCorrect = optionObj.has("isCorrect") && optionObj.get("isCorrect").getAsBoolean();

                        ChoiceOption choiceOption = new ChoiceOption(
                                optionPosition++,
                                optionId,
                                optionText,
                                optionImage,
                                optionAudio,
                                isCorrect
                        );
                        choiceOptions.add(choiceOption);
                    }
                }
                result = new QuestionChoice(position, id, content, imageUrl, audioUrl, point, timeLimit, description, choiceOptions);
                break;
            case "TRUE_FALSE":
                boolean _correctAnswer = jsonObject.get("correctAnswer").getAsBoolean();
                result = new QuestionTrueFalse(position, id, content, imageUrl, audioUrl, point, timeLimit, description, _correctAnswer);
                break;
            case "PUZZLE":
                List<PuzzleOption> puzzleOptions = new ArrayList<>();
                JsonElement puzzleOptionsElem = jsonObject.get("puzzlePieces");
                if (puzzleOptionsElem != null && !puzzleOptionsElem.isJsonNull()) {
                    int optionPosition = 0;
                    for (JsonElement option : puzzleOptionsElem.getAsJsonArray()) {
                        JsonObject optionObj = option.getAsJsonObject();

                        Long optionId = optionObj.get("id").getAsLong();
                        String optionText = optionObj.has("text") ? optionObj.get("text").getAsString() : "";
                        String optionImage = optionObj.has("image") && !optionObj.get("image").isJsonNull() ?
                                optionObj.get("image").getAsString() : "";
                        String optionAudio = optionObj.has("audio") && !optionObj.get("audio").isJsonNull() ?
                                optionObj.get("audio").getAsString() : "";
                        int correctPosition = optionObj.has("correctPosition") ?
                                optionObj.get("correctPosition").getAsInt() : optionPosition;

                        PuzzleOption puzzleOption = new PuzzleOption(
                                optionPosition++,
                                optionId,
                                optionText,
                                optionImage,
                                optionAudio,
                                correctPosition
                        );
                        puzzleOptions.add(puzzleOption);
                    }
                }
                result = new QuestionPuzzle(position, id, content, imageUrl, audioUrl, point, timeLimit, description, puzzleOptions);
                break;

            case "SLIDER":
                int minValue = jsonObject.has("minValue") ? jsonObject.get("minValue").getAsInt() : 0;
                int maxValue = jsonObject.has("maxValue") ? jsonObject.get("maxValue").getAsInt() : 100;
                int defaultValue = jsonObject.has("defaultValue") ? jsonObject.get("defaultValue").getAsInt() : 50;
                int correctAnswer = jsonObject.has("correctAnswer") ? jsonObject.get("correctAnswer").getAsInt() : 50;
                String color = jsonObject.has("color") ? jsonObject.get("color").getAsString() : "Default";
                result = new QuestionSlider(position, id, content, imageUrl, audioUrl, point, timeLimit, description,
                        minValue, maxValue, defaultValue, correctAnswer, color);
                break;

            case "TEXT":
            case "SAY_WORD":
                List<TypeTextOption> acceptedAnswers = new ArrayList<>();
                JsonElement acceptedAnswersElem = jsonObject.get("acceptedAnswers");
                if (acceptedAnswersElem != null && !acceptedAnswersElem.isJsonNull()) {
                    int optionPosition = 0;
                    for (JsonElement answer : acceptedAnswersElem.getAsJsonArray()) {
                        JsonObject answerObj = answer.getAsJsonObject();

                        Long answerId = answerObj.get("id").getAsLong();
                        String answerText = answerObj.has("text") ? answerObj.get("text").getAsString() : "";
                        String answerImage = answerObj.has("image") && !answerObj.get("image").isJsonNull() ?
                                answerObj.get("image").getAsString() : "";
                        String answerAudio = answerObj.has("audio") && !answerObj.get("audio").isJsonNull() ?
                                answerObj.get("audio").getAsString() : "";

                        TypeTextOption textOption = new TypeTextOption(
                                optionPosition++,
                                answerId,
                                answerText,
                                answerImage,
                                answerAudio
                        );
                        acceptedAnswers.add(textOption);
                    }
                }
                boolean caseSensitive = jsonObject.has("caseSensitive") && jsonObject.get("caseSensitive").getAsBoolean();
                result = new QuestionTypeText(position, id, content, imageUrl, audioUrl, point, timeLimit, description,
                        acceptedAnswers, caseSensitive);
                break;

            default:
                result = new Question();
                result.setId(id);
                result.setQuizId(quizId);
                result.setPosition(position);
                result.setQuestionType(questionType);
                result.setContent(content);
                result.setImage(imageUrl);
                result.setAudio(audioUrl);
                result.setPoint(point);
                result.setTimeLimit(timeLimit);
                result.setDescription(description);
                break;
        }

        // Set created and updated dates
        result.setCreatedAt(createdAt);
        result.setUpdatedAt(updatedAt);
        result.setQuestionType(questionType);

        return result;
    }
}