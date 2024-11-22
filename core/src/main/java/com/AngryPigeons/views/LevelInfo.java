package com.AngryPigeons.views;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.ArrayList;

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
