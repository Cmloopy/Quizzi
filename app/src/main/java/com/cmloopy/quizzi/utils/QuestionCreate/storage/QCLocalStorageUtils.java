package com.cmloopy.quizzi.utils.QuestionCreate.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QCLocalStorageUtils {

    private static final String PREF_NAME = "QuizziAppStorage";

    public static final String KEY_USER_DATA = "user_data";
    public static final String KEY_QUIZ_DATA = "quiz_data";
    public static final String KEY_AUTH_TOKEN = "auth_token";
    public static final String KEY_LAST_SYNC = "last_sync";

    public static boolean storeUserLoginSuccess(Context context, int userId, String email, String token) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("user_id", userId);
            editor.putString("user_email", email);
            editor.putString("auth_token", token);
            if (token != null && !token.isEmpty()) {
                editor.putString(KEY_AUTH_TOKEN, token);
            }

            editor.putBoolean("is_logged_in", true);

            return editor.commit();
        } catch (Exception e) {
            Log.e("LocalStorage", "Error storing user login data: " + e.getMessage());
            return false;
        }
    }

    public static boolean storeQuizCreationSuccess(Context context, Long quizId, String quizTitle, String quizData) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Set<String> existingQuizIds = prefs.getStringSet("created_quiz_ids", new HashSet<>());
            Set<String> updatedQuizIds = new HashSet<>(existingQuizIds);
            updatedQuizIds.add(String.valueOf(quizId));
            editor.putStringSet("created_quiz_ids", updatedQuizIds);

            editor.putString("quiz_" + quizId + "_title", quizTitle);
            editor.putLong("quiz_" + quizId + "_created_at", System.currentTimeMillis());

            if (quizData != null && !quizData.isEmpty()) {
                editor.putString("quiz_" + quizId + "_data", quizData);
            }

            editor.putLong("latest_quiz_id", quizId);

            return editor.commit();
        } catch (Exception e) {
            Log.e("LocalStorage", "Error storing quiz data: " + e.getMessage());
            return false;
        }
    }

//    public static Map<String, Object> getLatestQuizCreatedByUser(Context context) {
//        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        int quizId = prefs.getInt("latest_quiz_id", -1);
//
//        if (quizId == -1L) {
//            return null;
//        }
//
//        Map<String, Object> quiz = new HashMap<>();
//        quiz.put("quiz_id", quizId);
//        quiz.put("title", prefs.getString("quiz_" + quizId + "_title", "Untitled Quiz"));
//        quiz.put("data", prefs.getString("quiz_" + quizId + "_data", ""));
//        quiz.put("created_at", prefs.getLong("quiz_" + quizId + "_created_at", 0));
//
//        return quiz;
//    }

    public static int getLoggedInUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }

    public static Map<String, Object> getLoggedInUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Map<String, Object> userData = new HashMap<>();
        userData.put("user_id", prefs.getInt("user_id", -1));
        userData.put("user_email", prefs.getString("user_email", ""));
        userData.put("auth_token", prefs.getString(KEY_AUTH_TOKEN, ""));
        userData.put("is_logged_in", prefs.getBoolean("is_logged_in", false));
        return userData;
    }

    public static boolean isUserLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("is_logged_in", false);
    }

    public static Set<String> getUserCreatedQuizIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet("created_quiz_ids", new HashSet<>());
    }

    public static List<Map<String, Object>> getQuizzesCreatedByUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> quizIdStrings = prefs.getStringSet("created_quiz_ids", new HashSet<>());
        List<Map<String, Object>> quizzes = new ArrayList<>();

        for (String idStr : quizIdStrings) {
            try {
                Long id = Long.parseLong(idStr);
                String title = prefs.getString("quiz_" + id + "_title", "Untitled Quiz");
                Map<String, Object> quiz = new HashMap<>();
                quiz.put("quiz_id", id);
                quiz.put("title", title);
                quizzes.add(quiz);
            } catch (NumberFormatException e) {
                Log.w("LocalStorage", "Invalid quiz ID format: " + idStr);
            }
        }

        return quizzes;
    }

    public static boolean clearUserData(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.remove("user_id");
            editor.remove("user_email");
            editor.remove(KEY_AUTH_TOKEN);
            editor.putBoolean("is_logged_in", false);

            return editor.commit();
        } catch (Exception e) {
            Log.e("LocalStorage", "Error clearing user data: " + e.getMessage());
            return false;
        }
    }

    public static void clearAllData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // This removes all stored data
        editor.commit();
    }

    public static boolean storeCurrentUserId(Context context, int userId) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("current_userId", userId);
            editor.apply();
            return true;
        } catch (Exception e) {
            Log.e("QCLocalStorage", "Error storing current_userId: " + e.getMessage());
            return false;
        }
    }

    public static int getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("current_userId", -1); // -1 là giá trị mặc định nếu không tìm thấy
    }

}
