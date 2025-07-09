package com.AngryPigeons;

import com.AngryPigeons.exceptions.TileMapNotFoundException;
import com.AngryPigeons.storage.SavedLevel;
import com.AngryPigeons.storage.Storage;
import com.AngryPigeons.views.LevelInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.AngryPigeons.Utils.Scene2DUtils;
import com.AngryPigeons.views.*;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

// ~~~ Which attributes to serialize? ~~~
// LevelScreenList: YES

// Scene2D entirely
public class Main extends Game {
    private List<LevelScreen> levelScreenList;
    private List<LevelInfo> levelInfoList;
    // After the Main constructor is called and before create() is called, Gdx is initialized
    // Therefore, we cannot move the below code into a constructor because Scene2DUtils uses Gdx methods
    @Override
    public void create() {
        Scene2DUtils.setBackgroundTexture("textures/main2.jpg");
        Scene2DUtils.setMusic("music/music2.wav");
        Scene2DUtils.setSkin("skins/freezing/freezing-ui.json");

        Music music = Scene2DUtils.music;
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();

        levelScreenList = new ArrayList<>();
        levelInfoList = new ArrayList<>();

        levelInfoList.add(new LevelInfo("Maps\\AP_TestLevelMap.tmx", new ArrayList<>(List.of(1, 2, 3))));
        levelInfoList.add(new LevelInfo("Maps\\AP_TestLevelMap2.tmx", new ArrayList<>(List.of(1,2,1,3))));
        levelInfoList.add(new LevelInfo("Maps\\AP_TestLevelMap3.tmx", new ArrayList<>(List.of(1,2,1,3))));

        LevelRenderer.getInstance().setMain(this);

        this.changeScreen(Screens.HOMESCREEN);
    }


    // 1. Game.setScreen() calls hide() on the current screen and show() on the argument screen.
    // 2. When switching screens, must update input processor to current screen.
    // 3. All screens except LevelScreens are singleton classes.
    // Each of these classes contain a static reference to their sole instance.
    // And because of that, these instances are not garbage collected and remain active throughout the program duration
    public void changeScreen(Screens screen) {

        if (screen == Screens.HOMESCREEN) {
            HomeScreen homeScreen = HomeScreen.getInstance(this);

            this.setScreen(homeScreen);
            Gdx.input.setInputProcessor(homeScreen.getStage());
        }

        else if (screen == Screens.LEVELSELECTORSCREEN) {
            LevelSelectorScreen levelSelectorScreen = LevelSelectorScreen.getInstance(this);

            this.setScreen(levelSelectorScreen);
            Gdx.input.setInputProcessor(levelSelectorScreen.getStage());
        }

        else if (screen == Screens.WINSCREEN) {
            WinScreen winScreen = WinScreen.getInstance(this);

            this.setScreen(winScreen);
            Gdx.input.setInputProcessor(winScreen.getStage());
        }

        else if (screen == Screens.LOSESCREEN) {
            LoseScreen loseScreen = LoseScreen.getInstance(this);

            this.setScreen(loseScreen);
            Gdx.input.setInputProcessor(loseScreen.getStage());
        }
    }

    public LevelScreen resetExistingLevelOrCreateNewLevel(int index) {
        LevelScreen oldScreen = null;

        try {
            oldScreen = levelScreenList.get(index);
        } catch (IndexOutOfBoundsException ignored) {}

        if (oldScreen != null) {
            oldScreen.dispose(); // ✅ Properly dispose Box2D world and renderer
        }

        LevelScreen newScreen = null;
        try {
            newScreen = new LevelScreen(levelInfoList.get(index));
            if (index < levelScreenList.size()) {
                levelScreenList.set(index, newScreen);
            } else {
                levelScreenList.add(newScreen);
            }
        } catch (TileMapNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return newScreen;
    }


    public void playNewLevel(int index) {
        LevelRenderer levelRenderer = LevelRenderer.getInstance();

        LevelScreen levelScreen = resetExistingLevelOrCreateNewLevel(index);
        levelScreen.initializeBirdPointerIfNeeded();
        levelRenderer.setLevelScreen(levelScreen);

        this.setScreen(levelRenderer);

        // when switching screens, must update input processor to current screen
        Gdx.input.setInputProcessor(levelRenderer.getStage());
    }

    public void loadLevel(int index) {
        LevelScreen levelScreen = resetExistingLevelOrCreateNewLevel(index);

        Storage.getInstance().loadLevel(levelScreen);
        levelScreen.spawnCurrentBird();

        LevelRenderer levelRenderer = LevelRenderer.getInstance();
        levelRenderer.setLevelScreen(levelScreen);
        this.setScreen(levelRenderer);
        Gdx.input.setInputProcessor(levelRenderer.getStage());
    }

    @Override
    public void render() {
        super.render(); // won't run without it
    }

    // called when game window is closed
    @Override
    public void dispose() {

    }

    public List<LevelScreen> getLevelScreenList() {
        return levelScreenList;
    }

    public List<LevelInfo> getLevelInfoList() {
        return levelInfoList;
    }
}
