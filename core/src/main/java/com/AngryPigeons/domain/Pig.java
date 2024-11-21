package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Pig extends Drawable {

    private int health;

    public Pig(Body body, float w, float h, int health) {
        super(body, w, h);

        sprite = new Sprite(new Texture("Images/LargePig.png"));
        sprite.setSize(w, h);
        sprite.setOriginCenter();

        this.health = health;
        body.setUserData(this);
        body.setLinearDamping(1.5f);
        body.setAngularDamping(1.5f);
    }

    public Pig(Body body, float w, float h) {
        this(body, w, h, 100);
    }

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

}
