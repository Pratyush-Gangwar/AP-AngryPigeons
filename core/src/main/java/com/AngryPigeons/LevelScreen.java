package com.AngryPigeons;

import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.TextureRenderUtil;
import com.AngryPigeons.Utils.TiledMapUtil;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import static com.AngryPigeons.Utils.Constants.PPM;
//import com.AngryPigeons.Utils.TexturesUtil;

public class LevelScreen implements Screen{

    private final float SCALE = 1.0f;
    private final Main main;

    private OrthographicCamera camera;
    private Viewport viewport;

    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;

    Box2DDebugRenderer b2dr;
    private World world;

    private SpriteBatch batch;
    private Texture background_tex, ice_tex, wood_tex, stone_tex;
    private Texture slingShot_tex;
    private Texture cross_hair;

//    ArrayList<Bird> birds;
    ArrayList<Material> iceBlocks;
    ArrayList<Material> woodBlocks;
    ArrayList<Material> stoneBlocks;
    ArrayList<Material> slingShot;

//    public LevelScreen(TiledMap map, ArrayList<Bird> birds){
    public LevelScreen(TiledMap map, Main main){
//        this.map = map;
        this.main = main;
//        this.birds = birds;
    }

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
//        ice_tex = new Texture("Images/Background.png");
        wood_tex = new Texture("Images/Wood.jpg");
//        stone_tex = new Texture("Images/Background.png");
        slingShot_tex = new Texture("Images/Slingshot.png");
        cross_hair = new Texture("Images/images.png");

        map = new TmxMapLoader().load("Maps/AP_TestLevelMap.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);
        tmr.setView(camera);
        TiledMapUtil.parseTiledObjectLayer(world, map.getLayers().get("collision-layer").getObjects(), true);
//        iceBlocks = TiledMapUtil.parseTiledObjectLayer(world, map.getLayers().get("ice-blocks").getObjects());
        woodBlocks = TiledMapUtil.parseTiledObjectLayer(world, map.getLayers().get("wood-layer").getObjects(), false);
//        stoneBlocks = TiledMapUtil.parseTiledObjectLayer(world, map.getLayers().get("stone-blocks").getObjects());
        slingShot = TiledMapUtil.parseTiledObjectLayer(world, map.getLayers().get("sling-shot").getObjects(), true);
    }

//    @Override
    public void render(float delta){
        update(Gdx.graphics.getDeltaTime());

        //Rendering
        Gdx.gl.glClearColor(0f,0f, 0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float cameraCenterX = camera.position.x;
        float cameraCenterY = camera.position.y;
        float crosshairSize = 5f;

        System.out.println(cameraCenterX+" "+cameraCenterY);

        batch.begin();
//        batch.draw(cross_hair, cameraCenterX,cameraCenterY, crosshairSize, crosshairSize);//DEBUGGING
//        batch.draw(cross_hair, 240,360, crosshairSize, crosshairSize); //DEBUGGING

        batch.draw(background_tex, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        for (Material ss: slingShot){
            float w = TextureRenderUtil.getBodyWidth(ss.body);
            float h = TextureRenderUtil.getBodyHeight(ss.body);
            batch.draw(slingShot_tex,ss.og_x, ss.og_y-h, w, h);
        }
//        for (Body ice: iceBlocks){
//            batch.draw(ice_tex, ice.getPosition().x*PPM - ((float) ice_tex.getWidth()/2), ice.getPosition().y*PPM - ((float) ice_tex.getHeight()/2));
//        }
        for (Material wood: woodBlocks){
            float w = TextureRenderUtil.getBodyWidth(wood.body);
            float h = TextureRenderUtil.getBodyHeight(wood.body);
            batch.draw(wood_tex,wood.og_x+(wood.body.getPosition().x*PPM), wood.og_y+(wood.body.getPosition().y*PPM)-h, w, h);
        }
//        for (Body stone: stoneBlocks){
//            batch.draw(stone_tex, stone.getPosition().x*PPM - ((float) stone_tex.getWidth()/2), stone.getPosition().y*PPM - ((float) stone_tex.getHeight()/2));
//        }

        tmr.setView(camera);
        tmr.render();

        batch.end();

//        b2dr.render(world, camera.combined.scl(PPM));

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    public void update(float delta){
        world.step(1/60f, 6, 2);
        inputUpdate(delta);
        camera.update();

//        tmr.setView(camera);//To ensure map moves with player
        batch.setProjectionMatrix(camera.combined);//To ensure texture moves with player
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
