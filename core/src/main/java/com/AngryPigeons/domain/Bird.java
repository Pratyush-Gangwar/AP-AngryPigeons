package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Bird extends Drawable {

    private boolean waiting;

    private int dp;
    private int type;

    private boolean powerUsed;

    public Bird(Body body, float w, float h, int type){
        super(body, w, h);

        this.type = type;
        waiting = true;

        sprite = new Sprite();

        if (type == 1) {
            sprite = new Sprite(new Texture("Images/Red.png"));
            dp = 25;
        }
        if (type == 2) {
            sprite = new Sprite(new Texture("Images/Chuck.png"));
            dp = 75;
        }
        if (type == 3) {
            sprite = new Sprite(new Texture("Images/Silver.png"));
            dp = 100;
        }
        sprite.setSize(w, h);
        sprite.setOriginCenter();

        body.setUserData(this);
        body.setLinearDamping(0.1f);
        body.setAngularDamping(0.1f);
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

    public void power(){
        if (type == 2 && !powerUsed){
            powerUsed = true;
            body.setLinearVelocity(body.getLinearVelocity().x*3, body.getLinearVelocity().y);
            dp = 100;
        }
        else if (type == 3 && !powerUsed){
            powerUsed = true;
            body.setLinearVelocity(0, -50);
            body.setTransform(body.getPosition(), -90);
            dp = 125;
        }
    }
}
