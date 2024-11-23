package com.AngryPigeons.domain;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class Killable extends Drawable {
    protected int hp;
    protected boolean dead;

    public Killable(Body body, float w, float h) {
        super(body, w, h);
    }

    public boolean isDead() {return dead;}

    public int getHp() {return hp;}

    public void damage(int dp){
        hp -= dp;;
    }
}
