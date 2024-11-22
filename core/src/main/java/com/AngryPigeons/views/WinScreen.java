package com.AngryPigeons.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.AngryPigeons.Main;
import com.AngryPigeons.Utils.Scene2DUtils;

public class WinScreen implements Screen {

    private Main main;
    private Stage stage;
    private Table table;

    private Texture background;
    private float duration;

    public WinScreen(Main main) {
        this.main = main;
        this.stage = new Stage();
        background = new Texture(Gdx.files.internal("textures/win.jpg"));
        setupTable();
    }

    @Override
    public void show() {
        duration = 0;
    }

    @Override
    public void render(float v) {

//        InputProcessor ip = Gdx.input.getInputProcessor();
//        System.out.println("Home: " + ip.equals(main.getHomeScreen().getStage()));
//        System.out.println("Level Selector: "  + ip.equals(main.getLevelSelectorScreen().getStage()));
//        System.out.println("Win Screen: " + ip.equals(main.getWinScreen().getStage()));
//        System.out.println("Lose Screen: " + ip.equals(main.getLoseScreen().getStage()));
//        System.out.println("LevelRenderer: " + ip.equals(main.getLevelRenderer().getStage()));

        duration += v;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (duration >= 10.0f) {
            main.changeScreen(Screens.LEVELSELECTORSCREEN);
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public Stage getStage() {
        return stage;
    }

    private void setupTable() {
        table = new Table();
        table.setFillParent(true);
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);
        table.center().bottom();

        stage.addActor(table);

        Drawable drawable = new TextureRegionDrawable(background);
        table.setBackground(drawable);

        TextButton textButton = new TextButton("Levels", Scene2DUtils.skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.LEVELSELECTORSCREEN);
            }
        });

        table.add(textButton).width(Scene2DUtils.buttonWidth).padBottom(50);
    }
}
