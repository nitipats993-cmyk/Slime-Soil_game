package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.Rectangle;

public class SwordShop {

    private static SwordShop instance;
    private final Rectangle shopArea = new Rectangle(-1, -1, 0, 0);

    private SwordShop() {}

    public static SwordShop getInstance() {
        if (instance == null) {
            instance = new SwordShop();
        }
        return instance;
    }

    public void setShopArea(float gridX, float gridY, float width, float height) {
        this.shopArea.set(gridX, gridY, width, height);
        System.out.println(String.format("Sword Shop Area set in Tiled: X[%.1f to %.1f], Y[%.1f to %.1f]",
            gridX, gridX + width, gridY, gridY + height));
    }

    public boolean isShopLocation(int x, int y) {
        return shopArea.contains(x + 0.5f, y + 0.5f);
    }
}
