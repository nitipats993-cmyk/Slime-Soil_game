package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WarningDialog {

    private boolean active = false;
    private String title = "";
    private String message = "";
    private Runnable confirmAction;

    public void show(String title, String message, Runnable onConfirm) {
        this.title = title;
        this.message = message;
        this.confirmAction = onConfirm;
        this.active = true;
    }

    public void update() {
        if (!active) return;

        // Press 'Y' to Confirm
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            if (confirmAction != null) {
                confirmAction.run();
            }
            active = false;
        }

        // Press 'N' or 'ESCAPE' to Cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Action cancelled.");
            active = false;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active) return;

        // Draw Warning Box Text in Center of Screen (1280x720 Viewport Space)
        font.getData().setScale(2.0f);
        font.setColor(Color.RED);
        font.draw(batch, "=== " + title + " ===", 480, 450);

        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);
        font.draw(batch, message, 380, 390);

        font.setColor(Color.WHITE);
        font.draw(batch, "[Y] Confirm / Sell   |   [N] Cancel", 430, 330);

        font.getData().setScale(1.8f); // Reset font scale
    }

    public boolean isActive() {
        return active;
    }

    public void close() {
        active = false;
    }
}
