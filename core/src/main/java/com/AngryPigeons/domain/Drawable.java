package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Drawable {
    protected Body body;
    protected Sprite sprite;
    float w;
    float h;

    public Drawable(Body body, float w, float h) {
        this.body = body;
        this.w = w;
        this.h = h;
    }

    public abstract void update();


    public void render(SpriteBatch batch){
        sprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
