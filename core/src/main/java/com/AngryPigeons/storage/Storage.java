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
    private static final String SAVE_FOLDER = "saves";
    private static final String META_FILE = "metadata.json";

    private static Storage instance;

    public static Storage getInstance() {
        if (Storage.instance == null) {
            Storage.instance = new Storage();
        }

        return Storage.instance;
    }

    private Storage() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        File folder = new File(SAVE_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public void saveLevel(LevelScreen levelScreen) {
        SavedLevel savedLevel = new SavedLevel();
        savedLevel.save(levelScreen);

        int levelID = levelScreen.getLevelID();
        try (FileWriter writer = new FileWriter(getLevelFile(levelID))) {
            gson.toJson(savedLevel, writer);
            System.out.println("Level " + levelID + " saved to disk.");
        } catch (IOException e) {
            System.out.println("Error saving level " + levelID + ": " + e.getMessage());
        }

        if (savedLevel.isComplete()) {
            updateMaxCompletedLevel(levelID);
        }
    }

    public void loadLevel(LevelScreen levelScreen) {
        int levelID = levelScreen.getLevelID();
        File levelFile = getLevelFile(levelID);

        if (!levelFile.exists()) {
            System.out.println("No save found for level " + levelID);
            return;
        }

        try (FileReader reader = new FileReader(levelFile)) {
            SavedLevel savedLevel = gson.fromJson(reader, SavedLevel.class);
            if (savedLevel != null) {
                savedLevel.load(levelScreen);
                System.out.println("Level " + levelID + " loaded from disk.");
            }
        } catch (IOException e) {
            System.out.println("Error loading level " + levelID + ": " + e.getMessage());
        }
    }


    private File getLevelFile(int levelID) {
        return new File(SAVE_FOLDER + File.separator + "level_" + levelID + ".json");
    }

    public boolean levelSaveExists(int index) {
        File file = new File("saves/level_" + index + ".json");
        return file.exists();
    }

    public void updateMaxCompletedLevel(int levelID) {
        int currentMax = getMaxCompletedLevel();
        if (levelID > currentMax) {
            File metaFile = new File(SAVE_FOLDER + File.separator + META_FILE);
            try (FileWriter writer = new FileWriter(metaFile)) {
                gson.toJson(levelID, writer);
                System.out.println("Updated metadata: max completed level = " + levelID);
            } catch (IOException e) {
                System.out.println("Error saving metadata: " + e.getMessage());
            }
        }
    }

    public int getMaxCompletedLevel() {
        File metaFile = new File(SAVE_FOLDER + File.separator + META_FILE);
        if (!metaFile.exists()) return -1;

        try (FileReader reader = new FileReader(metaFile)) {
            Integer levelID = gson.fromJson(reader, Integer.class);
            return levelID != null ? levelID : -1;
        } catch (IOException e) {
            System.out.println("Error reading metadata: " + e.getMessage());
            return -1;
        }
    }

    public void deleteSavedLevel(int levelID) {
        File levelFile = new File(SAVE_FOLDER + File.separator + "level_" + levelID + ".json");
        if (levelFile.exists() && levelFile.delete()) {
            System.out.println("Deleted save for level " + levelID);
        } else {
            System.out.println("No save to delete for level " + levelID);
        }
    }



}
