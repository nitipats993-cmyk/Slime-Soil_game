package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class CropFactory {

    private static final Map<String, Texture> cropTextures = new HashMap<>();
    private static final Map<String, TextureRegion[]> cropStageRegions = new HashMap<>();
    private static Texture wateredSoilTexture;

    public static void loadAssets() {
        if (Gdx.files.internal("assets/objects/myAssets/plowed_watered.png").exists()) {
            wateredSoilTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/plowed_watered.png"));
        } else {
            System.out.println("Warning: 'plowed_watered.png' not found in assets/objects/myAssets/");
        }

        loadCropTexture("tomato", "assets/objects/myAssets/tomato.png", 4);
        loadCropTexture("carrot", "assets/objects/myAssets/carrot.png", 4);
        loadCropTexture("wheat", "assets/objects/myAssets/wheat.png", 4);
        loadCropTexture("gemflower", "assets/objects/myAssets/gemflower.png", 4);
        loadCropTexture("skillflower", "assets/objects/myAssets/skillflower.png", 4);
    }

    private static void loadCropTexture(String cropType, String filePath, int stageCount) {
        if (!Gdx.files.internal(filePath).exists()) {
            System.out.println("Warning: Asset file '" + filePath + "' not found in assets directory!");
            return;
        }

        Texture texture = new Texture(Gdx.files.internal(filePath));
        cropTextures.put(cropType, texture);

        int frameWidth = texture.getWidth() / stageCount;
        int frameHeight = texture.getHeight();

        TextureRegion[] stages = new TextureRegion[stageCount];
        for (int i = 0; i < stageCount; i++) {
            stages[i] = new TextureRegion(texture, i * frameWidth, 0, frameWidth, frameHeight);
        }

        cropStageRegions.put(cropType, stages);
    }

    public static Texture getWateredSoilTexture() {
        return wateredSoilTexture;
    }

    public static Crop createCrop(String cropType, int gridX, int gridY) {
        String key = cropType.toLowerCase().replaceAll("\\s+", "");
        Crop crop;

        switch (key) {
            case "tomato":
                crop = new Crop("Tomato", gridX, gridY, 3, true);
                break;
            case "carrot":
                crop = new Crop("Carrot", gridX, gridY, 3, false);
                break;
            case "wheat":
                crop = new Crop("Wheat", gridX, gridY, 3, false);
                break;
            case "gemflower":
                crop = new Crop("GemFlower", gridX, gridY, 3, false);
                break;
            case "skillflower":
                crop = new Crop("SkillFlower", gridX, gridY, 3, false);
                break;
            default:
                crop = new Crop("Tomato", gridX, gridY, 3, true);
                key = "tomato";
                break;
        }

        if (cropStageRegions.containsKey(key)) {
            crop.setStageTextures(cropStageRegions.get(key));
        }

        return crop;
    }

    public static void dispose() {
        for (Texture texture : cropTextures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        if (wateredSoilTexture != null) {
            wateredSoilTexture.dispose();
        }
        cropTextures.clear();
        cropStageRegions.clear();
    }
}
