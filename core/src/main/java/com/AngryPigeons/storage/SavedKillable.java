package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;
import com.badlogic.gdx.physics.box2d.Body;

public class SavedKillable {
    private SavedBody savedBody;
    private int hp;
    private boolean dead;

    private transient Killable killable;

    // default constructor needed for GSON
    public SavedKillable() {
        this.savedBody = null;
        this.hp = 0;
        this.dead = false;
        this.killable = null;
    }

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

    public void load(Killable killable) {
        // since killable is transient, this attribute was set to null upon deserialization
        this.killable = killable;
        Body body = killable.getBody();

        killable.setHp(this.hp);
        killable.setDead(this.dead);
        savedBody.load(body);

        if (this.dead) {
            killable.dispose(body.getWorld());
        }

    }

}
