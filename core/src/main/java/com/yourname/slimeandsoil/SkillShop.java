package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.Rectangle;

public class SkillShop {

    private static SkillShop instance;
    private final Rectangle shopArea = new Rectangle(-1, -1, 0, 0);

    private SkillShop() {}

    public static SkillShop getInstance() {
        if (instance == null) {
            instance = new SkillShop();
        }
        return instance;
    }

    public void setShopArea(float gridX, float gridY, float width, float height) {
        this.shopArea.set(gridX, gridY, width, height);
        System.out.println(String.format("Skill Shop Area set in Tiled: X[%.1f to %.1f], Y[%.1f to %.1f]",
            gridX, gridX + width, gridY, gridY + height));
    }

    public boolean isShopLocation(int x, int y) {
        return shopArea.contains(x + 0.5f, y + 0.5f);
    }
}
