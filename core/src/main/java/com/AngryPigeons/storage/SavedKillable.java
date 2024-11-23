package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;

public class SavedKillable {
    private SavedBody savedBody;
    private int hp;
    private boolean dead;

    private transient Killable killable;

    public SavedKillable(Killable killable) {
        this.killable = killable;
        sync();
    }

    public void sync() {

        if (this.savedBody == null) {
            this.savedBody = new SavedBody(killable.getBody());
        } else {
            this.savedBody.sync();
        }

        this.hp = killable.getHp();
        this.dead = killable.isDead();
    }

    public SavedBody getSavedBody() {
        return savedBody;
    }

    public void setSavedBody(SavedBody savedBody) {
        this.savedBody = savedBody;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}
