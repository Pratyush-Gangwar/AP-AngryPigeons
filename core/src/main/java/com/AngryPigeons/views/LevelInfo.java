package com.AngryPigeons.views;

import java.util.ArrayList;

// ~~~ Which attributes to serialize? ~~~
// - birds: NO
//	- set once in constructor and doesn't change during run-time
//
//- tileMapPath: NO
//	- set once in constructor and doesn't change during run-time

public class LevelInfo {
    private static int nextLevelID = 0;

    private int levelID;
    private ArrayList<Integer> birds;
    private String tileMapPath;

    public LevelInfo(String tileMapPath, ArrayList<Integer> birds) {
        this.tileMapPath = tileMapPath;
        this.birds = birds;

        this.levelID = LevelInfo.nextLevelID;
        LevelInfo.nextLevelID++;
    }

    public ArrayList<Integer> getBirds() {
        return birds;
    }

    public String getTileMapPath() {
        return tileMapPath;
    }

    public int getLevelID() {
        return levelID;
    }

}
