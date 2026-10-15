package com.yourname.slimeandsoil;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class Seed implements Tool {

    private final String cropType;

    public Seed(String cropType) {
        this.cropType = cropType;
    }

    @Override
    public void use(Player player, int targetX, int targetY, TiledMap currentMap) {
        String seedItemName = cropType + " Seed";

        if (Inventory.getInstance().getItemCount(seedItemName) <= 0) {
            System.out.println("Out of " + seedItemName + "s! Defeat slimes in the field to collect more!");
            return;
        }

        boolean success = CropManager.getInstance().plantCrop(targetX, targetY, cropType, currentMap);

        if (success) {
            Inventory.getInstance().removeItem(seedItemName, 1);
        }
    }

    @Override
    public String getName() {
        int count = Inventory.getInstance().getItemCount(cropType + " Seed");
        return cropType + " Seed (" + count + ")";
    }
}
