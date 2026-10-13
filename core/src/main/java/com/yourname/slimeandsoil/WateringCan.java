package com.yourname.slimeandsoil;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class WateringCan implements Tool {
    @Override
    public void use(Player player, int targetX, int targetY, TiledMap currentMap) {
        boolean watered = CropManager.getInstance().waterCrop(targetX, targetY);
        if (!watered) {
            System.out.println("Watered soil at X: " + targetX + " Y: " + targetY);
        }
    }

    @Override
    public String getName() {
        return "Basic Watering Can";
    }
}
