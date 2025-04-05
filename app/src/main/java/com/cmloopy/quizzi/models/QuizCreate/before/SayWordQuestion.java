package com.cmloopy.quizzi.models.QuizCreate.before;

public class SayWordQuestion extends Question {
    private static final String DEFAULT_WORD = "";
    private static final String DEFAULT_PRONUNCIATION = "";

    private String word;
    private String pronunciation;

    public SayWordQuestion() {
        super();
        this.word = DEFAULT_WORD;
        this.pronunciation = DEFAULT_PRONUNCIATION;
    }

    public SayWordQuestion(int position, String id, String title, String imageUrl, int time, int point,
                           String word, String pronunciation) {
        super(position, id, title, imageUrl, time, point);
        this.word = word != null ? word : DEFAULT_WORD;
        this.pronunciation = pronunciation != null ? pronunciation : DEFAULT_PRONUNCIATION;
    }

    @Override
    public int getType() {
        return TYPE_SAY_WORD;
    }

    public boolean isDefaultInstance() {
        return 
                //hasParentDefaultValues() &&
                DEFAULT_WORD.equals(word) &&
                DEFAULT_PRONUNCIATION.equals(pronunciation);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}
