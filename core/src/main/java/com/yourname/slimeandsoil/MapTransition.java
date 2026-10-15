package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.Rectangle;

public class MapTransition {
    private final Rectangle bounds;
    private final String targetMap;
    private final float spawnX;
    private final float spawnY;

    public MapTransition(float gridX, float gridY, float width, float height, String targetMap, float spawnX, float spawnY) {
        this.bounds = new Rectangle(gridX, gridY, width, height);
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public boolean triggers(float playerFeetX, float playerFeetY) {
        return bounds.contains(playerFeetX, playerFeetY);
    }
    public String getTargetMap() { return targetMap; }
    public float getSpawnX() { return spawnX; }
    public float getSpawnY() { return spawnY; }
}
