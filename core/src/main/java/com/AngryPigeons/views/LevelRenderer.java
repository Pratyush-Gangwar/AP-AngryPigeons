package com.AngryPigeons.views;

import com.AngryPigeons.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.AngryPigeons.Utils.Scene2DUtils;

public class LevelRenderer implements Screen, InputProcessor {
    // Scene2D
    private Main main;
    private Stage stage;
    private Table mainTable;
    private Dialog musicDialog;
    private Dialog exitDialog;

    private Table pauseMenuTable;
    private boolean isPaused;
    private boolean wasHidden;
    private boolean hasGameEnded;

    private LevelScreen levelScreen;
//    private int renderID = 1;

    public LevelRenderer(Main main /*, LevelScreen levelScreen*/) {
        // Scene2D
        this.main = main;
//        this.levelScreen = levelScreen;
        this.isPaused = false;
        this.wasHidden = false;
        this.hasGameEnded = false;

        musicDialog = null;
        exitDialog = null;

        stage = new Stage( new ScreenViewport() );

        setupPauseMenu();
        setupMainTable();

    }

    @Override
    public void show(){

        // if we don't reset this boolean, then LevelRenderer.render() will keep quitting early.
        hasGameEnded = false;

        // Scene2D
        if (wasHidden) {
            wasHidden = false;
            isPaused = false;
        }

        // Box2D
        if (!levelScreen.wasShown()) {
            levelScreen.show();
        }
    }

    @Override
    public void render(float v) {
//        System.out.println("level renderer rendered() "  + renderID);

        // Scene2D
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); // needed to render transparent backgrounds

        // Cannot pause if a bird is flying
        // Cannot pause if a bird has landed but hasn't disappeared yet
        // Cannot pause if you've pulled the slingshot
        // Can only pause if there's a bird on the slingshot and you haven't pulled it
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && levelScreen.getCurrentBird().isWaiting() && !levelScreen.isSsPulled()) {
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

//        System.out.println("Before LR calls LS render()");
        levelScreen.render(v);
//        System.out.println("After LR calls LS render(). Val of isPaused: " + isPaused);

        // ~~~ Why is hasGameEnded necessary? ~~~
        // Assume we didn't have this boolean variable
        // levelRenderer.render() calls levelScreen.render()
        // If the win/lose condition is met, then levelScreen.render() calls levelRenderer.winLevel()/loseLevel()
        // Both of them call main.changeLevel() which sets the input processor to the win/lose screen
        // After that, levelRenderer.win()/lose() returns and so does levelRenderer.render()
        // Now, we're back in levelScreen.render()
        // If isPaused is true, the input processor (which was set to win/lose screen) is now set to LevelRenderer
        // if isPaused is false, the input processor is now set to LevelScreen

        // Therefore, we need a third state (i.e, hasGameEnded) to indicate that the input processor set by winLevel()
        // or loseLevel() is not to be modified.
        if (hasGameEnded) {
            return;
        }

        // Scene2D
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (!isPaused) {
            if (!stage.getViewport().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).isZero()) {
//                System.out.println("LR 1");
                Gdx.input.setInputProcessor(this);
            }
        }
        else {
//                System.out.println("LR 2 " + renderID);
            Gdx.input.setInputProcessor(stage);
        }
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
//                main.getLevelSelectorScreen().incrementLastCompleted();
                winLevel();
            }
        });

        debugLoseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loseLevel();
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

    public void winLevel() {
        // reset level will erase all state data - Box2D and isCompleted
        // so we must set the isCompleted attribute after resetting the level
//        System.out.println("winlevel()");

//        isPaused = true;
        hasGameEnded = true;

//        System.out.println("isPaused set to true");

        int levelIdx = main.getLevelScreenList().indexOf(levelScreen);
        LevelScreen resetLevel = main.resetLevel(levelIdx);
        resetLevel.setComplete(true);

        main.changeScreen(Screens.WINSCREEN);
//        System.out.println("win level end");
    }

    public void loseLevel() {
//        isPaused = true;
        hasGameEnded = true;

        int levelIdx = main.getLevelScreenList().indexOf(levelScreen);
        main.resetLevel(levelIdx);
        main.changeScreen(Screens.LOSESCREEN);
    }

    // Scene2D
    private void setupMainTable() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setDebug(Scene2DUtils.scene2DDebugEnabled);
        mainTable.top().left();

        mainTable.add(Scene2DUtils.makeLabel("Esc to Pause!", 30)).pad(10);

//        Texture texture = new Texture(Gdx.files.internal("textures/pause.png"));
//        TextureRegion textureRegion = new TextureRegion(texture);
//        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);

//        ImageButton imageButton = new ImageButton(textureRegionDrawable);
//        mainTable.add(imageButton).width(100).height(100);

//        imageButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                isPaused = true;
//            }
//        });

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

    public LevelScreen getLevelScreen() {
        return levelScreen;
    }

    public void setLevelScreen(LevelScreen levelScreen) {
        this.levelScreen = levelScreen;
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { //<- Added
//        System.out.println("Mouse down at: " + screenX + ", " + screenY);
        levelScreen.touchDown(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { //<- Added
//        System.out.println("Mouse dragged at: " + screenX + ", " + screenY);
        levelScreen.touchDragged(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { //<- Added
//        System.out.println("Mouse up at: " + screenX + ", " + screenY);
        levelScreen.touchUp(screenX, screenY, pointer, button);
        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) { return false; } //<- Added
    @Override
    public boolean keyUp(int keycode) { return false; } //<- Added
    @Override
    public boolean keyTyped(char character) { return false; } //<- Added
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; } //<- Added
    @Override
    public boolean scrolled(float amountX, float amountY) { return false; } //<- Added
}
