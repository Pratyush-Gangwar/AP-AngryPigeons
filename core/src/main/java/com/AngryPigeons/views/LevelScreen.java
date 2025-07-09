package com.AngryPigeons.views;

import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.domain.*;
import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.exceptions.TileMapNotFoundException;
import com.AngryPigeons.logic.BirdManager;
import com.AngryPigeons.logic.LevelContactListener;
import com.AngryPigeons.logic.LevelDrawer;
import com.AngryPigeons.logic.LevelEntityManager;
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

    private int score;
    private int levelID;

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

    private BirdManager birdManager;

    private LevelEntityManager entityManager;
    private LevelDrawer levelDrawer;

    private boolean win;
    private boolean lose;

    private float timeSinceLaunch;

    private float timeSinceEnd;
    private final float timeToWaitAfterWinLoseConditionIsMet = 5.0f;

    private float timeStep;

    private boolean isFastForward = false;
    private final float NORMAL_TIME_STEP = 1/60f;
    private final float FAST_TIME_STEP = 1/20f;

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
        this.levelID = levelInfo.getLevelID();
        this.timeSinceEnd = 0.0f;
        this.score = 0;
        this.timeSinceLaunch = 0.0f;

        createLevel(levelInfo);
        createRenderers();
    }

    private void createLevel(LevelInfo levelInfo) {
        world = new World(new Vector2(0, -9.8f), false);
        world.setContactListener(new LevelContactListener());

        List<Material> materialList = new ArrayList<>();
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("ice-layer").getObjects(), 1));
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("wood-layer").getObjects(), 2));
        materialList.addAll(TiledMapUtil.parseMaterial(world, map.getLayers().get("stone-layer").getObjects(), 3));

        List<Pig> pigList = new ArrayList<>();
        pigList.addAll(TiledMapUtil.parsePigs(world, map.getLayers().get("large-pigs").getObjects(), false, 3));
        pigList.addAll( TiledMapUtil.parsePigs(world, map.getLayers().get("medium-pigs").getObjects(), false, 2));
        pigList.addAll(TiledMapUtil.parsePigs(world, map.getLayers().get("small-pigs").getObjects(), false, 1));

        TiledMapUtil.parseFloor(world, map.getLayers().get("ground").getObjects(), true);

        SlingShot slingShot = TiledMapUtil.parseSlingShot(world, map.getLayers().get("sling-shot").getObjects(), true);
        assert slingShot != null;

        birdManager = new BirdManager(world, slingShot, levelInfo.getBirds(), map.getLayers().get("bird"));
        birdManager.initialize();

        timeStep = NORMAL_TIME_STEP;

        entityManager = new LevelEntityManager(world, materialList, pigList);
        levelDrawer = new LevelDrawer();
    }

    private void createRenderers() {
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

    // ~~~ Scene2D integration end ~~~

    @Override
    public void show(){}

    @Override
    public void render(float delta){
        timeSinceLaunch += delta;
        update(delta);
        draw();
    }

    private void update(float delta) {
        LevelRenderer levelRenderer = LevelRenderer.getInstance();
        updatePhysics();

        entityManager.updateMaterials();
        entityManager.updatePigs();

        win = entityManager.getWin();

        birdManager.update();
        if (birdManager.isAllBirdsUsed() && !win) {
            lose = true;
        }

        if (win || lose) {
            timeSinceEnd += delta;
            if (timeSinceEnd >= timeToWaitAfterWinLoseConditionIsMet) {
                if (win) levelRenderer.winLevel();
                else levelRenderer.loseLevel();
            }
        }

    }

    private void updatePhysics(){
        LevelRenderer levelRenderer = LevelRenderer.getInstance();

        // only step through physics simulation if not paused.
        if (!levelRenderer.isPaused()) {
            world.step(timeStep, 6, 2);
            handleInput();
        }

        // camera updated regardless of pause status
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            toggleFastForward();
        }

        Bird currentBird = birdManager.getCurrentBird();
        Vector3 currentBirdPos = birdManager.getCurrentBirdPos();

        if (currentBird == null) return;

        if (Gdx.input.isTouched(Input.Buttons.LEFT)) {
            if (currentBird.isWaiting()) {
                birdManager.setSsPulled(true);
                currentBird.getBody().setTransform(currentBirdPos.x, currentBirdPos.y, currentBirdPos.z);
            }
        }

    }

    private void draw() {
        batch.begin();

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        birdManager.getSlingShot().render(batch);
        if (!birdManager.isAllBirdsUsed()) birdManager.getCurrentBird().render(batch);

        levelDrawer.drawKillables(batch, entityManager.getMaterials());
        levelDrawer.drawKillables(batch, entityManager.getPigs());

        tmr.render();
        batch.end();

        levelDrawer.drawTrajectoryIfNeeded(shapeRenderer, camera, birdManager.getCurrentBirdPos(), birdManager.getDistance(), world.getGravity(), birdManager.isSsPulled());
        levelDrawer.drawFastForwardIfNeeded(batch, timeStep, FAST_TIME_STEP);
    }

    public void toggleFastForward() {
        isFastForward = !isFastForward;
        timeStep = isFastForward ? FAST_TIME_STEP : NORMAL_TIME_STEP;
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

    public void touchDown(int screenX, int screenY, int pointer, int button) { birdManager.touchDown(screenX, screenY); }
    public void touchDragged(int screenX, int screenY, int pointer) { birdManager.touchDragged(screenX, screenY); }
    public void touchUp(int screenX, int screenY, int pointer, int button) { birdManager.touchUp(); }

    public boolean isWin() {
        return win;
    }

    public boolean isLose() {
        return lose;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getTimeSinceLaunch() {
        return timeSinceLaunch;
    }

    public int getLevelID() {
        return levelID;
    }

    public BirdManager getBirdManager() {
        return birdManager;
    }

    public LevelEntityManager getEntityManager() {
        return entityManager;
    }
}

