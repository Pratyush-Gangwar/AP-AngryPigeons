package com.AngryPigeons;

import com.badlogic.gdx.physics.box2d.Body;

public class Pig {
    public Body body;
    public float og_x;
    public float og_y;
    public float hp;

    public Pig(Body body, float og_x, float og_y){
        this.body = body;
        this.og_x = og_x;
        this.og_y = og_y;
    }
}
