package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

// ~~~ Which attributes should be serialized? ~~~
//- waiting: NO
//	- If the bird is not flying, waiting is set to true by the constructor
//
//	- If the bird is flying, waiting is false
//    - but you save the game only after the bird stops flying
//	- when it stops flying waiting is still false but this bird object is irrelevant because
//		- the bird pointer has moved forward
//		- this bird has been disposed
//
//- dp: NO
//	- set once in constructor and doesn't change during run-time
//
//- type: NO
//	- set once in constructor and doesn't change during run-time
//
//- powerUsed: NO
//	- If the bird is not flying, powerUsed is set to false by the constructor
//
//	- If the bird is flying, only then powerUsed can be changed
//	- but you save the game only after the bird stops flying
//	- when it stops flying, powerUsed may be true or false but this bird object is irrelevant because
//		- the bird pointer has moved forward
//		- this bird has been disposed

public class Bird extends Drawable {

    private boolean waiting; // true if it hasn't hopped on the slingshot yet

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
