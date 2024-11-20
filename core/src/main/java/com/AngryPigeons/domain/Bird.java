package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Bird extends Drawable {

    private boolean stopped;
    private boolean waiting;

    public Bird(Body body, float w, float h, int type){
        super(body, w, h);

        stopped = true;
        waiting = true;

        sprite = new Sprite();

        if (type == 1) {
            sprite = new Sprite(new Texture("Images/Red.png"));
        }
        if (type == 2) {
            sprite = new Sprite(new Texture("Images/Bomb.png"));
        }
        if (type == 3) {
            sprite = new Sprite(new Texture("Images/Chuck.png"));
        }
        sprite.setSize(w, h);
        sprite.setOriginCenter();
        body.setUserData(this);
    }

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
}
