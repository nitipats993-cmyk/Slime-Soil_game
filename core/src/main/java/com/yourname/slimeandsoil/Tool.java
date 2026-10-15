package com.yourname.slimeandsoil;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface Tool {
    void use(Player player, int targetX, int targetY, TiledMap currentMap);
    String getName();
}
