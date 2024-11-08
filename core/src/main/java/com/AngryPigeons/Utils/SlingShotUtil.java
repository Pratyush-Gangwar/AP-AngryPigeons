package com.AngryPigeons.Utils;

import com.badlogic.gdx.math.Vector2;
import com.AngryPigeons.domain.Bird;


public class SlingShotUtil {

    public static float calculateEuclideanDistance(float x1, float y1, float x2, float y2){
        System.out.println(x1+" x "+y1+" to "+x2+" x "+y2);
        System.out.println("DISTANCE "+Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2))));
        return (float) Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2)));
    }

    public static float calculateAngle(float x1, float y1, float x2, float y2){
        float angle = (float) Math.atan2((y2-y1), (x2-x1));
        angle %= (float) (2*Constants.PI);
        if (angle<0){
            return (float) (angle + Constants.PI*2);
        }
        return angle;
    }

    public static void fireBird(Bird bird){
        System.out.println("WEE!!!");
    }

}
