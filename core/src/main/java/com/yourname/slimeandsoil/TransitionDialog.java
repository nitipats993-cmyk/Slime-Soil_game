package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TransitionDialog {

    private boolean active = false;
    private String targetMap;
    private float spawnX;
    private float spawnY;
    private boolean isDungeonEntry;
    private boolean isBlocked;

    public void open(String targetMap, float spawnX, float spawnY) {
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.isDungeonEntry = targetMap.contains("untitled.tmx");

        // Check if dungeon limit has been reached for today
        if (isDungeonEntry && TimeManager.getInstance().hasEnteredDungeonToday()) {
            this.isBlocked = true;
        } else {
            this.isBlocked = false;
        }

        this.active = true;
    }

    public void update() {
        if (!active) return;

        if (isBlocked) {
            // Player already entered dungeon today; press N or ESC to close
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.N) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                active = false;
            }
            return;
        }

        // Press Y to confirm map transition
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            active = false;
            if (isDungeonEntry) {
                TimeManager.getInstance().setEnteredDungeonToday(true);
            }
            GameScreen.getInstance().loadMap(targetMap, spawnX, spawnY);
        }

        // Press N or ESC to cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            active = false;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active) return;

        font.getData().setScale(2.0f);
        font.setColor(Color.GOLD);

        if (isBlocked) {
            font.draw(batch, "=== DUNGEON RESTRICTION ===", 380, 520);
            font.getData().setScale(1.4f);
            font.setColor(Color.RED);
            font.draw(batch, "You have already entered the Forest Dungeon today!", 350, 440);
            font.setColor(Color.WHITE);
            font.draw(batch, "Rest in bed to enter again tomorrow.", 420, 390);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Press [N] or [ESC] to Close", 460, 310);
        } else if (isDungeonEntry) {
            font.draw(batch, "=== ENTER FOREST DUNGEON ===", 370, 520);
            font.getData().setScale(1.4f);
            font.setColor(Color.YELLOW);
            font.draw(batch, "Enter the forest to fight slime waves?", 420, 450);
            font.setColor(Color.ORANGE);
            font.draw(batch, "Warning: You can only enter ONCE per day!", 390, 400);
            font.setColor(Color.WHITE);
            font.draw(batch, "[Y] Enter Dungeon   |   [N] Stay Here", 430, 320);
        } else {
            font.draw(batch, "=== RETURN TO FARM ===", 430, 520);
            font.getData().setScale(1.4f);
            font.setColor(Color.YELLOW);
            font.draw(batch, "Leave the forest and return home to your farm?", 360, 430);
            font.setColor(Color.WHITE);
            font.draw(batch, "[Y] Return Home   |   [N] Stay in Forest", 420, 340);
        }

        font.getData().setScale(1.8f);
    }

    public boolean isActive() { return active; }
}
