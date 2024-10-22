package com.AngryPigeons.Utils;

import com.AngryPigeons.Material;
import com.AngryPigeons.Pig;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

import static com.AngryPigeons.Utils.Constants.PPM;

public class TiledMapUtil {

    public static ArrayList<Material> parseMaterial(World world, MapObjects objects, boolean isStatic) {
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
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 5f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0f;
            body.createFixture(shape, 1f);
            shape.dispose();

            material.add(new Material(body, (float) object.getProperties().get("x"), (float) object.getProperties().get("y")));
        }
        return material;
    }

    public static ArrayList<Pig> parsePigs(World world, MapObjects objects, boolean isStatic, int type) {
        System.out.println("PARSING PIGS");
        ArrayList<Pig> pigs = new ArrayList<>();

        for (MapObject object: objects){
//            float world_x = (float) object.getProperties().get("x")/PPM;
//            float world_y = ((float) object.getProperties().get("y"))/PPM;
//            System.out.println(world_x +" WORLD "+ world_y);
            CircleShape shape;
            System.out.println(object);
            if (object instanceof EllipseMapObject){
                System.out.println("IS CIRCLE");
                EllipseMapObject Obj = (EllipseMapObject) object;
                shape = createCircleShape((EllipseMapObject) object);
            }
            else {
                continue;
            }

            float world_x = ((EllipseMapObject) object).getEllipse().x/PPM;
            float world_y = ((EllipseMapObject) object).getEllipse().y/PPM;
            shape.setPosition(new Vector2(world_x+shape.getRadius(), world_y+shape.getRadius()));
            Body body;
            BodyDef def = new BodyDef();
            if (isStatic) {
                def.type = BodyDef.BodyType.StaticBody;
            }
            else {
                def.type = BodyDef.BodyType.DynamicBody;
            }
            body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0.5f;
            fixtureDef.restitution = 0f;
            body.createFixture(fixtureDef);
            if (type == 1) {
                pigs.add(new Pig(body, world_x, world_y, 25, shape.getRadius()));
            }
            else if (type == 2) {
                pigs.add(new Pig(body, world_x, world_y, 50, shape.getRadius()));
            }
            else{
                pigs.add(new Pig(body, world_x, world_y, 100, shape.getRadius()));
            }
            shape.dispose();

            System.out.println("r = "+shape.getRadius()+" "+object.getProperties().get("x")+" x "+(float) object.getProperties().get("y"));
        }
        return pigs;
    }

    public static PolygonShape createPolygonShape(PolygonMapObject polygonObject) {
        float[] vertices = polygonObject.getPolygon().getTransformedVertices(); // Get vertices in pixel coordinates

        // Convert vertices into world coordinates
        Vector2[] worldVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
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

    private static ChainShape createChainShape(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[(vertices.length / 2)];

        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
        }

        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);

        return cs;
    }

    public static CircleShape createCircleShape(EllipseMapObject circleObject) {
        CircleShape shape = new CircleShape();
        shape.setRadius(circleObject.getEllipse().width/2/PPM);

        return shape;
    }
}
