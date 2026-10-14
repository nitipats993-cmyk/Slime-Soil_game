package com.yourname.slimeandsoil;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

import java.util.HashMap;
import java.util.Map;

public class CropManager {

    private static CropManager instance;
    private final Map<GridPoint2, Crop> activeCrops;

    private CropManager() {
        activeCrops = new HashMap<>();
    }

    public static CropManager getInstance() {
        if (instance == null) {
            instance = new CropManager();
        }
        return instance;
    }

    /**
     * Attempts to plant a crop on tilled soil.
     */
    public boolean plantCrop(int x, int y, String cropType, TiledMap map) {
        GridPoint2 pos = new GridPoint2(x, y);

        if (activeCrops.containsKey(pos)) {
            System.out.println("A crop is already growing here!");
            return false;
        }

        TiledMapTileLayer soilLayer = (TiledMapTileLayer) map.getLayers().get("Soil");
        if (soilLayer == null || soilLayer.getCell(x, y) == null) {
            System.out.println("You must till the soil before planting!");
            return false;
        }

        Crop newCrop = CropFactory.createCrop(cropType, x, y);
        activeCrops.put(pos, newCrop);
        System.out.println("Planted " + newCrop.getName() + " at X: " + x + " Y: " + y);
        return true;
    }

    /**
     * Waters a crop at specific grid coordinates.
     */
    public boolean waterCrop(int x, int y) {
        GridPoint2 pos = new GridPoint2(x, y);
        Crop crop = activeCrops.get(pos);
        if (crop != null) {
            crop.water();
            System.out.println("Watered " + crop.getName() + " at X: " + x + " Y: " + y);
            return true;
        }
        return false;
    }

    /**
     * harvesting TIME
     */
    public boolean harvestCrop(int x, int y) {
        GridPoint2 key = new GridPoint2(x, y);
        Crop crop = activeCrops.get(key);

        if (crop != null && crop.isFullyGrown()) {
            String cropName = crop.getName();

            // Add 1 harvested item to inventory
            Inventory.getInstance().addItem(cropName, 1);

            // Grant Skill Point if harvesting a SkillFlower
            if (cropName.equals("SkillFlower")) {
                PlayerStats.getInstance().addSkillPoints(1);
                PlayerStats.getInstance().showPickupText("+1 Skill Point!");
            }

            // Regrowable crops (like Tomato) reset to Stage 1; others are removed
            if (crop.isRegrowable()) {
                crop.resetToStageOne();
            } else {
                activeCrops.remove(key);
            }
            return true;
        }
        return false;
    }

    /**
     * Advance all watered crops to their next growth stage.
     */
    public void advanceDay() {
        for (Crop crop : activeCrops.values()) {
            crop.grow();
        }
    }

    /**
     * Renders all active crops.
     */
    public void render(SpriteBatch batch) {
        for (Crop crop : activeCrops.values()) {
            crop.render(batch);
        }
    }
}
