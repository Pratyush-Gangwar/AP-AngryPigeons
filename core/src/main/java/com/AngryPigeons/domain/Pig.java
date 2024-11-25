package com.AngryPigeons.domain;

import com.AngryPigeons.exceptions.DrawableNotFoundException;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Pig extends Killable {

    public Pig(Body body, float w, float h, int type) {
        super(body, w, h);

        sprite = new Sprite(new Texture("Images/LargePig.png"));
        sprite.setSize(w, h);
        sprite.setOriginCenter();

        try {
            if (type == 1) {
                hp = 50;
            }
            else if (type == 2) {
                hp = 125;
            }
            else if (type == 3) {
                hp = 200;
            }
            else{
                throw new DrawableNotFoundException("Pig number does not exist");
            }
        }
        catch (DrawableNotFoundException e){
            System.out.println(e.getMessage());
            return;
        }

        body.setUserData(this);
        body.setLinearDamping(1.5f);
        body.setAngularDamping(1.5f);
    }

}
