package com.AngryPigeons.Utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.AngryPigeons.domain.Bird;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

import static com.AngryPigeons.Utils.Constants.PPM;

// ~~~ Which attributes to serialize? ~~~
// - NO attributes at all

public class SlingShotUtil {

    public static float calculateEuclideanDistance(float x1, float y1, float x2, float y2){
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

    public static void drawTrajectory(ShapeRenderer shapeRenderer, OrthographicCamera camera, Vector3 startPosition, float distance, Vector2 gravity){
//        System.out.println("Drawing trajectory at "+startPosition);
        float x = startPosition.x*PPM;
        float y = startPosition.y*PPM;
        float vx = (float) ((distance)*Constants.MAX_VELOCITY*Math.cos(startPosition.z));
        float vy = (float) ((distance)*Constants.MAX_VELOCITY*Math.sin(startPosition.z));

        shapeRenderer.setProjectionMatrix(camera.combined);
        for (int i = 0; i<25; i++){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.circle(x,y,8);
            shapeRenderer.end();

            x+=2*vx;
            y+=2*vy;
            vy+=2*(gravity.y/PPM);
        }
    }
}
