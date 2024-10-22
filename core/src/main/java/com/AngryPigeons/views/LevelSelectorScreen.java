package com.AngryPigeons.views;

import com.AngryPigeons.LevelScreen;
import com.AngryPigeons.Main2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.AngryPigeons.Main2;
import com.AngryPigeons.Utils.Scene2DUtils;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectorScreen implements Screen {

    private Main2 main;
    private Stage stage;
    private Table table;

    private boolean wasHidden;
    private int lastCompleted;

    public LevelSelectorScreen(Main2 main) {
        this.main = main;
        wasHidden = false;
        lastCompleted = -1;

        stage = new Stage( new ScreenViewport() );

        table = new Table();
        table.setFillParent(true);
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);
        stage.addActor(table);

        Scene2DUtils.setBackgroundOfTable(table);
    }

    private void updateLevelStatus() {
        int toComplete = lastCompleted + 1;
        int numLevelButtons = main.getLevelScreenList().size() - 1; // last button is back button

        if (toComplete < numLevelButtons) {
            System.out.println("enabled");
            Actor actor = table.getChild(toComplete);
            TextButton levelButton = (TextButton) actor;
            levelButton.setTouchable(Touchable.enabled);
        }
    }

    @Override
    public void show() {

        if (wasHidden) {
            wasHidden = false;
            updateLevelStatus();
            return;
        }

        for(int i = 0; i < main.getLevelScreenList().size(); i++) {

            final int iCopy = i;

            LevelScreen levelScreen = main.getLevelScreenList().get(i);
            TextButton levelButton = new TextButton("Level " + (i + 1), Scene2DUtils.skin);


            if (!levelScreen.isComplete() && i != lastCompleted + 1) {
                levelButton.setColor(Color.GRAY); // change color
                levelButton.setTouchable(Touchable.disabled);
            }

            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    main.changeLevel(iCopy);
                }
            });

            // padBottom to ensure space between buttons
            table.add(levelButton).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
            table.row();
        }

        // padTop(30) so that Back button is always 30 pixels below the last level button
        TextButton back = new TextButton("Back", Scene2DUtils.skin);
        table.add(back).width(Scene2DUtils.buttonWidth).padTop(30);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.HOMESCREEN);
            }
        });
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
        wasHidden = true;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public void incrementLastCompleted() {
        lastCompleted++;
    }
}
