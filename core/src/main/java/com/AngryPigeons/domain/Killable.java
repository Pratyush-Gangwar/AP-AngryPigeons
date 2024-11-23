package com.AngryPigeons.domain;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

// ~~~ Which attributes to serialize? ~~~
//- hp: YES
//	- changes during run-time
//
//- dead: YES
//	- changes during run-time

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

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void dispose(World world){
        world.destroyBody(this.body);
        sprite.getTexture().dispose();
        dead = true;
    }
}
