package com.AngryPigeons;

import com.AngryPigeons.views.LevelInfo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.AngryPigeons.Utils.Scene2DUtils;
import com.AngryPigeons.views.*;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

// Scene2D entirely
public class Main extends Game {

    // Only one instance of these classes is ever available
    private HomeScreen homeScreen;
    private LevelSelectorScreen levelSelectorScreen;
    private WinScreen winScreen;
    private LoseScreen loseScreen;

    private LevelRenderer levelRenderer;

//    private List<LevelRenderer> levelRendererList;
    private List<LevelScreen> levelScreenList;
    private List<LevelInfo> levelInfoList;

    @Override
    public void create() {
        Scene2DUtils.setBackgroundTexture("textures/main2.jpg");
        Scene2DUtils.setMusic("music/music2.wav");
        Scene2DUtils.setSkin("skins/freezing/freezing-ui.json");

        Music music = Scene2DUtils.music;
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        homeScreen = new HomeScreen(this);
        levelSelectorScreen = new LevelSelectorScreen(this);
        levelRenderer = new LevelRenderer(this);

//        levelRendererList = new ArrayList<>();
        levelScreenList = new ArrayList<>();
        levelInfoList = new ArrayList<>();

        levelInfoList.add(new LevelInfo("Maps/AP_TestLevelMap.tmx", new ArrayList<>(List.of(1,2,1))));
        levelInfoList.add(new LevelInfo("Maps/AP_TestLevelMap2.tmx", new ArrayList<>(List.of(1,2,1,3))));

//        LevelScreen levelScreen1 = new LevelScreen("Maps/AP_TestLevelMap.tmx", new ArrayList<>(List.of(1,3,1)));
//        LevelScreen levelScreen1 = new LevelScreen(levelInfoList.get(0));
//        levelScreenList.add(levelScreen1);


//        LevelScreen levelScreen2 = new LevelScreen("Maps/AP_TestLevelMap2.tmx", new ArrayList<>(List.of(1,2,1,3)));
//        levelScreenList.add(levelScreen2);


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

//            System.out.println("setScreen(WIN) " + Gdx.input.getInputProcessor().equals(winScreen.getStage()));
        }

        else if (screen == Screens.LOSESCREEN) {
            if (loseScreen == null) {
                loseScreen = new LoseScreen(this);
            }

            this.setScreen(loseScreen);
            Gdx.input.setInputProcessor(loseScreen.getStage());
        }
    }

    public LevelScreen resetLevel(int index) {
        LevelScreen levelScreen = new LevelScreen(levelInfoList.get(index));
        //        levelScreenList.add(levelScreen);
        try {
            levelScreenList.set(index, levelScreen);
        } catch (IndexOutOfBoundsException e) {
            levelScreenList.add(levelScreen);
        }

        return levelScreen;
    }

    public void playNewLevel(int index) {
//        System.out.println("play new level called");
        LevelScreen levelScreen = resetLevel(index);

//        LevelRenderer levelRenderer = new LevelRenderer(this, levelScreen);

//        try {
//            levelRendererList.set(index, levelRenderer);
//        } catch (IndexOutOfBoundsException e) {
//            levelRendererList.add(levelRenderer);
//        }

        levelRenderer.setLevelScreen(levelScreen);
        levelScreen.setLevelRenderer(levelRenderer);

        this.setScreen(levelRenderer);
        Gdx.input.setInputProcessor(levelRenderer.getStage());
    }

    public void loadLevel(int index) {

    }

//    public void changeLevel(int index) {
//
//        LevelRenderer levelRenderer;
//        LevelScreen levelScreen = levelScreenList.get(index);
//
//        try {
//            levelRenderer = levelRendererList.get(index);
//        } catch (IndexOutOfBoundsException e) {
//            levelRenderer = new LevelRenderer(this, levelScreen);
//            levelRendererList.add(levelRenderer);
//        }
//
//        levelScreen.setLevelRenderer(levelRenderer);
//
//        this.setScreen(levelRenderer);
//        Gdx.input.setInputProcessor(levelRenderer.getStage());
//    }

    @Override
    public void render() {
        super.render(); // won't run without it
    }

    public LevelSelectorScreen getLevelSelectorScreen() {
        return levelSelectorScreen;
    }

    public List<LevelScreen> getLevelScreenList() {
        return levelScreenList;
    }

    public List<LevelInfo> getLevelInfoList() {
        return levelInfoList;
    }

    public HomeScreen getHomeScreen() {
        return homeScreen;
    }

    public WinScreen getWinScreen() {
        return winScreen;
    }

    public LoseScreen getLoseScreen() {
        return loseScreen;
    }

    public LevelRenderer getLevelRenderer() {
        return levelRenderer;
    }

//    public List<LevelRenderer> getLevelRendererList() {
//        return levelRendererList;
//    }
}
