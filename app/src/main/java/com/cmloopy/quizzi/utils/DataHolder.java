package com.cmloopy.quizzi.utils;

import com.cmloopy.quizzi.models.RecommendUser;
import java.util.ArrayList;
import java.util.List;

public class DataHolder {
    private static final DataHolder instance = new DataHolder();

    private List<RecommendUser> topAuthors = new ArrayList<>();

    private DataHolder() {
        // Constructor private để đảm bảo mẫu singleton
    }

    public static DataHolder getInstance() {
        return instance;
    }

    public void setTopAuthors(List<RecommendUser> authors) {
        this.topAuthors.clear();
        if (authors != null) {
            this.topAuthors.addAll(authors);
        }
    }

    public List<RecommendUser> getTopAuthors() {
        return new ArrayList<>(topAuthors); // Trả về bản sao để tránh sửa đổi
    }
}