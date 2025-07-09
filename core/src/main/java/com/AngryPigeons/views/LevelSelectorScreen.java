package com.AngryPigeons.views;

import com.AngryPigeons.Main;
import com.AngryPigeons.storage.SavedLevel;
import com.AngryPigeons.storage.Storage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.AngryPigeons.Utils.Scene2DUtils;

import java.util.List;

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

public class LevelSelectorScreen implements Screen {

    private Main main;
    private Stage stage;
    private Table table;

    private static LevelSelectorScreen instance;

    public static LevelSelectorScreen getInstance(Main main) {
        if (LevelSelectorScreen.instance == null) {
            LevelSelectorScreen.instance = new LevelSelectorScreen(main);
        }

        return LevelSelectorScreen.instance;
    }

    private LevelSelectorScreen(Main main) {
        this.main = main;
        stage = new Stage( new ScreenViewport() );
        setupTable();
    }

    @Override
    public void show() {
        updateLevelStatus();
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        // stage.clear();
        System.out.println("Level selector hidden");
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    private void setupTable() {
        table = new Table();
        table.setFillParent(true);
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);
        stage.addActor(table);

        Scene2DUtils.setBackgroundOfTable(table);

        int numLevelButtons = main.getLevelInfoList().size();
        int maxCompleted = Storage.getInstance().getMaxCompletedLevel();
        int maxUnlockedIdx = Math.min(numLevelButtons - 1, maxCompleted + 1); // Next playable level is one after max completed

        for (int i = 0; i < numLevelButtons; i++) {
            TextButton levelButton = new TextButton("Level " + i, Scene2DUtils.skin);

            if (i > maxUnlockedIdx) {
                levelButton.setColor(Color.GRAY);
                levelButton.setTouchable(Touchable.disabled);
            }

            final int iCopy = i;
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    levelSelectHandler(iCopy);
                }
            });

            table.add(levelButton).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
            table.row();
        }

        TextButton back = new TextButton("Back", Scene2DUtils.skin);
        table.add(back).width(Scene2DUtils.buttonWidth).padTop(30);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.HOMESCREEN);
            }
        });
    }

    private void updateLevelStatus() {
        int maxCompleted = Storage.getInstance().getMaxCompletedLevel();

        int nextLevelIdx = maxCompleted + 1;
        int numLevelButtons = main.getLevelInfoList().size();

        if (nextLevelIdx >= numLevelButtons) return;

        Actor actor = table.getChild(nextLevelIdx);
        if (actor instanceof TextButton) {
            TextButton levelButton = (TextButton) actor;

            if (levelButton.isTouchable()) return;

            levelButton.setColor(Color.WHITE);
            levelButton.setTouchable(Touchable.enabled);
        }
    }



    private void levelSelectHandler(int levelIndex) {

        Dialog dialog = new Dialog("Play Level", Scene2DUtils.skin) {

            @Override
            protected void result(Object object) {

                // if you use this.hide() (without null), then the fade-out animation is played
                // however, this.hide() doesn't wait for the animation to complete and immediately returns
                // as soon as it returns, the screen is changed. the fade-out animation has not completed and is paused
                // when you win/lose a level and return to the level selector screen, the previously paused fade-out animation is resumed
                // when you pass null to this.hide(), it disables the fade-out mechanism and so the above problem doesn't occur

                int choice = (Integer) object;
                if (choice == 1) {
                    main.playNewLevel(levelIndex);
                }

                else if (choice == 2) {
                    main.loadLevel(levelIndex);
                }

                this.hide(null);
            }
        };

        // numeric values will be used later to execute different methods
        dialog.button("New", 1);

        boolean saveExists = Storage.getInstance().levelSaveExists(levelIndex);
        TextButton loadButton = new TextButton("Load saved", Scene2DUtils.skin);
        if (!saveExists) {
            loadButton.setColor(Color.GRAY);
            loadButton.setTouchable(Touchable.disabled);
        }
        dialog.button(loadButton, 2);

        dialog.show(stage);
    }
}
