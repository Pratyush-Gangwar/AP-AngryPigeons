package com.AngryPigeons.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Scene2DUtils {
    private static Texture backgroundTexture;
    public static Skin skin;
    public static int buttonWidth = 300;
    public static int paddingSpace = 10;
    public static boolean scene2DDebugEnabled = false;
    public static Music music;

    public static void setBackgroundTexture(String file) {
        backgroundTexture = new Texture(Gdx.files.internal(file));
    }

    public static void setSkin(String file) {
        skin = new Skin(Gdx.files.internal(file));
    }

    public static void setMusic(String file) {
        music = Gdx.audio.newMusic(Gdx.files.internal(file));
    }


    public static void setBackgroundOfTable(Table table) {
        Drawable drawable = new TextureRegionDrawable(backgroundTexture);
        table.setBackground(drawable);
    }

    // FreeTypeFontGenerator lets you resize fonts on the fly
    public static Label makeLabel(String text, int fontSize) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RebellionSquad.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.color = Color.RED;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        return new Label(text, labelStyle);
    }

    public static void makeExitWindow(Stage stage) {
        Dialog dialog = new Dialog("Exit", Scene2DUtils.skin) {
            @Override
            protected void result(Object object) {
                Integer option = (Integer) object;

                if (option == 1) {
                    Gdx.app.exit();
                }
            }
        };

        dialog.text("Are you sure you want to exit? Unsaved progress will be lost.");
        dialog.button("Yes", 1);
        dialog.button("No", 0);

        dialog.show(stage);
    }

    public static void makeMusicControlWindow(Stage stage) {
        Dialog dialog = new Dialog("Music", Scene2DUtils.skin) {
            @Override
            protected void result(Object object) {}
        };

        Slider volSlider = new Slider(0f, 100f, 0.01f, false, Scene2DUtils.skin);
        volSlider.setValue(Scene2DUtils.music.getVolume() * 100);

        volSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Scene2DUtils.music.setVolume(volSlider.getValue() / 100);
            }
        });

        dialog.text("Set volume");
        dialog.button("Done");

        dialog.getContentTable().add(volSlider);
        dialog.show(stage);
    }
}
