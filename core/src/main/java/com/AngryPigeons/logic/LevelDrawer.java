package com.AngryPigeons.logic;

import com.AngryPigeons.domain.Killable;
import com.AngryPigeons.Utils.SlingShotUtil;
import com.AngryPigeons.logic.BirdManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class LevelDrawer {
    private final Sprite ffSprite;
    private int ffAnimationCnt = 0;
    private int ffAnimationFrame = 0;

    public LevelDrawer() {
        ffSprite = new Sprite(new Texture("Images/FastForwardBlack.png"));
        ffSprite.setSize(100, 100);
    }

    public void drawKillables(SpriteBatch batch, List<? extends Killable> killables) {
        for (Killable k : killables) {
            if (!k.isDead()) {
                k.render(batch);
            }
        }
    }

    public void drawTrajectoryIfNeeded(ShapeRenderer shapeRenderer, OrthographicCamera camera,
                                       Vector3 birdPos, float distance, Vector2 gravity, boolean isPulled) {
        if (isPulled) {
            SlingShotUtil.drawTrajectory(shapeRenderer, camera, birdPos, distance, gravity);
        }
    }

    public void drawFastForwardIfNeeded(SpriteBatch batch, float timeStep, float FAST_TIME_STEP) {
        if (timeStep != FAST_TIME_STEP) return;

        int[] positions = {1105, 1130, 1155};
        ffSprite.setPosition(positions[ffAnimationFrame], 10);

        ffAnimationCnt = (ffAnimationCnt + 1) % 20;
        if (ffAnimationCnt == 0) {
            ffAnimationFrame = (ffAnimationFrame + 1) % 3;
        }

        batch.begin();
        ffSprite.draw(batch);
        batch.end();
    }
}
