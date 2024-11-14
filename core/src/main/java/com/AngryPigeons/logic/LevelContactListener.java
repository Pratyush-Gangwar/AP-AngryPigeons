package com.AngryPigeons.logic;

import com.badlogic.gdx.physics.box2d.*;

public class LevelContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
//        System.out.println("Contact");
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        // System.out.println(fa.getBody().getUserData()+" has hit "+ fb.getBody().getUserData());
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
