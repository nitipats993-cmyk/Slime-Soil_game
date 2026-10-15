package com.yourname.slimeandsoil;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

public class Hoe implements Tool {

    private static final String PLOWED_TILESET_NAME = "plowed";

    @Override
    public void use(Player player, int targetX, int targetY, TiledMap currentMap) {
        if (!isFarmable(currentMap, targetX, targetY)) {
            System.out.println("Cannot farm here! Target tile (X: " + targetX + ", Y: " + targetY + ") is outside the farmable area.");
            return;
        }

        //validation for finding tiled pic
        TiledMapTileLayer soilLayer = (TiledMapTileLayer) currentMap.getLayers().get("Soil");
        if (soilLayer == null) {
            System.out.println("Error: Layer 'Soil' was not found in TiledMap!");
            return;
        }
        TiledMapTileSet tileSet = currentMap.getTileSets().getTileSet(PLOWED_TILESET_NAME);
        if (tileSet == null || !tileSet.iterator().hasNext()) {
            System.out.println("Warning: Could not find tileset named '" + PLOWED_TILESET_NAME + "'!");
            return;
        }

        //change dirt to tilled dirt
        TiledMapTile tilledTile = tileSet.iterator().next();
        TiledMapTileLayer.Cell cell = soilLayer.getCell(targetX, targetY);
        if (cell == null) {
            cell = new TiledMapTileLayer.Cell();
        }

        cell.setTile(tilledTile);
        soilLayer.setCell(targetX, targetY, cell);
        System.out.println("Successfully tilled soil graphic at X: " + targetX + " Y: " + targetY);
    }

    private boolean isFarmable(TiledMap map, int tileX, int tileY) {
        TiledMapTileLayer farmableLayer = (TiledMapTileLayer) map.getLayers().get("Farmable");
        if (farmableLayer == null) {
            System.out.println("Warning: Layer 'Farmable' was not found in TiledMap!");
            return false;
        }
        TiledMapTileLayer.Cell cell = farmableLayer.getCell(tileX, tileY);
        return cell != null;
    }

    @Override
    public String getName() {
        return "Basic Hoe";
    }
}
