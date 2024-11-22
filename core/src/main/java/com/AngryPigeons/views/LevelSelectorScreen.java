package com.AngryPigeons.views;

import com.AngryPigeons.Main;
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

public class LevelSelectorScreen implements Screen {

    private Main main;
    private Stage stage;
    private Table table;

    private boolean wasHidden;
//    private int lastCompleted;

    public LevelSelectorScreen(Main main) {
        this.main = main;
        wasHidden = false;
//        lastCompleted = -1;

        stage = new Stage( new ScreenViewport() );

        table = new Table();
        table.setFillParent(true);
        table.setDebug(Scene2DUtils.scene2DDebugEnabled);
        stage.addActor(table);

        Scene2DUtils.setBackgroundOfTable(table);
    }

    private void updateLevelStatus() {
//        int toComplete = lastCompleted + 1;
        LevelScreen lastLevelScreen = main.getLevelScreenList().getLast();
        boolean isLastLevelComplete = lastLevelScreen.isComplete();

        System.out.println(isLastLevelComplete);

        if (!isLastLevelComplete) {
            return;
        }

        // all these buttons have already been rendered. we just need to re-enable them
        // int numLevelButtons = main.getLevelScreenList().size();
        int numLevelButtons = main.getLevelInfoList().size();
        int nextLevelIdx = main.getLevelScreenList().size();

        System.out.println(numLevelButtons + " " + nextLevelIdx);

        if (nextLevelIdx >= numLevelButtons) {
            return;
        }

//        System.out.println(nextLevelIdx);

//        if (nextLevelIdx < numLevelButtons) {
            Actor actor = table.getChild(nextLevelIdx);
            TextButton levelButton = (TextButton) actor;
            levelButton.setColor(new Color(37f, 150f, 190f, 1f));
            levelButton.setTouchable(Touchable.enabled);
//        }
    }

    @Override
    public void show() {

        if (wasHidden) {
            wasHidden = false;
            updateLevelStatus();
            return;
        }

        int numLevelButtons = main.getLevelInfoList().size();
        int numLevelScreens = main.getLevelScreenList().size();

        for(int i = 0; i < numLevelButtons /*main.getLevelScreenList().size()*/; i++) {

            TextButton levelButton = new TextButton("Level " + (i + 1), Scene2DUtils.skin);


//            LevelRenderer levelRenderer = main.getLevelRendererList().get(i);
//            LevelScreen levelScreen = levelRenderer.getLevelScreen();
//            LevelScreen levelScreen = main.getLevelScreenList().get(i);

            if (i != 0 && i >= numLevelScreens) {
                levelButton.setColor(Color.GRAY); // change color
                levelButton.setTouchable(Touchable.disabled);
            }

//            if (!levelScreen.isComplete()  && i != lastCompleted + 1) {
//                levelButton.setColor(Color.GRAY); // change color
//                levelButton.setTouchable(Touchable.disabled);
//            }

            final int iCopy = i; // lambda functions can only access local variables if they are final
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    levelSelectHandler(iCopy);
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

//    public void incrementLastCompleted() {
//        lastCompleted++;
//    }

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
                    main.newLevel(levelIndex);
                }


                this.hide(null);
//                main.changeLevel(levelIndex);
            }
        };

        // numeric values will be used later to execute different methods
        dialog.button("New", 1);
        dialog.button("Load saved", 2);

        dialog.show(stage);
    }
}
