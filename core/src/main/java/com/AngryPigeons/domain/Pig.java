package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Pig extends Drawable {

    private int hp;
    private boolean dead;

    public Pig(Body body, float w, float h, int type) {
        super(body, w, h);

        sprite = new Sprite(new Texture("Images/LargePig.png"));
        sprite.setSize(w, h);
        sprite.setOriginCenter();

        if (type == 1) {
            hp = 50;
        }
        if (type == 2) {
            hp = 100;
        }
        if (type == 3) {
            hp = 150;
        }

        body.setUserData(this);
        body.setLinearDamping(1.5f);
        body.setAngularDamping(1.5f);
    }

    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public void damage(int dp){
        hp -= dp;;
    }

    public void dispose(World world){
        world.destroyBody(this.body);
        sprite.getTexture().dispose();
        dead = true;
    }

    public boolean isDead() {return dead;}

    public int getHp() {return hp;}
}
