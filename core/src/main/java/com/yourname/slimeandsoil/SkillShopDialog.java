package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SkillShopDialog {

    private boolean active = false;

    public void open() {
        this.active = true;
    }

    public void update() {
        if (!active) return;

        PlayerStats stats = PlayerStats.getInstance();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            stats.upgradeMaxHealth();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            stats.upgradeMaxShield();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            stats.upgradeMaxMana();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            stats.upgradeAtkMultiplier();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            active = false;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active) return;

        PlayerStats stats = PlayerStats.getInstance();

        font.getData().setScale(2.0f);
        font.setColor(Color.GOLD);
        font.draw(batch, "=== SKILL TRAINER SHOP ===", 420, 520);

        font.getData().setScale(1.4f);
        font.setColor(Color.CYAN);
        font.draw(batch, "Available Skill Points: " + stats.getSkillPoints(), 480, 470);

        font.setColor(Color.WHITE);
        font.draw(batch, "[1] +1 Max HP (" + stats.getMaxHealth() + "/20) - Cost: 1 Skill Pt", 380, 420);
        font.draw(batch, "[2] +1 Max Shield (" + stats.getMaxShield() + "/10) - Cost: 1 Skill Pt", 380, 380);
        font.draw(batch, "[3] +1 Max Mana (" + stats.getMaxMana() + "/25) - Cost: 1 Skill Pt", 380, 340);
        font.draw(batch, String.format("[4] +0.15x ATK Multiplier (%.2fx/2.50x) - Cost: 1 Skill Pt", stats.getAtkMultiplier()), 380, 300);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Press [N] or [ESC] to Close", 480, 240);

        font.getData().setScale(1.8f);
    }

    public boolean isActive() { return active; }
}
