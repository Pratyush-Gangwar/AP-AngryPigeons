package com.AngryPigeons.storage;

import com.AngryPigeons.Main;
import com.AngryPigeons.views.LevelScreen;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private Gson gson;
    private Main main;

    private List<SavedLevel> savedLevelList;

    private static Storage instance;

    public static Storage getInstance() {
        if (Storage.instance == null) {
            Storage.instance = new Storage();
        }

        return Storage.instance;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    private Storage() {
        this.gson = new Gson();
        this.savedLevelList = new ArrayList<>();
    }

    public void saveAllLevels() {
        for(SavedLevel savedLevel : savedLevelList) {
            savedLevel.sync();
            gson.toJson(savedLevel);
        }

        System.out.println("All levels saved");
    }

    public void saveLevel(LevelScreen levelScreen) {
        SavedLevel savedLevel;

        List<LevelScreen> levelScreenList = main.getLevelScreenList();
        int levelIdx = levelScreenList.indexOf(levelScreen);

        if (levelIdx < savedLevelList.size()) {
            savedLevel = savedLevelList.get(levelIdx);
            savedLevel.sync();
        } else {
            savedLevel = new SavedLevel(levelScreen);
            savedLevelList.add(savedLevel);
        }

        System.out.println("Level " + levelIdx + " saved");

    }
}
