package com.AngryPigeons.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Material extends Drawable {

    private int hp;
    private boolean dead;
    public Material(Body body, float w, float h, int type){
        super(body, w, h);

        sprite = new Sprite();

        if (type == 1) {
            hp = 50;
            sprite = new Sprite(new Texture("Images/Ice2.png"),0 ,0 , (int)(w), (int)(h));

        }
        if (type == 2) {
            hp = 150;
            sprite = new Sprite(new Texture("Images/Wood.jpg"),0 ,0 , (int)(w), (int)(h));
        }
        if (type == 3) {
            hp = 200;
            sprite = new Sprite(new Texture("Images/1.jpg"),20 ,20 , (int)(w), (int)(h));
        }

        sprite.setSize(w, h);
        sprite.setOriginCenter();
        body.setUserData(this);
    }

    @Override
    public void update(){
        sprite.setPosition((body.getPosition().x*PPM)-w/2, (body.getPosition().y*PPM)-h/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    public void damage(int dp){
        hp -= dp;
    }

    public void dispose(World world){
        world.destroyBody(this.body);
        sprite.getTexture().dispose();
        dead = true;
    }

    public boolean isDead(){return dead;}

    public int getHp() {return hp;}
}
