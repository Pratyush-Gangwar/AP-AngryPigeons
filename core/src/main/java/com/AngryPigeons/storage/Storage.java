package com.AngryPigeons.storage;

import com.AngryPigeons.Main;
import com.AngryPigeons.views.LevelScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Why save first in memory and then in disk?
// Same for load

public class Storage {
    private Gson gson;
    private Main main;

    private List<SavedLevel> savedLevelList;
    private final static String path = "storage.txt";

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
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.savedLevelList = new ArrayList<>();
        loadAllLevels();
    }

    public void saveAllLevels() {
        for(SavedLevel savedLevel : savedLevelList) {
            savedLevel.sync();
        }

        try (FileWriter fileWriter = new FileWriter(path)) {
            gson.toJson(savedLevelList, fileWriter);
        } catch (IOException e) {
            System.out.println("Exception while saving to file.");
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

    public void loadAllLevels() {
        if (!saveFileExists()) {
            System.out.println("Save file doesn't exist.");
            return;
        }

        TypeToken<List<SavedLevel>> listTypeToken = new TypeToken<>(){};

        try (FileReader fileReader = new FileReader(path)) {
            this.savedLevelList = gson.fromJson(fileReader, listTypeToken);
            System.out.println(this.savedLevelList);
        } catch (IOException e) {
            System.out.println("Error while loading from file.");
        }

        System.out.println("Loaded all levels.");
    }

    public void loadLevel(LevelScreen levelScreen) {
        int levelIdx = main.getLevelScreenList().indexOf(levelScreen);

        // level hasn't been saved (likely a new level)
        if (levelIdx >= savedLevelList.size()) {
            return;
        }

        SavedLevel savedLevel = savedLevelList.get(levelIdx);
        savedLevel.load(levelScreen);
    }

    private boolean saveFileExists() {
        File file = new File(path);
        return file.exists();
    }

}
