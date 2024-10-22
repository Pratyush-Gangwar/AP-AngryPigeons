package com.AngryPigeons;

import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TextureRenderUtil;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.AngryPigeons.views.LevelRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

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
    private Texture background_tex;;
    private Texture cross_hair;

    ArrayList<Material> iceBlocks;
    ArrayList<Material> woodBlocks;
    ArrayList<Material> stoneBlocks;
    SlingShot slingShot;

    //    ArrayList<Bird> birds;
    ArrayList<Pig> smallPigs;
    ArrayList<Pig> mediumPigs;
    ArrayList<Pig> largePigs;

    // ~~~ Scene2D integration start ~~~
    public LevelScreen(String tilemapPath) {
        map = new TmxMapLoader().load(tilemapPath);
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

        b2dr = new Box2DDebugRenderer();

        batch = new SpriteBatch();

        background_tex = new Texture("Images/Background.jpg");
        cross_hair = new Texture("Images/images.png");

        tmr = new OrthogonalTiledMapRenderer(map);
        tmr.setView(camera);

        TiledMapUtil.parseBoundary(world, map.getLayers().get("collision-layer").getObjects(), true);

        iceBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("ice-layer").getObjects(), 1);
        woodBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("wood-layer").getObjects(), 2);
        stoneBlocks = TiledMapUtil.parseMaterial(world, map.getLayers().get("stone-layer").getObjects(), 3);

        largePigs = TiledMapUtil.parsePigs(world, map.getLayers().get("large-pigs").getObjects(), false, 3);
        mediumPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("medium-pigs").getObjects(), false, 2);
        smallPigs = TiledMapUtil.parsePigs(world, map.getLayers().get("small-pigs").getObjects(), false, 1);

        slingShot = TiledMapUtil.parseSlingShot(world, map.getLayers().get("sling-shot").getObjects(), true);
    }

//    @Override
    public void render(float delta){
        update(Gdx.graphics.getDeltaTime());

        //Rendering

        float cameraCenterX = camera.position.x;
        float cameraCenterY = camera.position.y;
        float crosshairSize = 5f;

        slingShot.update();

        for (Material ice: iceBlocks){
            ice.update();
        }
        for (Material wood: woodBlocks){
            wood.update();
        }
        for (Material stone: stoneBlocks){
            stone.update();
        }

        for (Pig largePig:largePigs){
            largePig.update();
        }
        for (Pig pig:mediumPigs){
            pig.update();
        }
        for (Pig smallPig:smallPigs){
            smallPig.update();
        }

//        System.out.println(cameraCenterX+" "+cameraCenterY);

//        batch.setProjectionMatrix(camera.projection);
//        batch.setTransformMatrix(camera.view);

        batch.begin();
//        batch.draw(cross_hair, cameraCenterX,cameraCenterY, crosshairSize, crosshairSize);//DEBUGGING
//        batch.draw(cross_hair, 240,360, crosshairSize, crosshairSize); //DEBUGGING

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        slingShot.render(batch);

        for (Material ice: iceBlocks){
            ice.render(batch);
        }
        for (Material wood: woodBlocks){
            wood.render(batch);
        }
        for (Material stone: stoneBlocks){
            stone.render(batch);
        }
        for (Pig largePig: largePigs){
            largePig.render(batch);
        }
        for (Pig mediumPig: mediumPigs){
            mediumPig.render(batch);
        }
        for (Pig smallPig: smallPigs){
            smallPig.render(batch);
        }

        tmr.setView(camera);
        tmr.render();

        batch.end();
//        b2dr.render(world, camera.combined.scl(PPM));
    }

    public void inputUpdate(float delta){}

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
}
