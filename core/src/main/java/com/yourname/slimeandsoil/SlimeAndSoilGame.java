package com.yourname.slimeandsoil;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SlimeAndSoilGame extends Game {
    // Make these public so your screens can access them easily
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Viewport viewport;
    public AssetManager assetManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(20, (float) 11.25, camera); // 16x9 World Units (1 Unit = 1 Tile)

        // Standard AssetManager setup for Tiled Maps
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        // Boot directly into the GameScreen
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose(); // Automatically disposes the active screen
        batch.dispose();
        assetManager.dispose();
    }
}
