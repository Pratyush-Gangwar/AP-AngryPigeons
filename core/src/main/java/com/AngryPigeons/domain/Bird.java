package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Bird extends Drawable {

    private boolean waiting;

    private int dp;

    public Bird(Body body, float w, float h, int type){
        super(body, w, h);

        waiting = true;

        sprite = new Sprite();

        if (type == 1) {
            sprite = new Sprite(new Texture("Images/Red.png"));
            dp = 40;
        }
        if (type == 2) {
            sprite = new Sprite(new Texture("Images/Chuck.png"));
            dp = 80;
        }
        if (type == 3) {
            sprite = new Sprite(new Texture("Images/Bomb.png"));
            dp = 150;
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

    public int getDp(){return dp;}
}
