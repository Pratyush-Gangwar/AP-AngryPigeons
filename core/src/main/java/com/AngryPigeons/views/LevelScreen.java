package com.AngryPigeons.views;

import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.domain.*;
import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.exceptions.TileMapNotFoundException;
import com.AngryPigeons.logic.LevelContactListener;
import com.AngryPigeons.storage.Storage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.AngryPigeons.Utils.Constants.*;

// ~~~ Which attributes to serialize? ~~~
//- SCALE: NO
//	- final constant

//- camera: NO
//	- when UI restarts, it should be recreated
//
//- viewport: NO
//	- when UI restarts, it should be recreated
//
//- tmr: NO
//	- when UI restarts, it should be recreated
//- map: NO
//	- when UI restarts, it should be recreated
//
//- batch: NO
//	- when UI restarts, it should be recreated
//
//- shapeRenderer: NO
//	- when UI restarts, it should be recreated
//
//- background_tex: NO
//	- when UI restarts, it should be recreated
//
//- iceBlocks, woodBlocks, stoneBlocks: YES
//	- when UI restarts, the objects in the lists are same
//	- but the attributes are different
//
//- slingShot: NO
//	- created once in constructor and doesn't change during run-time
//
//- ssPosition: NO
//	- only changes when the sling shot is being pulled
//	- but you can't save the game in that state
//	- after slingshot is pulled, it resets
//
//- currentBird: NO
//	- depends on birdPointer which is already serialized
//
//- currentBirdPos: NO
//	- only changes during flight
//	- when it stops flying, this bird object is irrelevant because
//		- the bird pointer has moved forward
//		- this bird has been disposed
//
//- ssPulled: NO
//	- only changes when the sling shot is being pulled
//	- but you can't save the game in that state
//	- after slingshot is pulled, it resets
//
//- distance: NO
//	- only changes when the sling shot is being pulled
//	- but you can't save the game in that state
//	- after slingshot is pulled, it resets
//
//- birds: NO
//	- merely an integer list
//
//- birdPointer: YES
//	- need to know which bird is going to fly
//
//- smallPigs, mediumPigs, largePigs: YES
//	- when UI restarts, the objects in the lists are same
//	- but the attributes are different
//
//- win, lose, timeSinceEnd : NO
//	- when the win/lose condition is met, these are true
//	- but you can't save the game in this state
//	- so, they go back to being false when the level is reset
//
//- waitTime:
//	- final float

public class LevelScreen implements Screen{

    private final float SCALE = 1.0f;

    private OrthographicCamera camera;
    private Viewport viewport;

    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;

    Box2DDebugRenderer b2dr;
    private World world;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Texture background_tex;;
    private Texture cross_hair;

    private List<Material> materialList;

    private SlingShot slingShot;
    private Vector2 ssPosition;
    private boolean ssPulled;
    private float distance;

    private List<Integer> birds;
    private int birdPointer;
    private Bird currentBird;
    private Vector3 currentBirdPos;

    private List<Pig> pigList;

    private boolean win;
    private boolean lose;

    private float timeSinceEnd;
    private static float waitTime = 5.0f;

    private float timeStep;

    private Sprite ffSprite;
    private int ffAnimationCnt;
    private int ffAnimationFrame;

    // createLevel() and createRenderers() separate two aspects of the Level
    // createLevel() instantiates the Box2D physics related objects
    // createRenderers() instantiates the objects needed to render the physics objects made in createLevel()

    // ~~~ Scene2D integration start ~~~
    public LevelScreen(LevelInfo levelInfo) throws TileMapNotFoundException{
        File file = new File(levelInfo.getTileMapPath());
        if (!(file.exists() && file.isFile())){
            throw new TileMapNotFoundException("Tile Map Path " + levelInfo.getTileMapPath()+" does not exist");
        }
        this.map = new TmxMapLoader().load(levelInfo.getTileMapPath());
        this.birds = levelInfo.getBirds();
        this.timeSinceEnd = 0.0f;
        this.birdPointer = 0;

        createLevel();
        createRenderers();
    }

    private void createLevel() {
        world = new World(new Vector2(0, -9.8f), false);
        world.setContactListener(new LevelContactListener());

        this.materialList = new ArrayList<>();
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("ice-layer").getObjects(), 1));
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("wood-layer").getObjects(), 2));
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("stone-layer").getObjects(), 3));

        this.pigList = new ArrayList<>();
        pigList.addAll(TiledMapUtil.parsePigs(world, map.getLayers().get("large-pigs").getObjects(), false, 3));
        pigList.addAll( TiledMapUtil.parsePigs(world, map.getLayers().get("medium-pigs").getObjects(), false, 2));
        pigList.addAll(TiledMapUtil.parsePigs(world, map.getLayers().get("small-pigs").getObjects(), false, 1));

        currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(), birds.get(birdPointer++));
        currentBirdPos = new Vector3();

        TiledMapUtil.parseFloor(world, map.getLayers().get("ground").getObjects(), true);

        slingShot = TiledMapUtil.parseSlingShot(world, map.getLayers().get("sling-shot").getObjects(), true);
        assert slingShot != null;
        ssPosition = slingShot.getBody().getPosition();
        ssPulled = false;

        timeStep = 1/60f;

        ffAnimationCnt = 0;
        ffAnimationFrame = 0;
        ffSprite = new Sprite(new Texture("Images/FastForwardBlack.png"));
        ffSprite.setSize(100,100);
    }

    private void createRenderers() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH/SCALE, WORLD_HEIGHT/SCALE);
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);

        b2dr = new Box2DDebugRenderer();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        background_tex = new Texture("Images/BG.png");
        cross_hair = new Texture("Images/images.png");

        tmr = new OrthogonalTiledMapRenderer(map);
        tmr.setView(camera);
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

    // ~~~ Scene2D integration end ~~~

    @Override
    public void show(){}

    public void load() {
        // before deserialization and just after tile map reading, all materials and pigs have isDead set to false
        // after deserialization, materials and pigs which had died in the previous session now have isDead set to true
        // so, they won't be drawn by drawKillable() method which only checks the value of isDead()

        // but they will still remain in the Box2D world (as evidenced by the debugRenderer) because updatePig() and
        // updateMaterial() first checks if the object is dead. If so, it continues. If not, it checks if the HP is
        // less than zero, and then disposes the object while setting isDead to true

        // therefore, we must remove them from the Box2D world while deserializing
        // this is done in SavedKillable.load() method

        Storage.getInstance().loadLevelFromMemory(this);
        world.destroyBody(currentBird.getBody());
        currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(), birds.get(birdPointer-1));
//        System.out.println("Pointer "+birdPointer);
    }

    @Override
    public void render(float delta){
        update(delta);
        draw();
    }

    private void update(float delta) {
        LevelRenderer levelRenderer = LevelRenderer.getInstance();
        updatePhysics();

        //Rendering

//        float cameraCenterX = camera.position.x;
//        float cameraCenterY = camera.position.y;
//        float crosshairSize = 5f;

        slingShot.update();
        currentBird.update();
//        for (Bird bird:birds1){bird.update();}
//        for (Bird bird:birds2){bird.update();}
//        for (Bird bird:birds3){bird.update();}

        win = true;
        updateMaterials();
        updatePigs();

        if (win){
            timeSinceEnd += delta;

            if (timeSinceEnd >= waitTime) {
                levelRenderer.winLevel();
                return; // don't do anything more
            }
        }

        // We delete the bird if its velocity is less than a certain magnitude
        // At this magnitude, the bird has almost stopped moving
        // But all birds apart from the current bird have a velocity of 0.
        // So, we need a boolean (isWaiting) to differentiate between the one flying bird and the others birds which haven't been launched
        if ((!currentBird.isWaiting() && currentBird.getBody().getLinearVelocity().len() <= 0.4f)||(currentBird.getBody().getPosition().y<0)) {
            timeStep = 1/60f;
            if (birdPointer<birds.size()) {
                world.destroyBody(currentBird.getBody());
                currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(), birds.get(birdPointer++));
            }

            // birds exhausted
            else if (!win){
                lose = true;
                timeSinceEnd += delta;

                if (timeSinceEnd >= waitTime) {
                    levelRenderer.loseLevel();
                    return; // don't do anything more
                }
            }
        }

//        batch.setProjectionMatrix(camera.projection);
//        batch.setTransformMatrix(camera.view);

    }

    private void inputUpdate(){
        if (Gdx.input.isTouched(Input.Buttons.LEFT)){
            if (currentBird.isWaiting()) {
//            System.out.println("MOUSE PRESSED");
                ssPulled = true;
                currentBird.getBody().setTransform(currentBirdPos.x, currentBirdPos.y, currentBirdPos.z);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            if (!currentBird.isWaiting()){
                timeStep = 1/20f;
            }
        }
    }

    private void updatePhysics(){
        LevelRenderer levelRenderer = LevelRenderer.getInstance();

        // only step through physics simulation if not paused.
        if (!levelRenderer.isPaused()) {
            world.step(timeStep, 6, 2);
            inputUpdate();
        }

        // camera updated regardless of pause status
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void updateMaterials() {
        for(Material material : materialList) {
            if (material.isDead()) {
                continue;
            }

            if (material.getHp() <= 0 || material.getBody().getPosition().y < 0) {
                material.dispose(world);
                continue;
            }

            material.update();
        }
    }

    private void updatePigs() {
        for(Pig pig : pigList) {
            if (pig.isDead()) {
                continue;
            }

            if (pig.getHp() <= 0 || pig.getBody().getPosition().y < 0) {
                pig.dispose(world);
                continue;
            }

            win = false;
            pig.update();
        }
    }

    private void draw() {
        batch.begin();
//        batch.draw(cross_hair, cameraCenterX,cameraCenterY, crosshairSize, crosshairSize);//DEBUGGING
//        batch.draw(cross_hair, 240,360, crosshairSize, crosshairSize); //DEBUGGING

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        slingShot.render(batch);

//        for (Bird bird:birds1){bird.render(batch);}
//        for (Bird bird:birds2){bird.render(batch);}
//        for (Bird bird:birds3){bird.render(batch);}

        currentBird.render(batch);

        drawKillables(materialList);
        drawKillables(pigList);

//        tmr.setView(camera);
        tmr.render();

        batch.end();

        if (ssPulled) {
            SlingShotUtil.drawTrajectory(shapeRenderer, camera, currentBirdPos, distance, world.getGravity());
        }


//        b2dr.render(world, camera.combined.scl(PPM));

//        System.out.println(currentBird.getBody().getPosition());
//        System.out.println(ssPosition);

        if (timeStep == 1/20f){
            if (ffAnimationFrame == 0){
                ffSprite.setPosition(1105, 10);
            }
            else if (ffAnimationFrame == 1) {
                ffSprite.setPosition(1130, 10);
            } else{
                ffSprite.setPosition(1155, 10);
            }

            ffAnimationCnt = (ffAnimationCnt+1)%20;
            if (ffAnimationCnt == 0){
                ffAnimationFrame = (ffAnimationFrame+1)%3;
            }
            batch.begin();
            ffSprite.draw(batch);
            batch.end();
        }
    }

    private void drawKillables(List<? extends  Killable> killableList) {
        for(Killable killable : killableList) {
            if (!killable.isDead()) {
                killable.render(batch);
            }
        }
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
        tmr.dispose();
        map.dispose();
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public void touchDown(int screenX, int screenY, int pointer, int button) {
        if (currentBird.isWaiting()) {
//            System.out.println("Mouse down received at: " + screenX + ", " + screenY);
            float bird_x = screenX / PPM;
            float bird_y = (Gdx.graphics.getHeight() - screenY) / PPM;

            distance = SlingShotUtil.calculateEuclideanDistance(bird_x, bird_y, ssPosition.x, ssPosition.y);
            float angle = SlingShotUtil.calculateAngle(bird_x, bird_y, ssPosition.x, ssPosition.y);
            distance = Math.min(distance, Constants.SS_RADIUS);

            currentBirdPos = new Vector3((float) (ssPosition.x + distance * -Math.cos(angle)), (float) (ssPosition.y + distance * -Math.sin(angle)), angle);
        }
        else {
            currentBird.power();
        }
    }

    public void touchDragged(int screenX, int screenY, int pointer) {
        if (currentBird.isWaiting()) {
//            System.out.println("Mouse dragged received at: " + screenX + ", " + screenY);
            float bird_x = screenX / PPM;
            float bird_y = (Gdx.graphics.getHeight() - screenY) / PPM;

            distance = SlingShotUtil.calculateEuclideanDistance(bird_x, bird_y, ssPosition.x, ssPosition.y);
            float angle = SlingShotUtil.calculateAngle(bird_x, bird_y, ssPosition.x, ssPosition.y);
            distance = Math.min(distance, Constants.SS_RADIUS);

            currentBirdPos = new Vector3((float) (ssPosition.x + distance * -Math.cos(angle)), (float) (ssPosition.y + distance * -Math.sin(angle)), angle);
        }
    }

    public void touchUp(int screenX, int screenY, int pointer, int button) {
        if (currentBird.isWaiting()) {
//            System.out.println("Mouse up received at: " + screenX + ", " + screenY);
            ssPulled = false;
            Vector2 velocity = new Vector2((float) (distance * Constants.MAX_VELOCITY * Math.cos(currentBirdPos.z)), (float) (distance * Constants.MAX_VELOCITY * Math.sin(currentBirdPos.z)));
            currentBird.getBody().setLinearVelocity(velocity);

            currentBird.setWaiting(false);
        }
    }

    public boolean isSsPulled() {
        return ssPulled;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLose() {
        return lose;
    }

    public int getBirdPointer() {
        return birdPointer;
    }

    public void setBirdPointer(int birdPointer) {
        this.birdPointer = birdPointer;
    }

    public Bird getCurrentBird() {
        return currentBird;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public List<Pig> getPigList() {
        return pigList;
    }

}

