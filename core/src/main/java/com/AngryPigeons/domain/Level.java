package com.AngryPigeons.domain;

public class Level {
    private boolean isComplete;

    public Level() {
        isComplete = false;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
}
