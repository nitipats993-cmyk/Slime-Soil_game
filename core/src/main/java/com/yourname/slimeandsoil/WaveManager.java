package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private static WaveManager instance;

    private final List<Slime> slimes;
    private int currentWave = 0;
    private float waveTimer = 0;

    private WaveManager() {
        slimes = new ArrayList<>();
    }

    public static WaveManager getInstance() {
        if (instance == null) {
            instance = new WaveManager();
        }
        return instance;
    }

    public void update(float delta, Player player, TiledMap currentMap, String currentMapPath) {
        if (currentMapPath == null) return;

        // 1. AUTOMATIC FARM DEFENSE WAVE (Triggers at 18:00 PM inside main.tmx)
        if (currentMapPath.contains("main.tmx")) {
            float hour = TimeManager.getInstance().getCurrentHour();
            if (hour >= 18.0f && !TimeManager.getInstance().isFarmWaveTriggeredToday()) {
                TimeManager.getInstance().setFarmWaveTriggeredToday(true);
                startNextWave(currentMap);
                System.out.println("=== 18:00 PM EVENING INVASION! Slimes are attacking your farm! ===");
            }
        }

        // 2. FOREST DUNGEON MANUAL WAVE CONTROLS (Press [M] inside untitled.tmx)
        if (currentMapPath.contains("untitled.tmx")) {
            if (waveTimer > 0) {
                waveTimer -= delta;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                if (currentWave == 0 || getAliveSlimeCount() == 0 || waveTimer <= 0) {
                    startNextWave(currentMap);
                } else {
                    System.out.println("Wait " + (int) Math.ceil(waveTimer) + "s or defeat all slimes to start the next wave!");
                }
            }
        }

        // Update all active slimes and remove finished dead slimes
        for (int i = slimes.size() - 1; i >= 0; i--) {
            Slime slime = slimes.get(i);
            slime.update(delta, player, currentMap);

            if (slime.isDeathFinished()) {
                slime.dispose();
                slimes.remove(i);
            }
        }
    }

    public void startNextWave(TiledMap currentMap) {
        currentWave++;
        waveTimer = 15; // Reset 15-second skip timer

        int tier = (currentWave - 1) / 3;
        int waveInBetween = (currentWave - 1) % 3;

        // Increase enemies spawned for each wave in between
        int spawnCount = 3 + (tier * 2) + (waveInBetween * 2);

        int mapWidth = currentMap.getProperties().get("width", Integer.class);
        int mapHeight = currentMap.getProperties().get("height", Integer.class);
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) currentMap.getLayers().get("Collision");

        for (int i = 0; i < spawnCount; i++) {
            int randomX = MathUtils.random(2, mapWidth - 3);
            int randomY = MathUtils.random(2, mapHeight - 3);

            // Re-roll if the random tile is on a red Collision tile
            if (collisionLayer != null) {
                int tries = 0;
                while (collisionLayer.getCell(randomX, randomY) != null && tries < 30) {
                    randomX = MathUtils.random(2, mapWidth - 3);
                    randomY = MathUtils.random(2, mapHeight - 3);
                    tries++;
                }
            }

            slimes.add(new Slime(randomX, randomY, currentWave));
        }

        System.out.println("=== Wave " + currentWave + " Started! Spawned " + spawnCount + " Slimes ===");
    }

    public void render(SpriteBatch batch) {
        for (Slime slime : slimes) {
            slime.render(batch);
        }
    }

    public int getAliveSlimeCount() {
        int count = 0;
        for (Slime slime : slimes) {
            if (!slime.getStats().isDead()) {
                count++;
            }
        }
        return count;
    }

    public List<Slime> getSlimes() {
        return slimes;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public float getWaveTimer() {
        return waveTimer;
    }

    public void dispose() {
        for (Slime slime : slimes) {
            slime.dispose();
        }
        slimes.clear();
    }
}
