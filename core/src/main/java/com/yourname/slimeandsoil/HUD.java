package com.yourname.slimeandsoil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD {

    private final OrthographicCamera hudCamera;
    private final Viewport hudViewport;
    private final BitmapFont font;
    private final WarningDialog warningDialog;

    public HUD() {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(1280, 720, hudCamera);
        hudViewport.apply();

        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);

        warningDialog = new WarningDialog();
    }

    public void render(SpriteBatch batch, Player player) {
        warningDialog.update();

        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();

        // 1. Time & Day Clock (Top Left)
        String timeString = formatTime(
            TimeManager.getInstance().getCurrentHour(),
            TimeManager.getInstance().getCurrentDay()
        );
        font.setColor(Color.YELLOW);
        font.draw(batch, timeString, 25, 695);

        // 2. Tool Display
        font.setColor(Color.WHITE);
        font.draw(batch, "Tool: " + player.getCurrentToolName(), 25, 655);

        // 3. Wave Status & Skip Timer Display (Top Left below Tool)
        WaveManager wm = WaveManager.getInstance();
        if (wm.getCurrentWave() == 0) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "Wave: Not Started (Press [N] to Start Wave 1)", 25, 615);
        } else {
            int alive = wm.getAliveSlimeCount();
            String skipStatus;
            if (alive == 0) {
                font.setColor(Color.GREEN);
                skipStatus = "Wave Clear! Press [N] for Next Wave";
            } else if (wm.getWaveTimer() <= 0) {
                font.setColor(Color.ORANGE);
                skipStatus = "Press [N] to Skip to Next Wave";
            } else {
                font.setColor(Color.LIGHT_GRAY);
                skipStatus = String.format("Next Wave Skip in %ds", (int) Math.ceil(wm.getWaveTimer()));
            }
            font.draw(batch, String.format("Wave %d  |  Slimes: %d  |  %s", wm.getCurrentWave(), alive, skipStatus), 25, 615);
        }

        // 4. Combat Stats Gauges (Top Center)
        PlayerStats stats = PlayerStats.getInstance();
        font.setColor(Color.RED);
        font.draw(batch, "HP: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth(), 380, 695);

        font.setColor(Color.CYAN);
        font.draw(batch, "Shield: " + stats.getCurrentShield() + "/" + stats.getMaxShield(), 520, 695);

        font.setColor(Color.BLUE);
        font.draw(batch, "Mana: " + stats.getCurrentMana() + "/" + stats.getMaxMana(), 680, 695);

        // 5. Gold & Crop Inventory Trackers (Top Right)
        int gold = Inventory.getInstance().getGold();
        int tomatoes = Inventory.getInstance().getItemCount("Tomato");
        int gemFlowers = Inventory.getInstance().getItemCount("GemFlower");
        int skillFlowers = Inventory.getInstance().getItemCount("SkillFlower");

        font.setColor(Color.GOLD);
        font.draw(batch, "Gold: " + gold + " G", 1080, 695);

        font.setColor(Color.GREEN);
        font.draw(batch, String.format("Tomato: %d | Gem: %d | Skill: %d", tomatoes, gemFlowers, skillFlowers), 830, 655);

        // 6. Hotbar Controls Guide
        font.getData().setScale(1.2f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[1] Hoe  [2] Tomato Seed  [3] Gem Seed  [4] Skill Seed  [5] Can  |  [Space] Action  [Shift] Dodge  [N] Next Wave  [M] Sleep", 25, 35);
        font.getData().setScale(1.8f);

        // 7. Render Warning Dialog Overlay
        if (warningDialog.isActive()) {
            warningDialog.render(batch, font);
        }

        batch.end();
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    private String formatTime(float currentHour, int currentDay) {
        int hours = (int) currentHour;
        int minutes = (int) ((currentHour - hours) * 60);
        String ampm = (hours >= 12) ? "PM" : "AM";
        int displayHour = hours % 12;
        if (displayHour == 0) displayHour = 12;

        return String.format("Day %d  |  %02d:%02d %s", currentDay, displayHour, minutes, ampm);
    }

    public WarningDialog getWarningDialog() {
        return warningDialog;
    }

    public void dispose() {
        font.dispose();
    }
}
