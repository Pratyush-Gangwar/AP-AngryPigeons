package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class SlingShot extends Drawable {

    public SlingShot(Body body, float w, float h){
        super(body, w, h);
        sprite = new Sprite(new Texture("Images/Slingshot.png"));
        sprite.setSize(w, h);
        sprite.setOriginCenter();
    }

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }
}
