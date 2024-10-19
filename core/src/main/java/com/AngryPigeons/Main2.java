package com.AngryPigeons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.AngryPigeons.domain.Level;
import com.AngryPigeons.Utils.Scene2DUtils;
import com.AngryPigeons.views.*;
import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

// Scene2D entirely
public class Main2 extends Game {
    // Only one instance of these classes is ever available
    private HomeScreen homeScreen;
    private LevelSelectorScreen levelSelectorScreen;
    private WinScreen winScreen;
    private LevelRenderer levelRenderer;
    private LoseScreen loseScreen;

    @Override
    public void create() {
        Scene2DUtils.setBackgroundTexture("textures/main.jpg");
        Scene2DUtils.setMusic("music/music2.wav");
        // Scene2DUtils.setSkin("skins/lgdxs/lgdxs-ui.json");
        // Scene2DUtils.setSkin("skins/comic/comic-ui.json");
        // Scene2DUtils.setSkin("skins/golden-spiral/golden-ui-skin.json");
        Scene2DUtils.setSkin("skins/freezing/freezing-ui.json");

        Music music = Scene2DUtils.music;
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        homeScreen = new HomeScreen(this);
        levelSelectorScreen = new LevelSelectorScreen(this);

        Level level1 = new Level();
        Level level2 = new Level();
        Level level3 = new Level();

        levelSelectorScreen.addLevel(level1);
        levelSelectorScreen.addLevel(level2);
        levelSelectorScreen.addLevel(level3);

        this.changeScreen(Screens.HOMESCREEN);
    }


    // .setScreen() calls .hide() on the current screen and .show() on the argument screen
    public void changeScreen(Screens screen) {

        // when switching screens, must update input processor to current screen

        if (screen == Screens.HOMESCREEN) {

            // created only if not null - ensures only one instance is ever there
            if (homeScreen == null) {
                homeScreen = new HomeScreen(this);
            }

            this.setScreen(homeScreen);
            Gdx.input.setInputProcessor(homeScreen.getStage());
        }

        else if (screen == Screens.LEVELSELECTORSCREEN) {
            if (levelSelectorScreen == null) {
                levelSelectorScreen = new LevelSelectorScreen(this);
            }

            this.setScreen(levelSelectorScreen);
            Gdx.input.setInputProcessor(levelSelectorScreen.getStage());
        }

        else if (screen == Screens.WINSCREEN) {
            if (winScreen == null) {
                winScreen = new WinScreen(this);
            }

            this.setScreen(winScreen);
            Gdx.input.setInputProcessor(winScreen.getStage());
        }

        else if (screen == Screens.LEVELRENDERER) {
            if (levelRenderer == null) {
                levelRenderer = new LevelRenderer(this);
            }

            this.setScreen(levelRenderer);
            Gdx.input.setInputProcessor(levelRenderer.getStage());
        }

        else if (screen == Screens.LOSESCREEN) {
            if (loseScreen == null) {
                loseScreen = new LoseScreen(this);
            }

            this.setScreen(loseScreen);
            Gdx.input.setInputProcessor(loseScreen.getStage());
        }
    }

    @Override
    public void render() {
        super.render(); // won't run without it
    }

    public HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public LevelSelectorScreen getLevelSelectorScreen() {
        return levelSelectorScreen;
    }

    public WinScreen getWinScreen() {
        return winScreen;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }
}
