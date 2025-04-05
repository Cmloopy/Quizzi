package com.cmloopy.quizzi.models.QuizCreate.after.Option;

import com.cmloopy.quizzi.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Option implements Serializable {
    private int position;
    private String id;
    private String text;
    private String imageUrl;
    private String audioUrl;
    private Date createdAt;
    private Date updatedAt;

    private int background;
    private String placeholder = "Add answer";
    public final static int[] answerBackgroundColor = {
            R.drawable.ui_qc_bg_elevation_blue,
            R.drawable.ui_qc_bg_elevation_red,
            R.drawable.ui_qc_bg_elevation_orange,
            R.drawable.ui_qc_bg_elevation_green
    };


    public Option() {
        this.id = "";
        this.text = "";
        this.imageUrl = "";
        this.audioUrl = "";
        this.background = 0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.background = answerBackgroundColor[position % 4];
    }

    public Option(int position, String id, String text, String imageUrl, String audioUrl) {
        this.position = position;
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.background = answerBackgroundColor[position % 4];
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Option(int position, String id, String text, String imageUrl, String audioUrl, String placeholder) {
        this.position = position;
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.background = answerBackgroundColor[position % 4];
        this.placeholder = placeholder;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public int[] getAnswerBackgroundColor() {
        return answerBackgroundColor;
    }

    public int getBackground() {
        return background;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPosition(int position) {
        this.position = position;
        this.background = answerBackgroundColor[position % 4];
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setBackground(int position) {
        this.background = answerBackgroundColor[position % 4];
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getTextOrDefault() {
        return !text.isEmpty() ? text : placeholder;
    }

    public void setOption(Option option) {
        this.id = option.getId();
        this.text = option.getText();
        this.imageUrl = option.getImageUrl();
        this.audioUrl = option.getAudioUrl();
        this.background = option.getBackground();
        this.placeholder = option.getPlaceholder();
    }

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_CHOICE = 1;
    public static final int TYPE_PUZZLE = 2;
    public static final int TYPE_TYPE = 3;

    public static <T extends Option> List<T> initializeDefaultOptions(int optionType, int count) {
        List<T> options = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Option option;
            switch (optionType) {
                case TYPE_CHOICE:
                    option = new ChoiceOption(i, "", "", "", "", false);
                    break;
                case TYPE_PUZZLE:
                    option = new PuzzleOption(i, "", "", "", "", i);
                    break;
                case TYPE_TYPE:
                    option = new TypeTextOption(i, "", "", "", "");
                    break;
                default:
                    option = new Option(i, "", "", "", "");
                    break;
            }
            if(optionType == TYPE_CHOICE && i == 0) ((ChoiceOption) option).setCorrect(true);
            options.add((T) option);
        }

        return options;
    }

    /**
     * Initialize a list of default base options (non-specialized)
     * @return List of initialized base options
     */
    public static List<Option> initializeDefaultOptions() {
        return initializeDefaultOptions(TYPE_DEFAULT, 4);
    }
}