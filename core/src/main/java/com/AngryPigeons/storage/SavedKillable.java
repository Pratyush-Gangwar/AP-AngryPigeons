package com.AngryPigeons.storage;

import com.AngryPigeons.domain.Killable;
import com.badlogic.gdx.physics.box2d.Body;

public class SavedKillable {
    private SavedBody savedBody;
    private int hp;
    private boolean dead;

    // default constructor needed for GSON
    public SavedKillable() {
        this.savedBody = null;
        this.hp = 0;
        this.dead = false;
    }

    public void save(Killable killable) {

        if (this.savedBody == null) {
            this.savedBody = new SavedBody();
        }

        this.savedBody.save(killable.getBody());

        this.hp = killable.getHp();
        this.dead = killable.isDead();
    }

    public void load(Killable killable) {
        Body body = killable.getBody();

        killable.setHp(this.hp);
        killable.setDead(this.dead);
        savedBody.load(body);

        if (this.dead) {
            killable.dispose(body.getWorld());
        }

    }

}
