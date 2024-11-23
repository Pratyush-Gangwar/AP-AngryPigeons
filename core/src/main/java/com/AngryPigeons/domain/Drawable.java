package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

// ~~~ Which attributes to serialize? ~~~
//- body: NO
//	- can't be serialized
//    - HAS DATA THAT CHANGES DURING RUN-TIME
//		- linearVelocity
//		- angular velocity
//		- body.x, body.y
//		- angle
//
//	- linearDamping is set once in constructor and doesn't change during run-time
//    - angularDamping is set once in constructor and doesn't change during run-time
//    - userData is set once in constructor and doesn't change during run-time
//
//    - BodyDef is set once in constructor and doesn't change during run-time
//    - dynamic/static body
//		- def.x, def.y MAY CHANGE
//
//	- FixtureDef is set once in constructor and doesn't change during run-time
//    - density, friction, restitution, shape
//
//- sprite: NO
//	- serialization not recommended by libgdx documentation
//	- HAS DATA THAT CHANGES DURING RUN-TIME BUT THESE DEPEND ON BODY WHICH IS ALREADY SERIALIZED
//		- x, y
//		- rotation
//
//- w, h: NO
//	- set once in constructor and doesn't change during run-time

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
