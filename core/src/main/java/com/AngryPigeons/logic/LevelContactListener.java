package com.AngryPigeons.logic;

import com.AngryPigeons.domain.Bird;
import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.badlogic.gdx.physics.box2d.*;

import static com.AngryPigeons.Utils.Constants.STD_DP;

// ~~~ Which attributes to serialize? ~~~
// - NO attributes at all

public class LevelContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        // Retrieve the fixtures and their associated bodies
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        // Material - Material collision
        if (bodyA.getUserData() instanceof Material && bodyB.getUserData() instanceof Material) {
            Material A = (Material) bodyA.getUserData();
            Material B = (Material) bodyB.getUserData();
            A.damage(STD_DP);
            B.damage(STD_DP);
        }
        // Pig - Pig collision
        if (bodyA.getUserData() instanceof Pig && bodyB.getUserData() instanceof Pig) {
            Pig A = (Pig) bodyA.getUserData();
            Pig B = (Pig) bodyB.getUserData();
            A.damage(STD_DP);
            B.damage(STD_DP);
        }
        // Material - Pig collision
        if (bodyA.getUserData() instanceof Material && bodyB.getUserData() instanceof Pig) {
            Material A = (Material) bodyA.getUserData();
            Pig B = (Pig) bodyB.getUserData();
            A.damage(STD_DP);
            B.damage(STD_DP);
        }
        // Pig - Material collision
        if (bodyA.getUserData() instanceof Pig && bodyB.getUserData() instanceof Material) {
            Pig A = (Pig) bodyA.getUserData();
            Material B = (Material) bodyB.getUserData();
            A.damage(STD_DP);
            B.damage(STD_DP);
        }
        // Material - Bird
        else if (bodyA.getUserData() instanceof Material && bodyB.getUserData() instanceof Bird) {
            Material A = (Material) bodyA.getUserData();
            Bird B = (Bird) bodyB.getUserData();
            A.damage(B.getDp());
        } else if (bodyA.getUserData() instanceof Bird && bodyB.getUserData() instanceof Material) {
            Bird A = (Bird) bodyA.getUserData();
            Material B = (Material) bodyB.getUserData();
            B.damage(A.getDp());
        }
        // Material - Floor
        else if (bodyA.getUserData() instanceof Material && bodyB.getUserData() == "floor") {
            Material A = (Material) bodyA.getUserData();
            A.damage(STD_DP);
        } else if (bodyA.getUserData() == "floor" && bodyB.getUserData() instanceof Material) {
            Material B = (Material) bodyB.getUserData();
            B.damage(STD_DP);
        }
        // Pig - Bird
        else if (bodyA.getUserData() instanceof Pig && bodyB.getUserData() instanceof Bird) {
            Pig A = (Pig) bodyA.getUserData();
            Bird B = (Bird) bodyB.getUserData();
            A.damage(B.getDp());
        } else if (bodyA.getUserData() instanceof Bird && bodyB.getUserData() instanceof Pig) {
            Bird A = (Bird) bodyA.getUserData();
            Pig B = (Pig) bodyB.getUserData();
            B.damage(A.getDp());
        }
        // Pig - Floor
        else if (bodyA.getUserData() instanceof Pig && bodyB.getUserData() == "floor") {
            Pig A = (Pig) bodyA.getUserData();
            A.damage(STD_DP);
        } else if (bodyA.getUserData() == "floor" && bodyB.getUserData() instanceof Pig) {
            Pig B = (Pig) bodyB.getUserData();
            B.damage(STD_DP);
        }
//        System.out.println(fixtureA.getBody().getUserData() + " has hit " + fixtureB.getBody().getUserData());
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
