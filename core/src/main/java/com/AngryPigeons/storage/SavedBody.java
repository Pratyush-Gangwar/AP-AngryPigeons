package com.AngryPigeons.storage;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SavedBody {
    private float angle;
    private float angularVelocity;
    private Vector2 linearVelocity;
    private Vector2 position;

    private transient Body body;

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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
