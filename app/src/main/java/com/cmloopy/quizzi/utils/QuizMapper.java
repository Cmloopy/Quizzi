package com.cmloopy.quizzi.utils;

import android.util.Log;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.Quiz;
import com.cmloopy.quizzi.models.Question;
import com.cmloopy.quizzi.models.quiz.QuizResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuizMapper {

    public static Quiz fromResponse(QuizResponse response) {
        Log.d("MAPPER_DEBUG", "Converting QuizResponse: " + response.getTitle());

        // Tạo thông tin thời gian tương đối
        String relativeTime = getRelativeTimeFromDate(response.getCreatedAt());

        // Truyền vào giá trị mặc định cho những thông tin không có trong response
        String plays = response.getScore() != null ? response.getScore() + " plays" : "0 plays";
        String author = "Unknown"; // API không cung cấp tên tác giả

        Quiz quiz = new Quiz(
                R.drawable.ic_launcher_background, // Hình ảnh mặc định
                response.getTitle() != null ? response.getTitle() : "Untitled Quiz",
                relativeTime,
                plays,
                author,
                R.drawable.ic_launcher_background, // Avatar mặc định
                response.getDescription() != null ? response.getDescription() : ""
        );

        // Quan trọng: Quiz hiện đã có danh sách questions từ constructor
        // nhưng chúng ta nên kiểm tra để chắc chắn

        // Log để debug
        Log.d("MAPPER_DEBUG", "Quiz created with title: " + quiz.getTitle());
        Log.d("MAPPER_DEBUG", "Questions count: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : "null"));

        return quiz;
    }

    public static List<Quiz> fromResponseList(List<QuizResponse> responses) {
        List<Quiz> quizzes = new ArrayList<>();

        for (QuizResponse response : responses) {
            quizzes.add(fromResponse(response));
        }

        return quizzes;
    }

    // Phương thức hỗ trợ để tạo chuỗi thời gian tương đối
    private static String getRelativeTimeFromDate(String dateString) {
        try {
            if (dateString == null) return "Unknown time";

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
            Date date = format.parse(dateString);
            Date now = new Date();

            // Tính khoảng cách thời gian
            long diff = now.getTime() - date.getTime();
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            long months = days / 30;
            long years = days / 365;

            if (years > 0) {
                return years + (years == 1 ? " year ago" : " years ago");
            } else if (months > 0) {
                return months + (months == 1 ? " month ago" : " months ago");
            } else if (days > 0) {
                return days + (days == 1 ? " day ago" : " days ago");
            } else {
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                if (hours > 0) {
                    return hours + (hours == 1 ? " hour ago" : " hours ago");
                } else {
                    return "Just now";
                }
            }
        } catch (ParseException e) {
            Log.e("MAPPER_DEBUG", "Error parsing date: " + dateString, e);
            return "Unknown time";
        }
    }
}