package com.AngryPigeons.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
//- musicDialog: NO
//	- set once in constructor and doesn't change during run-time
//
//- exitDialog: NO
//	- set once in constructor and doesn't change during run-time

public class HomeScreen implements Screen {

    private Main main; // orchestrator class
    private Stage stage;
    private Table table;
    private Dialog musicDialog;
    private Dialog exitDialog;

    private static HomeScreen instance;

    public static HomeScreen getInstance(Main main) {
        if (HomeScreen.instance == null) {
            HomeScreen.instance = new HomeScreen(main);
        }

        return HomeScreen.instance;
    }

    private HomeScreen(Main main) {
        System.out.println("Constructed home");

        this.main = main;

        musicDialog = null;
        exitDialog = null;

        stage = new Stage( new ScreenViewport() ); // stage controls all GUI elements
        setupTable();
    }

    @Override
    public void show() {}

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

        // before user clicks on music or exit button, both of these are null
        // resize with screen
        if (musicDialog != null) {
            musicDialog.setPosition((Gdx.graphics.getWidth() - musicDialog.getWidth())/2, (Gdx.graphics.getHeight() - musicDialog.getHeight())/2);
        }

        if (exitDialog != null) {
            exitDialog.setPosition((Gdx.graphics.getWidth() - exitDialog.getWidth())/2, (Gdx.graphics.getHeight() - exitDialog.getHeight())/2);
        }

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
    }

    @Override
    public void dispose() {
        System.out.println("Home disposed");
        stage.dispose();
    }

    private void setupTable() {
        table = new Table();
        table.setFillParent(true); // table same size as screen
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);

        Scene2DUtils.setBackgroundOfTable(table);
        stage.addActor(table); // add table to stage

        // expandY() makes sure that the Label and Button have space between them
        table.add(Scene2DUtils.makeLabel("Angry Pigeons", 150)).expandY();
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
                exitDialog = Scene2DUtils.makeExitWindow();
                exitDialog.show(stage); // adds dialog to stage, sets dialog as the input processor
            }
        });

        musicBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicDialog = Scene2DUtils.makeMusicControlWindow();
                musicDialog.show(stage);
            }
        });

    }

    public Stage getStage() {
        return stage;
    }

}
