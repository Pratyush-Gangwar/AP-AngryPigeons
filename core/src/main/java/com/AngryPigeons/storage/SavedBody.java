package com.AngryPigeons.storage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SavedBody {
    private float angle;
    private float angularVelocity;
    private Vector2 linearVelocity;
    private Vector2 position;

    private transient Body body;

    // default constructor needed for GSON
    public SavedBody() {
        this.angle = 0;
        this.angularVelocity = 0;
        this.linearVelocity = null;
        this.position = null;
    }

    public SavedBody(Body body) {
        this.body = body;
        sync();
    }

    public void sync() {
        this.angle = body.getAngle();
        this.angularVelocity = body.getAngularVelocity();
        this.linearVelocity = body.getLinearVelocity();
        this.position = body.getPosition();
    }

    public void load(Body body) {
        // since body is transient, this attribute was set to null upon deserialization
        this.body = body;

        body.setAngularVelocity(angularVelocity);
        body.setLinearVelocity(linearVelocity);
        body.setTransform(position, angle);
    }
}
