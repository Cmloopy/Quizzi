package com.cmloopy.quizzi.data.api.QuestionCreate.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();

        if (dateString.contains(".")) {
            int dotIndex = dateString.indexOf(".");
            int endIndex = dotIndex + 1;

            while (endIndex < dateString.length() && Character.isDigit(dateString.charAt(endIndex))) {
                endIndex++;
            }

            String nanos = dateString.substring(dotIndex + 1, endIndex);
            String millis = nanos.length() >= 3 ? nanos.substring(0, 3) : (nanos + "000").substring(0, 3);

            String afterMillis = endIndex < dateString.length() ? dateString.substring(endIndex) : "";
            dateString = dateString.substring(0, dotIndex + 1) + millis + afterMillis;
        }

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf.parse(dateString);
            } catch (ParseException e) {
            }
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.parse(dateString);
        } catch (ParseException e) {
            throw new JsonParseException("Failed to parse Date: " + dateString, e);
        }
    }
}