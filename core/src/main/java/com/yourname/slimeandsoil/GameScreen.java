package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends ScreenAdapter {
    private final SlimeAndSoilGame game;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap currentMap;

    private static final float UNIT_SCALE = (float) 1 / 16;

    // --- Player & UI ---
    private Player player;
    private HUD hud;

    // --- Camera Boundary Variables ---
    private float mapWorldWidth;
    private float mapWorldHeight;

    public GameScreen(SlimeAndSoilGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        game.assetManager.load("assets/objects/main.tmx", TiledMap.class);
        game.assetManager.finishLoading();
        currentMap = game.assetManager.get("assets/objects/main.tmx", TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap, UNIT_SCALE, game.batch);

        int mapWidthInTiles = currentMap.getProperties().get("width", Integer.class);
        int mapHeightInTiles = currentMap.getProperties().get("height", Integer.class);
        mapWorldWidth = mapWidthInTiles;
        mapWorldHeight = mapHeightInTiles;

        // 1. Initialize Crop Assets
        CropFactory.loadAssets();

        // 2. Initialize Player & HUD
        player = new Player();
        hud = new HUD();

        // 3. Scan Tiled map layers for objects
        for (MapLayer layer : currentMap.getLayers()) {
            for (MapObject object : layer.getObjects()) {

                // Scan for Light Sources
                if (object.getProperties().containsKey("isLight") &&
                    Boolean.TRUE.equals(object.getProperties().get("isLight", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);

                    float worldX = x * UNIT_SCALE;
                    float worldY = y * UNIT_SCALE;

                    LightManager.getInstance().addLightSource(worldX, worldY);
                }

                // Scan for Shipping Bin Selling Area
                if (object.getProperties().containsKey("isShippingBin") &&
                    Boolean.TRUE.equals(object.getProperties().get("isShippingBin", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class);
                    float y = object.getProperties().get("y", Float.class);
                    float width = object.getProperties().get("width", Float.class);
                    float height = object.getProperties().get("height", Float.class);

                    // Convert pixel units from Tiled into grid tile units
                    float gridX = x * UNIT_SCALE;
                    float gridY = y * UNIT_SCALE;
                    float gridW = width * UNIT_SCALE;
                    float gridH = height * UNIT_SCALE;

                    ShippingBin.getInstance().setBinArea(gridX, gridY, gridW, gridH);
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        // 1. Update Logic & Time Cycle
        player.update(delta, currentMap, hud);
        TimeManager.getInstance().update(delta);

        // 2. Camera Placement
        game.camera.position.set(player.getX() + (player.getWidth() / 2), player.getY() + (player.getHeight() / 2), 0);

        float cameraHalfWidth = game.camera.viewportWidth / 2;
        float cameraHalfHeight = game.camera.viewportHeight / 2;

        game.camera.position.x = MathUtils.clamp(game.camera.position.x, cameraHalfWidth, mapWorldWidth - cameraHalfWidth);
        game.camera.position.y = MathUtils.clamp(game.camera.position.y, cameraHalfHeight, mapWorldHeight - cameraHalfHeight);
        game.camera.update();

        // 3. Clear Screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // 4. Render Map with Time-of-Day Light Tint
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);

        TimeManager.getInstance().applyLightTint(game.batch);
        mapRenderer.setView(game.camera);
        mapRenderer.render();

        // 5. Render Planted Crops
        game.batch.begin();
        CropManager.getInstance().render(game.batch);
        game.batch.end();

        // 6. Render Player
        game.batch.begin();
        player.render(game.batch);
        game.batch.end();

        // 7. Render Lamp & Window Light Glows (Additive Pass)
        game.batch.begin();
        LightManager.getInstance().render(game.batch);
        game.batch.end();

        // 8. Reset Ambient Light Tint
        TimeManager.getInstance().resetLightTint(game.batch);

        // 9. Render HUD Overlay (always rendered un-tinted at full brightness)
        hud.render(game.batch, player);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (hud != null) {
            hud.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (player != null) player.dispose();
        if (hud != null) hud.dispose();
        CropFactory.dispose();
        LightManager.getInstance().dispose();
    }
}
