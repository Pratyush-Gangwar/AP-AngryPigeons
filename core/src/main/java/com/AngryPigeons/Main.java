package com.AngryPigeons;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    private TiledMap map;

    @Override
    public void create(){
        batch = new SpriteBatch();
        font = new BitmapFont();

        map = new TmxMapLoader().load("Maps/AP_TestLevelMap.tmx");

        this.setScreen(new LevelScreen(map, this));
//        this.setScreen(new MainMenuScreen(this));
    }
}
