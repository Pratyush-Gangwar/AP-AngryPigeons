package com.AngryPigeons.views;

import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.domain.*;
import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.logic.LevelContactListener;
import com.AngryPigeons.storage.Storage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

import java.util.ArrayList;
import java.util.List;

import static com.AngryPigeons.Utils.Constants.PPM;

public class LevelScreen implements Screen{

    private final float SCALE = 1.0f;

    // Scene2D integration start
    private LevelRenderer levelRenderer;
    private boolean isComplete; // has level been won at least once?
    private boolean wasShown; // show() creates the Box2D world
    // Scene2D integration end

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

    private ArrayList<Material> iceBlocks;
    private ArrayList<Material> woodBlocks;
    private ArrayList<Material> stoneBlocks;

    private SlingShot slingShot;
    private Vector2 ssPosition;
    private boolean ssPulled;
    private float distance;

//    ArrayList<Bird> birds1, birds2, birds3;
    private ArrayList<Integer> birds;
    private int birdPointer;
    private Bird currentBird;
    private Vector3 currentBirdPos;

    private ArrayList<Pig> smallPigs;
    private ArrayList<Pig> mediumPigs;
    private ArrayList<Pig> largePigs;

    private boolean win;
    private boolean lose;

    private float timeSinceEnd;
    private static float waitTime = 5.0f;

    // createLevel() and show() separate two aspects of the Level
    // createLevel() instantiates the Box2D physics related objects
    // show() instantiates the objects needed to render the physics objects made in createLevel()

    // ~~~ Scene2D integration start ~~~
    public LevelScreen(LevelInfo levelInfo) {
        this.map = new TmxMapLoader().load(levelInfo.getTileMapPath());
        this.birds = levelInfo.getBirds();
        this.isComplete = false;
        this.wasShown = false;
        this.timeSinceEnd = 0.0f;
        this.levelRenderer = LevelRenderer.getInstance();
        this.birdPointer = 0;

        createLevel();
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

    public void update(float delta){

        // only step through physics simulation if not paused.
        if (!levelRenderer.isPaused()) {
            world.step(1 / 60f, 6, 2);
            inputUpdate(delta);
        }

        // camera updated regardless of pause status
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
    // ~~~ Scene2D integration end ~~~

    @Override
    public void show(){
        this.wasShown = true;

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);

        b2dr = new Box2DDebugRenderer();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        background_tex = new Texture("Images/BG.png");
        cross_hair = new Texture("Images/images.png");

        tmr = new OrthogonalTiledMapRenderer(map);
        tmr.setView(camera);

//        TiledMapUtil.parseBoundary(world, map.getLayers().get("boundary").getObjects(), true);

//        birds1 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons1").getObjects(), 1);
//        birds2 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons2").getObjects(), 2);
//        birds3 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons3").getObjects(), 3);
    }

    public void createLevel() {
        world = new World(new Vector2(0, -9.8f), false);
        world.setContactListener(new LevelContactListener());

        iceBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("ice-layer").getObjects(), 1);
        woodBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("wood-layer").getObjects(), 2);
        stoneBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("stone-layer").getObjects(), 3);

        largePigs = TiledMapUtil.parsePigs(world, map.getLayers().get("large-pigs").getObjects(), false, 3);
        mediumPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("medium-pigs").getObjects(), false, 2);
        smallPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("small-pigs").getObjects(), false, 1);

        currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(),
            birds.get(birdPointer++));
        currentBirdPos = new Vector3();

        TiledMapUtil.parseFloor(world, map.getLayers().get("ground").getObjects(), true);

        slingShot = TiledMapUtil.parseSlingShot(world, map.getLayers().get("sling-shot").getObjects(), true);
        assert slingShot != null;
        ssPosition = slingShot.getBody().getPosition();
        ssPulled = false;
    }

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
        // ~~~ Updating positions ~~~

//        System.out.println(currentBird.getDp());
        update(Gdx.graphics.getDeltaTime());

        //Rendering

//        float cameraCenterX = camera.position.x;
//        float cameraCenterY = camera.position.y;
//        float crosshairSize = 5f;

        slingShot.update();
        currentBird.update();
//        for (Bird bird:birds1){bird.update();}
//        for (Bird bird:birds2){bird.update();}
//        for (Bird bird:birds3){bird.update();}

        updateMaterials(iceBlocks);
        updateMaterials(woodBlocks);
        updateMaterials(stoneBlocks);

        win = true;

        updatePigs(largePigs);
        updatePigs(mediumPigs);
        updatePigs(smallPigs);

        if (win){
            timeSinceEnd += delta;

            if (timeSinceEnd >= waitTime) {
                levelRenderer.winLevel();
                return; // don't do anything more
            }
        }

//        System.out.println(currentBird.getBody().getPosition());

        // We delete the bird if its velocity is less than a certain magnitude
        // At this magnitude, the bird has almost stopped moving
        // But all birds apart from the current bird have a velocity of 0.
        // So, we need a boolean (isWaiting) to differentiate between the one flying bird and the others birds which haven't been launched
        if ((!currentBird.isWaiting() && currentBird.getBody().getLinearVelocity().len() <= 0.4f)||(currentBird.getBody().getPosition().y<0)) {
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

        // ~~~ Drawing objects ~~~

        batch.begin();
//        batch.draw(cross_hair, cameraCenterX,cameraCenterY, crosshairSize, crosshairSize);//DEBUGGING
//        batch.draw(cross_hair, 240,360, crosshairSize, crosshairSize); //DEBUGGING

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        slingShot.render(batch);

//        for (Bird bird:birds1){bird.render(batch);}
//        for (Bird bird:birds2){bird.render(batch);}
//        for (Bird bird:birds3){bird.render(batch);}

        currentBird.render(batch);

        drawKillables(iceBlocks);
        drawKillables(woodBlocks);
        drawKillables(stoneBlocks);

        drawKillables(largePigs);
        drawKillables(mediumPigs);
        drawKillables(smallPigs);

//        tmr.setView(camera);
        tmr.render();

        batch.end();

        if (ssPulled) {
            SlingShotUtil.drawTrajectory(shapeRenderer, camera, currentBirdPos, distance, world.getGravity());
        }


        b2dr.render(world, camera.combined.scl(PPM));

//        System.out.println(currentBird.getBody().getPosition());
//        System.out.println(ssPosition);
    }

    private void updateMaterials(List<Material> materialList) {
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

    private void updatePigs(List<Pig> pigList) {
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

    private void drawKillables(List<? extends  Killable> killableList) {
        for(Killable killable : killableList) {
            if (!killable.isDead()) {
                killable.render(batch);
            }
        }
    }

    public void inputUpdate(float delta){
        if (Gdx.input.isTouched(Input.Buttons.LEFT)){
            if (currentBird.isWaiting()) {
//            System.out.println("MOUSE PRESSED");
                ssPulled = true;
                currentBird.getBody().setTransform(currentBirdPos.x, currentBirdPos.y, currentBirdPos.z);
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public boolean wasShown() {
        return wasShown;
    }

    public Bird getCurrentBird() {
        return currentBird;
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

    public ArrayList<Pig> getSmallPigs() {
        return smallPigs;
    }

    public ArrayList<Pig> getMediumPigs() {
        return mediumPigs;
    }

    public ArrayList<Pig> getLargePigs() {
        return largePigs;
    }

    public ArrayList<Material> getIceBlocks() {
        return iceBlocks;
    }

    public ArrayList<Material> getWoodBlocks() {
        return woodBlocks;
    }

    public ArrayList<Material> getStoneBlocks() {
        return stoneBlocks;
    }

    public void setBirdPointer(int birdPointer) {
        this.birdPointer = birdPointer;
    }

}

