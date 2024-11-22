package com.AngryPigeons.views;

import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.domain.Bird;
import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.AngryPigeons.domain.SlingShot;
import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.logic.LevelContactListener;
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
    private boolean isComplete;
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

    ArrayList<Material> iceBlocks;
    ArrayList<Material> woodBlocks;
    ArrayList<Material> stoneBlocks;

    SlingShot slingShot;
    Vector2 ssPosition;
    Bird currentBird;
    Vector3 currentBirdPos;
    boolean ssPulled;
    float distance;

//    ArrayList<Bird> birds1, birds2, birds3;
    ArrayList<Integer> birds;
    int birdPointer;

    ArrayList<Pig> smallPigs;
    ArrayList<Pig> mediumPigs;
    ArrayList<Pig> largePigs;

    // ~~~ Scene2D integration start ~~~
    public LevelScreen(String tileMapPath, ArrayList<Integer> birds) {
        map = new TmxMapLoader().load(tileMapPath);
        this.birds = birds;
        isComplete = false;
    }

    public void setLevelRenderer(LevelRenderer levelRenderer) {
        this.levelRenderer = levelRenderer;
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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
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
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);

        world = new World(new Vector2(0, -9.8f), false);
        world.setContactListener(new LevelContactListener());

        b2dr = new Box2DDebugRenderer();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        background_tex = new Texture("Images/BG.png");
        cross_hair = new Texture("Images/images.png");

        tmr = new OrthogonalTiledMapRenderer(map);
        tmr.setView(camera);

        TiledMapUtil.parseFloor(world, map.getLayers().get("ground").getObjects(), true);
//        TiledMapUtil.parseBoundary(world, map.getLayers().get("boundary").getObjects(), true);

//        birds1 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons1").getObjects(), 1);
//        birds2 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons2").getObjects(), 2);
//        birds3 = TiledMapUtil.parseBird(world, map.getLayers().get("pigeons3").getObjects(), 3);
        birdPointer = 0;
        currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(), birds.get(birdPointer++));
        currentBirdPos = new Vector3();
        ssPulled = false;

        iceBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("ice-layer").getObjects(), 1);
        woodBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("wood-layer").getObjects(), 2);
        stoneBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("stone-layer").getObjects(), 3);

        largePigs = TiledMapUtil.parsePigs(world, map.getLayers().get("large-pigs").getObjects(), false, 3);
        mediumPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("medium-pigs").getObjects(), false, 2);
        smallPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("small-pigs").getObjects(), false, 1);

        slingShot = TiledMapUtil.parseSlingShot(world, map.getLayers().get("sling-shot").getObjects(), true);
        assert slingShot != null;
        ssPosition = slingShot.getBody().getPosition();
    }

//    @Override
    public void render(float delta){
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

        for (Material ice: iceBlocks){
            if (ice.isDead()){continue;}
            if ((ice.getHp()<=0)||(ice.getBody().getPosition().y<0)){
                ice.dispose(world);
                continue;
            }
            ice.update();
        }
        for (Material wood: woodBlocks){
            if (wood.isDead()){continue;}
            if ((wood.getHp()<=0)||(wood.getBody().getPosition().y<0)){
                wood.dispose(world);
                continue;
            }
            wood.update();
        }
        for (Material stone: stoneBlocks){
            if (stone.isDead()){continue;}
            if ((stone.getHp()<=0)||(stone.getBody().getPosition().y<0)) {
                stone.dispose(world);
                continue;
            }
            stone.update();
        }

        for (Pig largePig:largePigs){
            if (largePig.isDead()){continue;}
            if ((largePig.getHp()<=0)||(largePig.getBody().getPosition().y<0)){
                largePig.dispose(world);
                continue;
            }
            largePig.update();
        }
        for (Pig mediumPig:mediumPigs){
            if (mediumPig.isDead()){continue;}
            if ((mediumPig.getHp()<=0)||(mediumPig.getBody().getPosition().y<0)){
                mediumPig.dispose(world);
                continue;
            }
            mediumPig.update();
        }
        for (Pig smallPig:smallPigs){
            if (smallPig.isDead()){continue;}
            if ((smallPig.getHp()<=0)||(smallPig.getBody().getPosition().y<0)){
                smallPig.dispose(world);
                continue;
            }
            smallPig.update();
        }

//        System.out.println(currentBird.getBody().getPosition());
        if ((!currentBird.isWaiting() && currentBird.getBody().getLinearVelocity().len() <= 0.4f)||(currentBird.getBody().getPosition().y<0)) {
            currentBird.setStopped(true);
            if (birdPointer<birds.size()) {
                world.destroyBody(currentBird.getBody());
                currentBird = TiledMapUtil.parseBird(world, map.getLayers().get("bird").getObjects(), birds.get(birdPointer++));
            }
        }

//        batch.setProjectionMatrix(camera.projection);
//        batch.setTransformMatrix(camera.view);

        batch.begin();
//        batch.draw(cross_hair, cameraCenterX,cameraCenterY, crosshairSize, crosshairSize);//DEBUGGING
//        batch.draw(cross_hair, 240,360, crosshairSize, crosshairSize); //DEBUGGING

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        slingShot.render(batch);

//        for (Bird bird:birds1){bird.render(batch);}
//        for (Bird bird:birds2){bird.render(batch);}
//        for (Bird bird:birds3){bird.render(batch);}

        currentBird.render(batch);

        for (Material ice: iceBlocks){
            if (!ice.isDead()){
                ice.render(batch);
            }
        }
        for (Material wood: woodBlocks){
            if (!wood.isDead()){
                wood.render(batch);
            }
        }
        for (Material stone: stoneBlocks){
            if (!stone.isDead()){
                stone.render(batch);
            }
        }

        for (Pig largePig: largePigs){
            if (!largePig.isDead()){
                largePig.render(batch);
            }
        }
        for (Pig mediumPig: mediumPigs){
            if (!mediumPig.isDead()){
                mediumPig.render(batch);
            }
        }
        for (Pig smallPig: smallPigs){
            if (!smallPig.isDead()){
                smallPig.render(batch);
            }
        }

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
}
