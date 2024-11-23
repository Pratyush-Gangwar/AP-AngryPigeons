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
import java.util.List;

// ~~~ Save mechanism ~~~
// All changes are first saved in memory in the form of List<SavedLevel>
// Only when the user exits the program are the in-memory changes stored on the disk. So the disk is written ONCE.
// The previous contents of the save-file are overwritten

// We want the levels to be stored in a deterministic order (i.e, level 1, then level 2, etc.)
// This is because the levels must be deserialized in the same order they were serialized
// So, we must know the order of serialization at the time of deserialization

// Assume that we didn't use the above system of caching changes in memory and then writing to disk only once.
// That is, assume we write to the disk each time save() is called
// If the user plays Level 2 before Level 1, then level 2 will be written to the disk before level 1
// There will be no deterministic order to serialization
// and hence we won't know which order to deserialize in when we load the levels

// ~~~ Load mechanism ~~~
// Again, all on-disk saved levels are read ONCE
// When the user presses 'Load Level' on the LevelSelectorScreen, that LevelScreen is synced with the appropriate
// SavedLevel. All the LevelScreens are not synced with their SavedLevels because
// (a) Syncing them all at once is inefficient. Lazy syncing is better.
// (b) The LevelScreens haven't even been constructed! They are constructed when the user clicks Load Game or New Game

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
        readLevelsFromDisk();
    }

    public void writeToDisk() {
        try (FileWriter fileWriter = new FileWriter(path)) {
            gson.toJson(savedLevelList, fileWriter);
        } catch (IOException e) {
            System.out.println("Exception while saving to file.");
        }

        System.out.println("All levels saved");
    }

    public SavedLevel getOrCreateSavedLevel(int levelIdx) {
        SavedLevel savedLevel;

        if (levelIdx < savedLevelList.size()) {
            savedLevel = savedLevelList.get(levelIdx);
        } else {
            savedLevel = new SavedLevel();
            savedLevelList.add(savedLevel);
        }

        return savedLevel;
    }

    public void saveLevelInMemory(LevelScreen levelScreen) {

        List<LevelScreen> levelScreenList = main.getLevelScreenList();
        int levelIdx = levelScreenList.indexOf(levelScreen);

        SavedLevel savedLevel = getOrCreateSavedLevel(levelIdx);
        savedLevel.save(levelScreen);

        // if you're trying to save a level, then you should be able to load it
        savedLevel.setLoadingDisabled(false);

        System.out.println("Level " + levelIdx + " saved");
    }

    public void readLevelsFromDisk() {
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

    public void loadLevelFromMemory(LevelScreen levelScreen) {
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

    public List<SavedLevel> getSavedLevelList() {
        return savedLevelList;
    }

}
