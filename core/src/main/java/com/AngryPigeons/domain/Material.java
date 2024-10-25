package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Material extends Drawable {

    public Material(Body body, float w, float h, int type){
        super(body, w, h);

        sprite = new Sprite();

        if (type == 1) {
            sprite = new Sprite(new Texture("Images/Ice2.png"),0 ,0 , (int)(w), (int)(h));
        }
        if (type == 2) {
            sprite = new Sprite(new Texture("Images/Wood.jpg"),0 ,0 , (int)(w), (int)(h));
        }
        if (type == 3) {
            sprite = new Sprite(new Texture("Images/1.jpg"),20 ,20 , (int)(w), (int)(h));
        }

        sprite.setSize(w, h);
        sprite.setOriginCenter();
    }

    @Override
    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }
}
