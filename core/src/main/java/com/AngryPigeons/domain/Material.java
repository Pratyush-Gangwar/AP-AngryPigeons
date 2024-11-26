package com.AngryPigeons.domain;

import com.AngryPigeons.exceptions.DrawableNotFoundException;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import static com.AngryPigeons.Utils.Constants.PPM;

public class Material extends Killable {

    public Material(Body body, float w, float h, int type) throws DrawableNotFoundException{
        super(body, w, h);

        sprite = new Sprite();

        if (type == 1) {
            hp = 75;
            sprite = new Sprite(new Texture("Images/Ice2.png"), 0, 0, (int) (w), (int) (h));
        }
        else if (type == 2) {
            hp = 150;
            sprite = new Sprite(new Texture("Images/Wood.jpg"), 0, 0, (int) (w), (int) (h));
        }
        else if (type == 3) {
            hp = 200;
            sprite = new Sprite(new Texture("Images/1.jpg"), 20, 20, (int) (w), (int) (h));
        }
        else {
            throw new DrawableNotFoundException("Material number does not exist");
        }

        sprite.setSize(w, h);
        sprite.setOriginCenter();
        body.setUserData(this);
    }
}
