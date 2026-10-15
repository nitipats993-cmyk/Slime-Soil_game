package com.yourname.slimeandsoil;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Crop {
    private final String name;
    private final int gridX;
    private final int gridY;

    private int currentStage;
    private final int maxStage;
    private boolean isWatered;
    private final boolean isRegrowable;

    private TextureRegion[] stageTextures;

    public Crop(String name, int gridX, int gridY, int maxStage, boolean isRegrowable) {
        this.name = name;
        this.gridX = gridX;
        this.gridY = gridY;
        this.currentStage = 0; // Starts at stage 0 (Seed)
        this.maxStage = maxStage;
        this.isWatered = false;
        this.isRegrowable = isRegrowable;
    }

    public void setStageTextures(TextureRegion[] textures) {
        this.stageTextures = textures;
    }

    public void water() {
        this.isWatered = true;
    }

    public void grow() {
        if (isWatered) {
            if (currentStage < maxStage) {
                currentStage++;
            }
            isWatered = false; // Always reset watering state for the new day
        }
    }

    public boolean isHarvestable() {
        return currentStage >= maxStage;
    }

    public void harvest() {
        if (isHarvestable()) {
            if (isRegrowable) {
                currentStage = Math.max(1, maxStage - 2); // Reset to earlier growth stage for regrowable crops
            }
        }
    }

    public void render(SpriteBatch batch) {
        // if use can tile change to wet
        if (isWatered) {
            Texture wateredTile = CropFactory.getWateredSoilTexture();
            if (wateredTile != null) {
                batch.draw(wateredTile, gridX, gridY, 1.0f, 1.0f);
            }
        }

        // updates crop growth stage
        if (stageTextures != null && currentStage < stageTextures.length) {
            TextureRegion currentFrame = stageTextures[currentStage];
            if (currentFrame != null) {
                batch.draw(currentFrame, gridX, gridY, 1.0f, 1.0f);
            }
        }
    }

    public String getName() { return name; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public boolean isWatered() { return isWatered; }
    public int getCurrentStage() { return currentStage; }

    public boolean isFullyGrown() {
        return this.currentStage >= 3;
    }
    public boolean isRegrowable() {
        return this.isRegrowable;
    } //only tomato
    public void resetToStageOne() {
        this.currentStage = 1;
    } //tomato dont die when collect
}
