package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SwordShopDialog {

    private boolean active = false;

    public void open() { this.active = true; }

    public void update() {
        if (!active) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            PlayerStats.getInstance().upgradeSword();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            active = false;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active) return;

        PlayerStats stats = PlayerStats.getInstance();
        int tier = stats.getSwordTier();
        int subLevel = stats.getSwordSubLevel();
        int gold = Inventory.getInstance().getGold();

        font.getData().setScale(2.0f);
        font.setColor(Color.GOLD);
        font.draw(batch, "=== BLACKSMITH SWORD SHOP ===", 380, 530);

        font.getData().setScale(1.4f);

        if (tier < 6) {
            SwordData.TierInfo currentInfo = SwordData.TIERS[tier];
            int currentDmg = stats.getBaseSwordDamage();
            int cost = SwordData.getUpgradeCost(tier, subLevel);

            font.setColor(Color.WHITE);
            font.draw(batch, "Current Weapon: " + currentInfo.name + " (+" + subLevel + "/5)", 380, 470);
            font.draw(batch, "Base Damage: " + currentDmg + " ATK", 380, 430);

            if (subLevel < 5) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "Upgrade Sub-Level (+" + (subLevel + 1) + "): +" + currentInfo.subUpgradeDamageBonus + " ATK", 380, 380);
            } else {
                font.setColor(Color.CYAN);
                font.draw(batch, "NEXT TIER EVOLUTION: " + SwordData.TIERS[tier + 1].name + "!", 380, 380);
            }

            font.setColor(Color.GREEN);
            font.draw(batch, "Upgrade Cost: " + cost + " Gold   (Your Gold: " + gold + " G)", 380, 330);

            font.setColor(Color.WHITE);
            font.draw(batch, "[Y] Confirm Upgrade   |   [N] Exit", 430, 270);
        } else {
            font.setColor(Color.RED);
            font.draw(batch, "Current Weapon: ??? (Masamune)", 420, 460);
            font.setColor(Color.GOLD);
            font.draw(batch, "MAX TIER REACHED! Ultimate power unleashed.", 380, 400);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Press [N] or [ESC] to Exit", 450, 320);
        }

        font.getData().setScale(1.8f);
    }

    public boolean isActive() { return active; }
}
