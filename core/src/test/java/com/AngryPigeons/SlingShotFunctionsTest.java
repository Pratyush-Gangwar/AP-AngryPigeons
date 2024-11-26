package com.AngryPigeons;

import com.AngryPigeons.Utils.Constants;
import com.AngryPigeons.Utils.SlingShotUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class SlingShotFunctionsTest {

    @Test
    public void checkDistance(){
        assertEquals(1.0f,SlingShotUtil.calculateEuclideanDistance(0,0, 1,0), 0.00001f);
        assertEquals(1.0f,SlingShotUtil.calculateEuclideanDistance(0,0, 0,1), 0.00001f);
        assertEquals(Math.sqrt(2), SlingShotUtil.calculateEuclideanDistance(0,0, 1,1), 0.00001f);
    }

    @Test
    public void checkAngle(){
        assertEquals(0, SlingShotUtil.calculateAngle(0,0,1,0), 0.001f);
        assertEquals(Constants.PI/2, SlingShotUtil.calculateAngle(0,0,0,1), 0.001f);
        assertEquals(Constants.PI/4, SlingShotUtil.calculateAngle(0,0,1,1), 0.001f);
    }
}
