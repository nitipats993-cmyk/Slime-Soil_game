package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class SwordData {

    public static class TierInfo {
        public final String name;
        public final int baseDamage;
        public final int subUpgradeDamageBonus;
        public final int baseUpgradeCost;
        public final String texturePath;

        public TierInfo(String name, int baseDamage, int subUpgradeDamageBonus, int baseUpgradeCost, String texturePath) {
            this.name = name;
            this.baseDamage = baseDamage;
            this.subUpgradeDamageBonus = subUpgradeDamageBonus;
            this.baseUpgradeCost = baseUpgradeCost;
            this.texturePath = texturePath;
        }
    }

    // sword upgrade stats
    public static final TierInfo[] TIERS = new TierInfo[] {
        new TierInfo("Wooden Sword", 3, 1, 30, "assets/objects/myAssets/wooden_sword.png"),
        new TierInfo("Iron Sword", 8, 2, 80, "assets/objects/myAssets/iron_sword.png"),
        new TierInfo("Hero Sword", 18, 3, 180, "assets/objects/myAssets/hero_sword.png"),
        new TierInfo("Photon Blade", 32, 4, 350, "assets/objects/myAssets/photon_blade.png"),
        new TierInfo("I am Kirito", 50, 6, 600, "assets/objects/myAssets/kirito_sword.png"),
        new TierInfo("Excalibur", 80, 10, 1000, "assets/objects/myAssets/excalibur.png"),
        new TierInfo("???", 150, 0, 0, "assets/objects/myAssets/masamune.png") // Max Tier (Masamune)
    };

    private static final Texture[] loadedTextures = new Texture[TIERS.length];

    public static void loadTextures() {
        for (int i = 0; i < TIERS.length; i++) {
            if (Gdx.files.internal(TIERS[i].texturePath).exists()) {
                loadedTextures[i] = new Texture(Gdx.files.internal(TIERS[i].texturePath));
            }
        }
    }

    public static Texture getSwordTexture(int tier) {
        if (tier >= 0 && tier < loadedTextures.length) {
            return loadedTextures[tier];
        }
        return null;
    }

    public static int calculateDamage(int tier, int subLevel) {
        if (tier >= TIERS.length) tier = TIERS.length - 1;
        TierInfo info = TIERS[tier];
        return info.baseDamage + (subLevel * info.subUpgradeDamageBonus);
    }

    public static int getUpgradeCost(int tier, int subLevel) {
        if (tier >= TIERS.length - 1) return 0; // Max tier cannot be upgraded
        TierInfo info = TIERS[tier];
        return info.baseUpgradeCost + (subLevel * (info.baseUpgradeCost / 2));
    }

    public static void dispose() {
        for (int i = 0; i < loadedTextures.length; i++) {
            if (loadedTextures[i] != null) {
                loadedTextures[i].dispose();
                loadedTextures[i] = null;
            }
        }
    }
}
