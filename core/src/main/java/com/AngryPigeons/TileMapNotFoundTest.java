package com.AngryPigeons;

import com.AngryPigeons.views.LevelInfo;
import com.AngryPigeons.views.LevelScreen;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TileMapNotFoundTest {
    @Test
    public void tileMapNotFound(){
        assertThrows(com.badlogic.gdx.utils.GdxRuntimeException.class, ()->{
            LevelScreen l = new LevelScreen(new LevelInfo("", new ArrayList<>()));
        });
    }
}
