package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

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

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }


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
