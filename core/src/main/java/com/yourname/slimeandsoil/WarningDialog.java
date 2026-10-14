package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WarningDialog {

    private boolean active = false;
    private String title = "=== CONFIRMATION ===";
    private String message = "";
    private Runnable onConfirmAction;

    /**
     * 3-parameter method for custom titles (e.g., ShippingBin)
     */
    public void show(String title, String message, Runnable onConfirmAction) {
        this.title = title;
        this.message = message;
        this.onConfirmAction = onConfirmAction;
        this.active = true;
    }

    /**
     * 2-parameter method for default title prompts (e.g., Player actions)
     */
    public void showDialog(String message, Runnable onConfirmAction) {
        show("=== CONFIRMATION ===", message, onConfirmAction);
    }

    public void update() {
        if (!active) return;

        // Press Y to confirm action
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            active = false;
            if (onConfirmAction != null) {
                onConfirmAction.run();
            }
        }

        // Press N or ESC to cancel
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            active = false;
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        if (!active) return;

        font.getData().setScale(2.0f);
        font.setColor(Color.GOLD);
        font.draw(batch, title, 420, 500);

        font.getData().setScale(1.4f);
        font.setColor(Color.WHITE);
        font.draw(batch, message, 320, 430);

        font.setColor(Color.YELLOW);
        font.draw(batch, "[Y] Proceed   |   [N] Cancel", 430, 350);

        font.getData().setScale(1.8f);
    }

    public boolean isActive() {
        return active;
    }
}
