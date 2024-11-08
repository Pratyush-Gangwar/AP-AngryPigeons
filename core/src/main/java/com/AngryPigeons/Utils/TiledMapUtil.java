package com.AngryPigeons.Utils;

import com.AngryPigeons.domain.Bird;
import com.AngryPigeons.domain.Material;
import com.AngryPigeons.domain.Pig;
import com.AngryPigeons.domain.SlingShot;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

import static com.AngryPigeons.Utils.Constants.PPM;

public class TiledMapUtil {

    public static SlingShot parseSlingShot(World world, MapObjects objects, boolean isStatic) {
        SlingShot ss;

        for (MapObject object: objects){
            Shape shape;
            if (object instanceof PolygonMapObject){
                shape = createPolygonShape((PolygonMapObject) object);
            }
            else {
                continue;
            }
            Body body;
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            Rectangle rect = ((PolygonMapObject) object).getPolygon().getBoundingRectangle();
            def.position.x = (float) object.getProperties().get("x")/PPM+rect.width/PPM/2;
            def.position.y = (float) object.getProperties().get("y")/PPM-rect.height/PPM/2;
            body = world.createBody(def);
//            FixtureDef fixtureDef = new FixtureDef();
//            fixtureDef.shape = shape;
//            fixtureDef.density = 2f;
//            body.createFixture(fixtureDef);
            shape.dispose();

            ss = new SlingShot(body, 3*PPM, 5*PPM);
            return ss;
        }

        return  null;
    }

    public static void parseBoundary(World world, MapObjects objects, boolean isStatic) {
        for (MapObject object: objects){
            Shape shape;
            if (object instanceof PolylineMapObject){
                shape = createChainShape((PolylineMapObject) object);
            }
            else {
                continue;
            }
            Body body;
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 15f;
            body.createFixture(fixtureDef);
            shape.dispose();
        }
    }

    public static ArrayList<Material> parseMaterial(World world, MapObjects objects, int type) {
        ArrayList<Material> material = new ArrayList<>();
        for (MapObject object: objects){
            Shape shape;
            if (object instanceof PolygonMapObject){
                shape = createPolygonShape((PolygonMapObject) object);
            }
            else {
                continue;
            }
            Body body;
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            Rectangle rect = ((PolygonMapObject) object).getPolygon().getBoundingRectangle();
            def.position.x = (float) object.getProperties().get("x")/PPM+rect.width/PPM/2;
            def.position.y = (float) object.getProperties().get("y")/PPM-rect.height/PPM/2;
            body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            if (type == 1) {
                fixtureDef.density = 1f;
            }
            if (type == 2) {
                fixtureDef.density = 3f;
            }
            if (type == 3){
                fixtureDef.density = 5f;
            }
            body.createFixture(fixtureDef);
            shape.dispose();

            material.add(new Material(body, rect.getWidth(), rect.getHeight(), type));
        }
        return material;
    }

    public static ArrayList<Pig> parsePigs(World world, MapObjects objects, boolean isStatic, int type) {
        ArrayList<Pig> pigs = new ArrayList<>();

        for (MapObject object: objects){
            PolygonShape shape;
            if (object instanceof PolygonMapObject){
                shape = createPolygonShape((PolygonMapObject)object);
            }
            else {
                continue;
            }
            Rectangle rect = ((PolygonMapObject) object).getPolygon().getBoundingRectangle();
            Body body;
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            def.position.x = (float) object.getProperties().get("x")/PPM+rect.width/PPM/2;
            def.position.y = (float) object.getProperties().get("y")/PPM-rect.height/PPM/2;
            body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 2f;
            body.createFixture(fixtureDef);
            shape.dispose();


            pigs.add(new Pig(body, rect.getWidth(), rect.getHeight()));

            System.out.println(body.getPosition());

//            System.out.println("r = "+shape.getRadius()+" "+object.getProperties().get("x")+" x "+(float) object.getProperties().get("y"));
        }
        return pigs;
    }

    public static ArrayList<Bird> parseBird(World world, MapObjects objects, int type) {
        ArrayList<Bird> birds = new ArrayList<>();
        for (MapObject object: objects){
            Shape shape;
            if (object instanceof PolygonMapObject){
                shape = createPolygonShape((PolygonMapObject) object);
            }
            else {
                continue;
            }
            Body body;
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.DynamicBody;
            Rectangle rect = ((PolygonMapObject) object).getPolygon().getBoundingRectangle();
            def.position.x = (float) object.getProperties().get("x")/PPM+rect.width/PPM/2;
            def.position.y = (float) object.getProperties().get("y")/PPM-rect.height/PPM/2;
            body = world.createBody(def);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 5f;
            body.createFixture(fixtureDef);
            shape.dispose();

            birds.add(new Bird(body, rect.getWidth(), rect.getHeight(), type));
        }
        return birds;
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

        Rectangle rect = polygonObject.getPolygon().getBoundingRectangle();
        shape.setAsBox(rect.getWidth()/PPM/2, rect.getHeight()/PPM/2);

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
}
