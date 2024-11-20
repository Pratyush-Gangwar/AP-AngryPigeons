package com.AngryPigeons.logic;

import com.badlogic.gdx.physics.box2d.*;

public class LevelContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
//        System.out.println("Contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

         System.out.println(fixtureA.getBody().getUserData()+" has hit "+ fixtureB.getBody().getUserData());
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
