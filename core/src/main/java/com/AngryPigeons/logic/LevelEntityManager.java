package com.AngryPigeons.logic;

import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class LevelEntityManager {
    private final World world;
    private final List<Material> materialList;
    private final List<Pig> pigList;

    public LevelEntityManager(World world, List<Material> materialList, List<Pig> pigList) {
        this.world = world;
        this.materialList = materialList;
        this.pigList = pigList;
    }

    public void updateMaterials() {
        for (Material m : materialList) {
            if (!m.isDead() && (m.getHp() <= 0 || m.getBody().getPosition().y < 0)) {
                m.dispose(world);
            } else {
                m.update();
            }
        }
    }

    public void updatePigs() {

        for (Pig p : pigList) {
            if (!p.isDead() && (p.getHp() <= 0 || p.getBody().getPosition().y < 0)) {
                p.dispose(world);
            } else {
                p.update();
            }
        }
    }

    public boolean getWin() {
        for (Pig pig : pigList) {
            if (!pig.isDead()) {
                return false;
            }
        }
        return true;
    }

    public void sleepBodies() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for(Body body : bodies) {
            body.setAwake(false);
        }
    }

    public void wakeBodies() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        for(Body body : bodies) {
            body.setAwake(true);
        }
    }

    public List<Material> getMaterials() { return materialList; }
    public List<Pig> getPigs() { return pigList; }
}
