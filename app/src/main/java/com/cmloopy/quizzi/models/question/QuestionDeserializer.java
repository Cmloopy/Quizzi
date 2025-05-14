package com.cmloopy.quizzi.models.question;

import com.cmloopy.quizzi.models.question.choice.MultiChoiceQuestion;
import com.cmloopy.quizzi.models.question.choice.SingleChoiceQuestion;
import com.cmloopy.quizzi.models.question.puzzle.PuzzleQuestion;
import com.cmloopy.quizzi.models.question.slider.SliderQuestion;
import com.cmloopy.quizzi.models.question.text.TextQuestion;
import com.cmloopy.quizzi.models.question.truefalse.TrueFalseQuestion;
import com.google.gson.*;
import java.lang.reflect.Type;

public class QuestionDeserializer implements JsonDeserializer<Question> {
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();
        String typeName = jsonObj.getAsJsonObject("questionType").get("name").getAsString();

        switch (typeName) {
            case "PUZZLE":
                return context.deserialize(json, PuzzleQuestion.class);
            case "SLIDER":
                return context.deserialize(json, SliderQuestion.class);
            case "TRUE_FALSE":
                return context.deserialize(json, TrueFalseQuestion.class);
            case "MULTI_CHOICE":
                return context.deserialize(json, MultiChoiceQuestion.class);
            case "SINGLE_CHOICE":
                return context.deserialize(json, SingleChoiceQuestion.class);
            case "TEXT":
                return context.deserialize(json, TextQuestion.class);
            default:
                throw new JsonParseException("Unknown question type: " + typeName);
        }
    }
}

