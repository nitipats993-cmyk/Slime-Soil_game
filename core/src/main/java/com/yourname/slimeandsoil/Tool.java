package com.yourname.slimeandsoil;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface Tool {
    // Every specific tool must implement its own version of this method
    void use(Player player, int targetX, int targetY, TiledMap currentMap);

    String getName();
}
