package com.AngryPigeons.logic;

import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.domain.Bird;
import com.AngryPigeons.domain.SlingShot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.List;

public class BirdManager {

    private final World world;
    private final SlingShot slingShot;
    private final List<Integer> birdTypes;
    private final MapLayer birdLayer;

    private Bird currentBird;
    private int birdPointer;

    private Vector3 currentBirdPos;

    private float distance;
    private boolean ssPulled;
    private final Vector2 ssPosition;

    public BirdManager(World world, SlingShot slingShot, List<Integer> birdTypes, MapLayer birdLayer) {
        this.world = world;
        this.slingShot = slingShot;
        this.birdTypes = birdTypes;
        this.birdLayer = birdLayer;

        this.birdPointer = -1;
        this.ssPulled = false;
        this.distance = 0;
        this.currentBirdPos = new Vector3();
        this.ssPosition = slingShot.getBody().getPosition();
    }

    public void initialize() {
        if (birdPointer == -1) {
            birdPointer = 0;
        }
        spawnBird();
    }

    // We delete the bird if its velocity is less than a certain magnitude
    // At this magnitude, the bird has almost stopped moving
    // But all birds apart from the current bird have a velocity of 0.
    // So, we need a boolean (isWaiting) to differentiate between the one flying bird and the others birds which haven't been launched
    public void update() {
        slingShot.update();

        if (currentBird == null) return;
        currentBird.update();

        if (!currentBird.isWaiting() &&
            (currentBird.getBody().getLinearVelocity().len() <= 0.4f || currentBird.getBody().getPosition().y < 0)
        ) {
            world.destroyBody(currentBird.getBody());
            birdPointer++;
            if (birdPointer < birdTypes.size()) {
                spawnBird();
            } else {
                currentBird = null;
            }
        }
    }

    public void spawnBird() {
        currentBird = TiledMapUtil.parseBird(world, birdLayer.getObjects(), birdTypes.get(birdPointer));
    }

    public void touchDown(int screenX, int screenY) {
        if (currentBird == null) return;

        if (!currentBird.isWaiting()) {
            currentBird.power();
            return;
        }

        float bird_x = screenX / Constants.PPM;
        float bird_y = (Gdx.graphics.getHeight() - screenY) / Constants.PPM;

        distance = SlingShotUtil.calculateEuclideanDistance(bird_x, bird_y, ssPosition.x, ssPosition.y);
        float angle = SlingShotUtil.calculateAngle(bird_x, bird_y, ssPosition.x, ssPosition.y);
        distance = Math.min(distance, Constants.SS_RADIUS);

        currentBirdPos = new Vector3(
            (float) (ssPosition.x + distance * -Math.cos(angle)),
            (float) (ssPosition.y + distance * -Math.sin(angle)),
            angle
        );
    }

    public void touchDragged(int screenX, int screenY) {
        if (!currentBird.isWaiting()) return;

        float bird_x = screenX / Constants.PPM;
        float bird_y = (Gdx.graphics.getHeight() - screenY) / Constants.PPM;

        distance = SlingShotUtil.calculateEuclideanDistance(bird_x, bird_y, ssPosition.x, ssPosition.y);
        float angle = SlingShotUtil.calculateAngle(bird_x, bird_y, ssPosition.x, ssPosition.y);
        distance = Math.min(distance, Constants.SS_RADIUS);

        currentBirdPos = new Vector3(
            (float) (ssPosition.x + distance * -Math.cos(angle)),
            (float) (ssPosition.y + distance * -Math.sin(angle)),
            angle
        );
    }

    public void touchUp() {
        if (currentBird == null || !currentBird.isWaiting()) return;

        ssPulled = false;
        Vector2 velocity = new Vector2(
            (float) (distance * Constants.MAX_VELOCITY * Math.cos(currentBirdPos.z)),
            (float) (distance * Constants.MAX_VELOCITY * Math.sin(currentBirdPos.z))
        );
        currentBird.getBody().setLinearVelocity(velocity);
        currentBird.setWaiting(false);
    }

    public boolean isAllBirdsUsed() {
        return birdPointer >= birdTypes.size();
    }

    public Bird getCurrentBird() {
        return currentBird;
    }

    public boolean isSsPulled() {
        return ssPulled;
    }

    public void setSsPulled(boolean ssPulled) {
        this.ssPulled = ssPulled;
    }

    public Vector3 getCurrentBirdPos() {
        return currentBirdPos;
    }

    public float getDistance() {
        return distance;
    }

    public int getBirdPointer() {
        return birdPointer;
    }

    public void setBirdPointer(int birdPointer) {
        this.birdPointer = birdPointer;
    }

    public SlingShot getSlingShot() {
        return slingShot;
    }


}
