package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class GameScreen extends ScreenAdapter {

    private static GameScreen instance;

    private final SlimeAndSoilGame game;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap currentMap;
    private String currentMapPath;

    private static final float UNIT_SCALE = (float) 1 / 16;

    // --- Player & UI ---
    private Player player;
    private HUD hud;

    // --- Camera Boundary Variables ---
    private float mapWorldWidth;
    private float mapWorldHeight;

    // --- Map Transition Triggers ---
    private final List<MapTransition> transitions = new ArrayList<>();

    public GameScreen(SlimeAndSoilGame game) {
        this.game = game;
        instance = this;
    }

    public static GameScreen getInstance() {
        return instance;
    }

    @Override
    public void show() {
        CropFactory.loadAssets();
        SwordData.loadTextures();

        player = new Player();
        hud = new HUD();

        // Load starting map
        loadMap("assets/objects/main.tmx", 5.0f, 5.0f);
    }

    public void loadMap(String mapPath, float spawnX, float spawnY) {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        currentMapPath = mapPath;
        if (!game.assetManager.isLoaded(mapPath)) {
            game.assetManager.load(mapPath, TiledMap.class);
            game.assetManager.finishLoading();
        }

        currentMap = game.assetManager.get(mapPath, TiledMap.class);
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap, UNIT_SCALE, game.batch);

        int mapWidthInTiles = currentMap.getProperties().get("width", Integer.class);
        int mapHeightInTiles = currentMap.getProperties().get("height", Integer.class);
        mapWorldWidth = mapWidthInTiles;
        mapWorldHeight = mapHeightInTiles;

        transitions.clear();

        // Scan Tiled map layers
        for (MapLayer layer : currentMap.getLayers()) {
            for (MapObject object : layer.getObjects()) {

                // Scan for Map Transitions
                if (object.getProperties().containsKey("isTransition") &&
                    Boolean.TRUE.equals(object.getProperties().get("isTransition", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class) * UNIT_SCALE;
                    float y = object.getProperties().get("y", Float.class) * UNIT_SCALE;
                    float width = object.getProperties().get("width", Float.class) * UNIT_SCALE;
                    float height = object.getProperties().get("height", Float.class) * UNIT_SCALE;

                    String targetMap = object.getProperties().get("targetMap", String.class);
                    float targetSpawnX = object.getProperties().get("spawnX", Float.class);
                    float targetSpawnY = object.getProperties().get("spawnY", Float.class);

                    transitions.add(new MapTransition(x, y, width, height, targetMap, targetSpawnX, targetSpawnY));
                }

                // Scan for Light Sources
                if (object.getProperties().containsKey("isLight") &&
                    Boolean.TRUE.equals(object.getProperties().get("isLight", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class) * UNIT_SCALE;
                    float y = object.getProperties().get("y", Float.class) * UNIT_SCALE;
                    LightManager.getInstance().addLightSource(x, y);
                }

                // Scan for Shipping Bin
                if (object.getProperties().containsKey("isShippingBin") &&
                    Boolean.TRUE.equals(object.getProperties().get("isShippingBin", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class) * UNIT_SCALE;
                    float y = object.getProperties().get("y", Float.class) * UNIT_SCALE;
                    float width = object.getProperties().get("width", Float.class) * UNIT_SCALE;
                    float height = object.getProperties().get("height", Float.class) * UNIT_SCALE;

                    ShippingBin.getInstance().setBinArea(x, y, width, height);
                }

                // Scan for Skill Shop
                if (object.getProperties().containsKey("isSkillShop") &&
                    Boolean.TRUE.equals(object.getProperties().get("isSkillShop", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class) * UNIT_SCALE;
                    float y = object.getProperties().get("y", Float.class) * UNIT_SCALE;
                    float width = object.getProperties().get("width", Float.class) * UNIT_SCALE;
                    float height = object.getProperties().get("height", Float.class) * UNIT_SCALE;

                    SkillShop.getInstance().setShopArea(x, y, width, height);
                }

                // Scan for Sword Shop
                if (object.getProperties().containsKey("isSwordShop") &&
                    Boolean.TRUE.equals(object.getProperties().get("isSwordShop", Boolean.class))) {

                    float x = object.getProperties().get("x", Float.class) * UNIT_SCALE;
                    float y = object.getProperties().get("y", Float.class) * UNIT_SCALE;
                    float width = object.getProperties().get("width", Float.class) * UNIT_SCALE;
                    float height = object.getProperties().get("height", Float.class) * UNIT_SCALE;

                    SwordShop.getInstance().setShopArea(x, y, width, height);
                }
            }
        }

        // Reposition player cleanly
        if (player != null) {
            player.setPosition(spawnX, spawnY);
        }
    }

    public MapTransition checkTransition(float feetX, float feetY) {
        for (MapTransition t : transitions) {
            if (t.triggers(feetX, feetY)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public void render(float delta) {
        player.update(delta, currentMap, hud);
        TimeManager.getInstance().update(delta);

        game.camera.position.set(player.getX() + (player.getWidth() / 2), player.getY() + (player.getHeight() / 2), 0);

        float cameraHalfWidth = game.camera.viewportWidth / 2;
        float cameraHalfHeight = game.camera.viewportHeight / 2;

        game.camera.position.x = MathUtils.clamp(game.camera.position.x, cameraHalfWidth, mapWorldWidth - cameraHalfWidth);
        game.camera.position.y = MathUtils.clamp(game.camera.position.y, cameraHalfHeight, mapWorldHeight - cameraHalfHeight);
        game.camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);

        TimeManager.getInstance().applyLightTint(game.batch);
        mapRenderer.setView(game.camera);
        mapRenderer.render();

        if (currentMapPath.contains("main.tmx")) {
            game.batch.begin();
            CropManager.getInstance().render(game.batch);
            game.batch.end();
        }

        game.batch.begin();
        player.render(game.batch);
        game.batch.end();

        game.batch.begin();
        LightManager.getInstance().render(game.batch);
        game.batch.end();

        TimeManager.getInstance().resetLightTint(game.batch);

        hud.render(game.batch, player);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (hud != null) hud.resize(width, height);
    }

    @Override
    public void dispose() {
        if (mapRenderer != null) mapRenderer.dispose();
        if (player != null) player.dispose();
        if (hud != null) hud.dispose();
        CropFactory.dispose();
        SwordData.dispose();
        LightManager.getInstance().dispose();
    }
}
