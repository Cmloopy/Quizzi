package com.cmloopy.quizzi.models.user;

import com.google.gson.annotations.SerializedName;

public class UserMusicEffect {

    @SerializedName("id")
    private int id;

    @SerializedName("music")
    private boolean music;

    @SerializedName("soundEffects")
    private boolean soundEffects;

    @SerializedName("animationEffects")
    private boolean animationEffects;

    @SerializedName("visualEffects")
    private boolean visualEffects;

    @SerializedName("userMusicEffect")
    private Object userMusicEffect; // hoặc một class cụ thể nếu bạn biết kiểu

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public boolean isMusic() { return music; }
    public void setMusic(boolean music) { this.music = music; }

    public boolean isSoundEffects() { return soundEffects; }
    public void setSoundEffects(boolean soundEffects) { this.soundEffects = soundEffects; }

    public boolean isAnimationEffects() { return animationEffects; }
    public void setAnimationEffects(boolean animationEffects) { this.animationEffects = animationEffects; }

    public boolean isVisualEffects() { return visualEffects; }
    public void setVisualEffects(boolean visualEffects) { this.visualEffects = visualEffects; }

    public Object getUserMusicEffect() { return userMusicEffect; }
    public void setUserMusicEffect(Object userMusicEffect) { this.userMusicEffect = userMusicEffect; }
}
