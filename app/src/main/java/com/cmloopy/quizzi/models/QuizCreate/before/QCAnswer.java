package com.cmloopy.quizzi.models.QuizCreate.before;


public class QCAnswer {
    private String text;
    private boolean isCorrect;
    private int background;

    private String placeholder = "Add answer";

    public QCAnswer(String text, boolean isCorrect, int background) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.background = background;
    }

    public QCAnswer(String text, boolean isCorrect, int background, String placeholder) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.background = background;
        this.placeholder = placeholder;
    }

    public String getText() { return text; }
    public String getTextOrDefault() { return !text.isEmpty() ? text : placeholder; }
    public void setText(String text) { this.text = text; }
    public boolean isCorrect() { return isCorrect; }

    public String getPlaceholder() { return placeholder; }

    public void setCorrect(boolean correct) { isCorrect = correct; }
    public int getBackground() { return background; }
    public void setBackgroundColor(int background) { this.background = background; }

    public void setBackground(int background) { this.background = background; }

    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }

    public void setAnswer(QCAnswer answer) {
        this.text = answer.getText();
        this.isCorrect = answer.isCorrect();
        this.background = answer.getBackground();
    }
}
