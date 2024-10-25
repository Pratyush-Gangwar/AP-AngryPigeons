package com.AngryPigeons.views;

import com.AngryPigeons.Main;
import com.badlogic.gdx.Gdx;
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
import com.AngryPigeons.Utils.Scene2DUtils;

public class LoseScreen implements Screen {

    private Main main;
    private Stage stage;
    private Table table;

    private Texture background;
    private float duration;

    public LoseScreen(Main main) {
        this.main = main;
        this.stage = new Stage();
        background = new Texture(Gdx.files.internal("textures/lose.jpg"));
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

        // automatically change after 10 seconds
        if (duration >= 10.0f) {
            main.changeScreen(Screens.HOMESCREEN);
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

        TextButton homeBtn = new TextButton("Home", Scene2DUtils.skin);
        TextButton restartBtn = new TextButton("Restart", Scene2DUtils.skin);

        table.add(homeBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        table.row();
        table.add(restartBtn).width(Scene2DUtils.buttonWidth).padBottom(20);

        homeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.HOMESCREEN);
            }
        });
    }
}
