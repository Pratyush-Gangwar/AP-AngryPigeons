package com.AngryPigeons.Utils;

import com.AngryPigeons.Material;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class TiledMapUtil {

    public static ArrayList<Material> parseTiledObjectLayer(World world, MapObjects objects, boolean isStatic) {
        ArrayList<Material> material = new ArrayList<>();
        for (MapObject object: objects){
            Shape shape;
            float x = (float) object.getProperties().get("x");
            if (object instanceof PolygonMapObject){
                System.out.println("IS DYNAMIC SHAPE");
                shape = createPolygonShape((PolygonMapObject) object);
            }
            else if (object instanceof PolylineMapObject){
                System.out.println("IS STATIC");
                shape = createChainShape((PolylineMapObject) object);
            }
            else {
                continue;
            }
            Body body;
            BodyDef def = new BodyDef();
            if (isStatic) {
                def.type = BodyDef.BodyType.StaticBody;
            }
            else {
                def.type = BodyDef.BodyType.DynamicBody;
            }
            body = world.createBody(def);
            body.createFixture(shape, 1f);
            shape.dispose();

            material.add(new Material(body, (float) object.getProperties().get("x"), (float) object.getProperties().get("y")));
        }
        return material;
    }

    public static PolygonShape createPolygonShape(PolygonMapObject polygonObject) {
        float[] vertices = polygonObject.getPolygon().getTransformedVertices(); // Get vertices in pixel coordinates

        // Convert vertices into world coordinates
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
        }

        // Create and set the vertices for PolygonShape
        PolygonShape shape = new PolygonShape();
        if (worldVertices.length <= 8) {
            shape.set(worldVertices); // Set the vertices if they are 8 or fewer
        } else {
            System.err.println("Polygon with more than 8 vertices is not supported by Box2D PolygonShape.");
        }

        return shape;
    }

    private static ChainShape createChainShape(PolylineMapObject polygon) {
        float[] vertices = polygon.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[(vertices.length / 2)];

        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
        }

        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);

        return cs;
    }
}
