package com.AngryPigeons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Bird {
    public Body body;
    public Sprite sprite;
    float w;
    float h;

    public Bird(Body body, float w, float h, int type){
        this.body = body;
        this.w = w;
        this.h = h;
        sprite = new Sprite();
        if (type == 1) {
            sprite = new Sprite(new Texture("Images/Red.png"));
        }
        if (type == 2) {
            sprite = new Sprite(new Texture("Images/Bomb.png"));
        }
        sprite.setSize(w, h);
        sprite.setOriginCenter();
    }

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public void render(SpriteBatch batch){
        sprite.draw(batch);
    }
}
