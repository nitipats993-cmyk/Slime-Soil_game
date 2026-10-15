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
    private final SkillShopDialog skillShopDialog;
    private final SwordShopDialog swordShopDialog;
    private final TransitionDialog transitionDialog;

    public HUD() {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(1280, 720, hudCamera);
        hudViewport.apply();

        font = new BitmapFont();
        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);

        warningDialog = new WarningDialog();
        skillShopDialog = new SkillShopDialog();
        swordShopDialog = new SwordShopDialog();
        transitionDialog = new TransitionDialog();
    }

    public void render(SpriteBatch batch, Player player) {
        warningDialog.update();
        skillShopDialog.update();
        swordShopDialog.update();
        transitionDialog.update();

        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();

        //clock and the day
        String timeString = formatTime(
            TimeManager.getInstance().getCurrentHour(),
            TimeManager.getInstance().getCurrentDay()
        );
        font.setColor(Color.YELLOW);
        font.draw(batch, timeString, 25, 695);

        //tool display
        font.setColor(Color.WHITE);
        font.draw(batch, "Tool: " + player.getCurrentToolName(), 25, 655);

        //wave display
        WaveManager wm = WaveManager.getInstance();
        if (wm.getCurrentWave() == 0) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "Wave: Not Started (Press [M] to Start Wave 1)", 25, 615);
        } else {
            int alive = wm.getAliveSlimeCount();
            String skipStatus;
            if (alive == 0) {
                font.setColor(Color.GREEN);
                skipStatus = "Wave Clear! Press [M] for Next Wave";
            } else if (wm.getWaveTimer() <= 0) {
                font.setColor(Color.ORANGE);
                skipStatus = "Press [M] to Skip to Next Wave";
            } else {
                font.setColor(Color.LIGHT_GRAY);
                skipStatus = String.format("Next Wave Skip in %ds", (int) Math.ceil(wm.getWaveTimer()));
            }
            font.draw(batch, String.format("Wave %d  |  Slimes: %d  |  %s", wm.getCurrentWave(), alive, skipStatus), 25, 615);
        }

        // health,armor,mana
        PlayerStats stats = PlayerStats.getInstance();
        font.setColor(Color.RED);
        font.draw(batch, "HP: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth(), 380, 695);

        font.setColor(Color.CYAN);
        font.draw(batch, "Shield: " + stats.getCurrentShield() + "/" + stats.getMaxShield(), 520, 695);

        font.setColor(Color.BLUE);
        font.draw(batch, "Mana: " + stats.getCurrentMana() + "/" + stats.getMaxMana(), 680, 695);

        // gold & skillpts
        int gold = Inventory.getInstance().getGold();
        int skillPts = stats.getSkillPoints();

        font.setColor(Color.GOLD);
        font.draw(batch, "Gold: " + gold + " G", 1080, 695);

        font.setColor(Color.GREEN);
        font.draw(batch, "Skill: " + skillPts, 1080, 655);

        // hotbars select display
        font.getData().setScale(1.1f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[1] Hoe  [2] Tomato  [3] Gem  [4] Skill  [5] Carrot  [6] Wheat  [7] Can  |  [Space] Action  [Shift] Dodge  [M] Wave  [R] Rest 18:00", 20, 35);
        font.getData().setScale(1.8f);

        // dead screen
        if (stats.isDead()) {
            font.getData().setScale(3.0f);
            font.setColor(Color.RED);
            font.draw(batch, "=== GAME OVER ===", 420, 420);
            font.getData().setScale(1.6f);
            font.setColor(Color.WHITE);
            font.draw(batch, "You were defeated by slimes! Restart game to continue.", 330, 340);
        } else if (warningDialog.isActive()) {
            warningDialog.render(batch, font);
        } else if (skillShopDialog.isActive()) {
            skillShopDialog.render(batch, font);
        } else if (swordShopDialog.isActive()) {
            swordShopDialog.render(batch, font);
        } else if (transitionDialog.isActive()) {
            transitionDialog.render(batch, font);
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

    public WarningDialog getWarningDialog() { return warningDialog; }
    public SkillShopDialog getSkillShopDialog() { return skillShopDialog; }
    public SwordShopDialog getSwordShopDialog() { return swordShopDialog; }
    public TransitionDialog getTransitionDialog() { return transitionDialog; }

    public void dispose() {
        font.dispose();
    }
}
