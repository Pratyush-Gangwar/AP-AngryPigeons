package com.AngryPigeons.views;

import java.util.ArrayList;

// ~~~ Which attributes to serialize? ~~~
// - birds: NO
//	- set once in constructor and doesn't change during run-time
//
//- tileMapPath: NO
//	- set once in constructor and doesn't change during run-time

public class LevelInfo {
    private ArrayList<Integer> birds;
    private String tileMapPath;

    public LevelInfo(String tileMapPath, ArrayList<Integer> birds) {
        this.tileMapPath = tileMapPath;
        this.birds = birds;
    }

    public ArrayList<Integer> getBirds() {
        return birds;
    }

    public void setBirds(ArrayList<Integer> birds) {
        this.birds = birds;
    }

    public String getTileMapPath() {
        return tileMapPath;
    }

    public void setTileMapPath(String tileMapPath) {
        this.tileMapPath = tileMapPath;
    }
}
