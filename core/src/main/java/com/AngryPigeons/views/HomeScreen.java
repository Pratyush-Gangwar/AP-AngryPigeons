package com.AngryPigeons.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.AngryPigeons.Main2;
import com.AngryPigeons.Utils.Scene2DUtils;


public class HomeScreen implements Screen {

    private Main2 main; // orchestrator class
    private Stage stage;
    private Table table;

    private boolean wasHidden; // to prevent multiple rendering

    public HomeScreen(Main2 main) {
        System.out.println("Constructed home");

        this.main = main;
        wasHidden = false;

        stage = new Stage( new ScreenViewport() ); // stage controls all GUI elements

        table = new Table();
        table.setFillParent(true); // table same size as screen
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);
        stage.addActor(table); // add table to stage

        Scene2DUtils.setBackgroundOfTable(table);
    }

    @Override
    public void show() {

        // if .setScreen(HomeScreen) was called and HomeScreen was previously hidden, then don't execute this
        // method again. Otherwise, elements will be rendered twice!
        if (wasHidden) {
            wasHidden = false;
            return;
        }

        // expandY() makes sure that the Label and Button have space between them
        table.add(Scene2DUtils.makeLabel("Angry Pigeons", 100)).expandY();
        table.row(); // new row

        // width(200) - 200 pixels wide
        TextButton selectLevelBtn = new TextButton("Select Level", Scene2DUtils.skin);
        table.add(selectLevelBtn).padBottom(Scene2DUtils.paddingSpace).width(Scene2DUtils.buttonWidth);

        table.row();

        TextButton musicBtn = new TextButton("Music", Scene2DUtils.skin);
        table.add(musicBtn).padBottom(Scene2DUtils.paddingSpace).width(Scene2DUtils.buttonWidth);

        table.row();

        // padBottom(100) - if not there, then this button sticks to the bottom of the window with no space at all
        TextButton exitBtn = new TextButton("Exit", Scene2DUtils.skin);
        table.add(exitBtn).padBottom(100).width(Scene2DUtils.buttonWidth);

        // add action to button
        selectLevelBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                main.changeScreen(Screens.LEVELSELECTORSCREEN);
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Scene2DUtils.makeExitWindow(stage);
            }
        });

        musicBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Scene2DUtils.makeMusicControlWindow(stage);
            }
        });

    }

    @Override
    public void render(float v) {
        // always clear screen before rendering
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw the stage - properties of the stage were set up in constructor and when .show() was initially called
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // ensure the stage resizes appropriate
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
        System.out.println("Home hide");
        wasHidden = true;
    }

    @Override
    public void dispose() {
        System.out.println("Home disposed");

        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

}
