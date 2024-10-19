package com.AngryPigeons.Utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.CircleShape;

import static com.AngryPigeons.Utils.Constants.PPM;

public class TextureRenderUtil {
    public static float getBodyWidth(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            Shape shape = fixture.getShape();

            if (shape instanceof PolygonShape) {
                PolygonShape polygon = (PolygonShape) shape;
                float maxX = Float.NEGATIVE_INFINITY;
                float minX = Float.POSITIVE_INFINITY;

                Vector2 vertex = new Vector2();
                for (int i = 0; i < polygon.getVertexCount(); i++) {
                    polygon.getVertex(i, vertex);
                    maxX = Math.max(maxX, vertex.x);
                    minX = Math.min(minX, vertex.x);
                }
                return (maxX - minX) * PPM;  // Convert to pixels
            } else if (shape instanceof CircleShape) {
                CircleShape circle = (CircleShape) shape;
                return circle.getRadius() * 2 * PPM;  // Convert diameter to pixels
            }
        }
        return 0;
    }

    public static float getBodyHeight(Body body) {
        for (Fixture fixture : body.getFixtureList()) {
            Shape shape = fixture.getShape();

            if (shape instanceof PolygonShape) {
                PolygonShape polygon = (PolygonShape) shape;
                float maxY = Float.NEGATIVE_INFINITY;
                float minY = Float.POSITIVE_INFINITY;

                Vector2 vertex = new Vector2();
                for (int i = 0; i < polygon.getVertexCount(); i++) {
                    polygon.getVertex(i, vertex);
                    maxY = Math.max(maxY, vertex.y);
                    minY = Math.min(minY, vertex.y);
                }
                return (maxY - minY) * PPM;  // Convert to pixels
            } else if (shape instanceof CircleShape) {
                CircleShape circle = (CircleShape) shape;
                return circle.getRadius() * 2 * PPM;  // Convert diameter to pixels
            }
        }
        return 0;
    }
}
