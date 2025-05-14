package com.cmloopy.quizzi.data.api.QuestionCreate.serializer;

import android.util.Log;

import com.cmloopy.quizzi.models.QuestionCreate.Option.ChoiceOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.cmloopy.quizzi.models.QuestionCreate.Option.TypeTextOption;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionType;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class QuestionCreateDeserializer implements JsonDeserializer<QuestionCreate> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    @Override
    public QuestionCreate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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

//        Date createdAt = new Date();
//        Date updatedAt = new Date();
        Date createdAt = parseDateSafely(jsonObject.get("createdAt"));
        Date updatedAt = parseDateSafely(jsonObject.get("updatedAt"));


        JsonObject questionTypeObj = jsonObject.getAsJsonObject("questionType");
        QuestionType questionType = context.deserialize(questionTypeObj, QuestionType.class);

        QuestionCreate result;

        Log.d("Current Question Deserializer: ", questionType.getName());
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
                result = new QuestionCreateChoice(position, id, content, imageUrl, audioUrl, point, timeLimit, description, choiceOptions);
                break;
            case "TRUE_FALSE":
                boolean _correctAnswer = jsonObject.get("correctAnswer").getAsBoolean();
                result = new QuestionCreateTrueFalse(position, id, content, imageUrl, audioUrl, point, timeLimit, description, _correctAnswer);
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
                result = new QuestionCreatePuzzle(position, id, content, imageUrl, audioUrl, point, timeLimit, description, puzzleOptions);
                break;

            case "SLIDER":
                int minValue = jsonObject.has("minValue") ? jsonObject.get("minValue").getAsInt() : 0;
                int maxValue = jsonObject.has("maxValue") ? jsonObject.get("maxValue").getAsInt() : 100;
                int defaultValue = jsonObject.has("defaultValue") ? jsonObject.get("defaultValue").getAsInt() : 50;
                int correctAnswer = jsonObject.has("correctAnswer") ? jsonObject.get("correctAnswer").getAsInt() : 50;
                String color = jsonObject.has("color") ? jsonObject.get("color").getAsString() : "Default";
                result = new QuestionCreateSlider(position, id, content, imageUrl, audioUrl, point, timeLimit, description,
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
                result = new QuestionCreateTypeText(position, id, content, imageUrl, audioUrl, point, timeLimit, description,
                        acceptedAnswers, caseSensitive);
                break;

            default:
                result = new QuestionCreate();
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

        result.setCreatedAt(createdAt);
        result.setUpdatedAt(updatedAt);
        result.setQuestionType(questionType);

        return result;
    }

    private Date parseDateSafely(JsonElement dateElem) {
        if (dateElem == null || dateElem.isJsonNull()) return new Date();

        String dateString = dateElem.getAsString();

        try {
            if (dateString.contains(".")) {
                int dotIndex = dateString.indexOf(".");
                int endIndex = dotIndex + 1;
                while (endIndex < dateString.length() && Character.isDigit(dateString.charAt(endIndex)) && endIndex < dotIndex + 10) {
                    endIndex++;
                }
                String nanos = dateString.substring(dotIndex + 1, endIndex);
                String millis = nanos.length() >= 3 ? nanos.substring(0, 3) : (nanos + "000").substring(0, 3);

                String after = (endIndex < dateString.length()) ? dateString.substring(endIndex) : "";
                dateString = dateString.substring(0, dotIndex + 1) + millis + after;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(dateString);

        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}