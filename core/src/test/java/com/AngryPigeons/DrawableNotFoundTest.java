package com.AngryPigeons;

import com.AngryPigeons.domain.Bird;
import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.AngryPigeons.exceptions.DrawableNotFoundException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DrawableNotFoundTest {

    @Test
    public void checkBirdNotFound(){
        assertThrows(DrawableNotFoundException.class,()->{Bird b = new Bird(null, 1, 1, 4);});
    }

    @Test
    public void checkMaterialNotFound(){
        assertThrows(DrawableNotFoundException.class,()->{Material b = new Material(null, 1, 1, 4);});
    }

    @Test
    public void checkPigNotFound(){
        assertThrows(DrawableNotFoundException.class,()->{Pig b = new Pig(null, 1, 1, 4);});
    }
}
