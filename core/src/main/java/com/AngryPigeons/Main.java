package com.AngryPigeons;

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

    // Only one instance of these classes is ever available
    private HomeScreen homeScreen;
    private LevelSelectorScreen levelSelectorScreen;
    private WinScreen winScreen;
    private LoseScreen loseScreen;

    private LevelRenderer levelRenderer;

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
        levelRenderer = LevelRenderer.getInstance();
        levelRenderer.setMain(this);

        Storage.getInstance().setMain(this);

        // ~~~ Why must we make levelScreenList have the same length as savedLevelList ~~~
        // Assume we didn't make them the same length.

        // 'Play New Game' is selected for Level 1.
        // resetExistingLevelOrCreateNewLevel() is called which adds Level 1 to the levelScreenList at index 0
        // win() is called. The LevelScreen at index 0 is replaced by a blank LevelScreen by resetExistingLevelOrCreateNewLevel()
        // saveLevelToMemory() is called. SavedLevel is added at index 0 of savedLevelList

        // 'Play New Game' is selected for Level 2.
        // resetExistingLevelOrCreateNewLevel() is called which adds Level 2 to the levelScreenList at index 1
        // lose() is called. The LevelScreen at index 1 is replaced by a blank LevelScreen by resetExistingLevelOrCreateNewLevel()
        // saveLevelToMemory() is called. SavedLevel is added at index 1 of savedLevelList

        // Game is now exit and SavedLevelList is stored to disk

        // Game is relaunched
        // Since we don't set LevelScreenList to any size, it is empty
        // 'Play New Game' is selected for Level 2
        // resetExistingLevelOrCreateNewLevel() is called which adds Level 2 to the levelScreenList at index 0
        // Clearly, this is unwanted
        levelScreenList = new ArrayList<>();
        for(int i = 0; i < Storage.getInstance().getSavedLevelList().size(); i++) {
            levelScreenList.add(null);
        }

        levelInfoList = new ArrayList<>();

        levelInfoList.add(new LevelInfo("Maps/AP_TestLevelMap.tmx", new ArrayList<>(List.of(1,2,3))));
        levelInfoList.add(new LevelInfo("Maps/AP_TestLevelMap2.tmx", new ArrayList<>(List.of(1,2,1,3))));
        levelInfoList.add(new LevelInfo("Maps/AP_TestLevelMap3.tmx", new ArrayList<>(List.of(1,2,1,3))));

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

    public LevelScreen resetExistingLevelOrCreateNewLevel(int index) {
        LevelScreen levelScreen = new LevelScreen(levelInfoList.get(index));

        try {
            levelScreenList.set(index, levelScreen);
        } catch (IndexOutOfBoundsException e) {
            levelScreenList.add(levelScreen);
        }

        return levelScreen;
    }

    public void playNewLevel(int index) {
        LevelScreen levelScreen = resetExistingLevelOrCreateNewLevel(index);
        levelRenderer.setLevelScreen(levelScreen);

        this.setScreen(levelRenderer);

        // when switching screens, must update input processor to current screen
        Gdx.input.setInputProcessor(levelRenderer.getStage());
    }

    public void loadLevel(int index) {

        // create a new level (add/set into levelScreenList) and load the savedLevel into it
        // we don't want to load into an existing levelScreen because it has its own box2D world and camera/viewport states
        LevelScreen levelScreen = resetExistingLevelOrCreateNewLevel(index);
        List<SavedLevel> savedLevelList = Storage.getInstance().getSavedLevelList();

        // level wasn't saved
        if (index >= savedLevelList.size()) {
            return;
        }

        if (savedLevelList.get(index).isLoadingDisabled()) {
            return;
        }

        levelScreen.load();
        levelRenderer.setLevelScreen(levelScreen);

        this.setScreen(levelRenderer);

        // when switching screens, must update input processor to current screen
        Gdx.input.setInputProcessor(levelRenderer.getStage());
    }

    @Override
    public void render() {
        super.render(); // won't run without it
    }

    // called when game window is closed
    @Override
    public void dispose() {
        Storage.getInstance().writeToDisk();
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

}
