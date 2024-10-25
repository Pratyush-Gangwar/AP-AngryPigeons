package com.AngryPigeons.views;

import com.AngryPigeons.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.AngryPigeons.Utils.Scene2DUtils;

public class LevelRenderer implements Screen {
    // Scene2D
    private Main main;
    private Stage stage;
    private Table mainTable;
    private Dialog musicDialog;
    private Dialog exitDialog;

    private Table pauseMenuTable;
    private boolean isPaused;
    private boolean wasHidden;

    private LevelScreen levelScreen;

    public LevelRenderer(Main main, LevelScreen levelScreen) {
        // Scene2D
        this.main = main;
        this.levelScreen = levelScreen;
        this.isPaused = false;
        this.wasHidden = false;

        musicDialog = null;
        exitDialog = null;

        stage = new Stage( new ScreenViewport() );

        setupPauseMenu();
        setupMainTable();

    }

    // Box2D
    @Override
    public void show(){
        if (wasHidden) {
            wasHidden = false;
            isPaused = false;
            return;
        }

        levelScreen.show();
    }

    @Override
    public void render(float v) {
        // Scene2D
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); // needed to render transparent backgrounds

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (isPaused) {
            stage.addActor(pauseMenuTable);
            levelScreen.sleepBodies(); // pause physics for all bodies
        } else {
            pauseMenuTable.remove();
        }

        // Box2D
        // must render game before pause menu
        // renderGame(v);
        levelScreen.render(v);

        // Scene2D
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Scene2D
        stage.getViewport().update(width, height, true);
        if (musicDialog != null) {
            musicDialog.setPosition((Gdx.graphics.getWidth() - musicDialog.getWidth())/2, (Gdx.graphics.getHeight() - musicDialog.getHeight())/2);
        }

        if (exitDialog != null) {
            exitDialog.setPosition((Gdx.graphics.getWidth() - exitDialog.getWidth())/2, (Gdx.graphics.getHeight() - exitDialog.getHeight())/2);
        }

        // Box2D
        levelScreen.resize(width, height);
    }

    // Scene2D
    private void setTransparentBackground() {
        Pixmap backgroundPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        backgroundPixmap.setColor(new Color(0, 0, 0, 0.5f));
        backgroundPixmap.fill();

        Texture backgroundTexture = new Texture(backgroundPixmap);
        TextureRegion backgroundTextureRegion = new TextureRegion(backgroundTexture);
        TextureRegionDrawable regionDrawable = new TextureRegionDrawable(backgroundTextureRegion);

        pauseMenuTable.setBackground(regionDrawable);
        backgroundPixmap.dispose();
    }

    // Scene2D
    private void setupPauseMenu() {
        pauseMenuTable = new Table();
        pauseMenuTable.setFillParent(true);
        pauseMenuTable.setDebug(Scene2DUtils.scene2DDebugEnabled);

        setTransparentBackground();

        Label label = Scene2DUtils.makeLabel("Paused", 70);
        pauseMenuTable.add(label).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton resumeBtn = new TextButton("Resume", Scene2DUtils.skin);
        pauseMenuTable.add(resumeBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton saveBtn = new TextButton("Save Game", Scene2DUtils.skin);
        pauseMenuTable.add(saveBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton musicBtn = new TextButton("Music", Scene2DUtils.skin);
        pauseMenuTable.add(musicBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton homeBtn = new TextButton("Home Screen", Scene2DUtils.skin);
        pauseMenuTable.add(homeBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton exitBtn = new TextButton("Exit", Scene2DUtils.skin);
        pauseMenuTable.add(exitBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton debugCompleteBtn = new TextButton("Complete Level", Scene2DUtils.skin);
        pauseMenuTable.add(debugCompleteBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        TextButton debugLoseBtn = new TextButton("Lose Level", Scene2DUtils.skin);
        pauseMenuTable.add(debugLoseBtn).width(Scene2DUtils.buttonWidth).padBottom(Scene2DUtils.paddingSpace);
        pauseMenuTable.row();

        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("resume clicked");
                isPaused = false;
                levelScreen.wakeBodies(); // resume physics for paused bodies
            }
        });

        homeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.HOMESCREEN);
            }
        });

        debugCompleteBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelScreen.setComplete(true);
                main.getLevelSelectorScreen().incrementLastCompleted();
                main.changeScreen(Screens.WINSCREEN);
            }
        });

        debugLoseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.changeScreen(Screens.LOSESCREEN);
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                exitDialog = Scene2DUtils.makeExitWindow();
                exitDialog.show(stage);
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

    // Scene2D
    private void setupMainTable() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(Scene2DUtils.scene2DDebugEnabled);
        mainTable.top().left();

        Texture texture = new Texture(Gdx.files.internal("textures/pause.png"));
        TextureRegion textureRegion = new TextureRegion(texture);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);

        ImageButton imageButton = new ImageButton(textureRegionDrawable);
        mainTable.add(imageButton).width(100).height(100);

        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isPaused = true;
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void dispose() {
        // Scene2D
        stage.dispose();
        levelScreen.dispose();
    }

    // Scene2D
    @Override
    public void pause() {

    }

    // Scene2D
    @Override
    public void resume() {

    }

    // Scene2D
    @Override
    public void hide() {
        isPaused = true;
        wasHidden = true;
    }

    // Scene2D
    public Stage getStage() {
        return stage;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
