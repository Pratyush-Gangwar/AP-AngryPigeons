package com.AngryPigeons.Utils;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.maps.objects.PolygonMapObject;

import static com.AngryPigeons.Utils.Constants.PPM;

public class TextureRenderUtil {

    public static float getPolygonWidth(PolygonMapObject polygonMapObject) {
        Polygon polygon = polygonMapObject.getPolygon();
        float[] vertices = polygon.getVertices();
        float maxX = Float.NEGATIVE_INFINITY;
        float minX = Float.POSITIVE_INFINITY;

        for (int i = 0; i < vertices.length; i += 2) {
            float x = vertices[i];
            maxX = Math.max(maxX, x);
            minX = Math.min(minX, x);
        }

        return (maxX - minX) * polygon.getScaleX();
    }

    public static float getPolygonHeight(PolygonMapObject polygonMapObject) {
        Polygon polygon = polygonMapObject.getPolygon();
        float[] vertices = polygon.getVertices();
        float maxY = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;

        for (int i = 1; i < vertices.length; i += 2) {
            float y = vertices[i];
            maxY = Math.max(maxY, y);
            minY = Math.min(minY, y);
        }

        return (maxY - minY) * polygon.getScaleY();
    }
}
