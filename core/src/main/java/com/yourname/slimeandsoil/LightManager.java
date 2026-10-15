package com.yourname.slimeandsoil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class LightManager {

    private static LightManager instance;
    private Texture lightTexture;
    private final List<Vector2> lightSources;

    private LightManager() {
        lightSources = new ArrayList<>();
        generateLightTexture();
    }

    public static LightManager getInstance() {
        if (instance == null) {
            instance = new LightManager();
        }
        return instance;
    }

    public void clearLights() {
        if (lightSources != null) {
            lightSources.clear();
        }
    }

    // Tiled light fixtures
    private void generateLightTexture() {
        int radius = 64; // glow size
        int size = radius * 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float dist = Vector2.dst(x, y, radius, radius);
                if (dist < radius) {
                    float alpha = 1.0f - (dist / radius);
                    alpha = alpha * alpha;

                    //change color here
                    pixmap.setColor(1.0f, 0.85f, 0.5f, alpha * 0.85f);
                    pixmap.drawPixel(x, y);
                }
            }
        }

        lightTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void addLightSource(float worldX, float worldY) {
        lightSources.add(new Vector2(worldX, worldY));
    }


    public void render(SpriteBatch batch) {
        float currentHour = TimeManager.getInstance().getCurrentHour();

        // when dark lights on
        if (currentHour >= 17.0f || currentHour < 6.0f) {


            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batch.setColor(Color.WHITE);
            float lightSize = 4.0f;

            // center light pos
            for (Vector2 pos : lightSources) {
                batch.draw(
                    lightTexture,
                    pos.x - (lightSize / 2f),
                    pos.y - (lightSize / 2f),
                    lightSize,
                    lightSize
                );
            }

            // makes it looks better
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public void dispose() {
        if (lightTexture != null) {
            lightTexture.dispose();
        }
    }
}
