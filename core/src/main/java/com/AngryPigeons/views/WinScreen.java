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

// ~~~ Which attributes to serialize? ~~~
// - main: NO
//	- set once in Main and never changed during run-time
//
//- stage: NO
//	- set once in constructor and doesn't change during run-time
//
//- table: NO
//	- set once in constructor and doesn't change during run-time
//
//- background: NO
//	- set once in constructor and doesn't change during run-time
//
//- duration: NO
//	- this does change during run-time
//	- but you cannot save the game while this screen is being display
//	- when the screen is shown again, this is set to 0

public class WinScreen implements Screen {

    private Main main;
    private Stage stage;
    private Table table;

    private Texture background;
    private float duration;

    private static WinScreen instance;

    public static WinScreen getInstance(Main main) {
        if (WinScreen.instance == null) {
            WinScreen.instance = new WinScreen(main);
        }

        return WinScreen.instance;
    }

    private WinScreen(Main main) {
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
