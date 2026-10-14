package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Player {
    // --- Player Variables ---
    private Texture slimerTexture;
    private Animation<TextureRegion> walkAnimation;

    // --- Idle Animation Variables ---
    private Texture slimerIdleTexture;
    private Animation<TextureRegion> idleAnimation;

    // --- Dodge Animation Variables ---
    private Texture slimerDodgeTexture;
    private Animation<TextureRegion> dodgeAnimation;

    // --- Overhead Pickup Text Font ---
    private BitmapFont pickupFont;

    private float stateTime = 0;

    private float x = 5;
    private float y = 5;
    private float speed = 4;
    private boolean isFacingRight = false;

    // Player size changed back to 2.0 for precise tile alignment
    private float width = (float) 2.0;
    private float height = (float) 2.0;

    // Player actions
    private Tool currentTool;
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction currentDirection = Direction.DOWN; // Default facing direction

    public Player() {
        // Load the sprite sheet with the explicit assets path
        slimerTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer.png"));

        int frameWidth = slimerTexture.getWidth() / 4;
        int frameHeight = slimerTexture.getHeight();

        // slicing: manually extracting each frame by shifting the X position
        TextureRegion frame1 = new TextureRegion(slimerTexture, 0, 0, frameWidth, frameHeight);
        TextureRegion frame2 = new TextureRegion(slimerTexture, frameWidth, 0, frameWidth, frameHeight);
        TextureRegion frame3 = new TextureRegion(slimerTexture, frameWidth * 2, 0, frameWidth, frameHeight);
        TextureRegion frame4 = new TextureRegion(slimerTexture, frameWidth * 3, 0, frameWidth, frameHeight);

        walkAnimation = new Animation<>((float) 0.15, frame1, frame2, frame3, frame4);

        // Load the Idle Animation (Updated for 2 frames)
        slimerIdleTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer_idle.png"));

        int idleFrameWidth = slimerIdleTexture.getWidth() / 2;
        int idleFrameHeight = slimerIdleTexture.getHeight();

        TextureRegion idle1 = new TextureRegion(slimerIdleTexture, 0, 0, idleFrameWidth, idleFrameHeight);
        TextureRegion idle2 = new TextureRegion(slimerIdleTexture, idleFrameWidth, 0, idleFrameWidth, idleFrameHeight);

        idleAnimation = new Animation<>((float) 0.4, idle1, idle2);

        // --- Load Dodge Animation ---
        if (Gdx.files.internal("assets/objects/myAssets/slimer_dodge.png").exists()) {
            slimerDodgeTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer_dodge.png"));

            int dodgeFrameWidth = slimerDodgeTexture.getWidth() / 6;
            int dodgeFrameHeight = slimerDodgeTexture.getHeight();

            TextureRegion dFrame1 = new TextureRegion(slimerDodgeTexture, 0, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame2 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame3 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 2, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame4 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 3, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame5 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 4, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame6 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 5, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame7 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 4, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame8 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 3, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame9 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth * 2, 0, dodgeFrameWidth, dodgeFrameHeight);
            TextureRegion dFrame10 = new TextureRegion(slimerDodgeTexture, dodgeFrameWidth, 0, dodgeFrameWidth, dodgeFrameHeight);

            dodgeAnimation = new Animation<>((float) 0.032, dFrame1, dFrame2, dFrame3, dFrame4, dFrame5, dFrame6, dFrame7, dFrame8, dFrame9, dFrame10);
        }

        // Initialize small overhead font scaled for world tile coordinates
        pickupFont = new BitmapFont();
        pickupFont.setUseIntegerPositions(false);
        pickupFont.getData().setScale((float) 0.035);
        pickupFont.setColor(Color.PURPLE);

        // Equip a default tool to start
        currentTool = new Hoe();
    }

    public void update(float delta, TiledMap currentMap, HUD hud) {
        // Time must always pass so walk, idle, and dodge animations can play!
        stateTime += delta;

        // Update player stats & timers (Mana, Shield, Dodge CD, Pickup Text Timer)
        PlayerStats.getInstance().update(delta);

        // Block player movement and action inputs while Warning Dialog is open
        if (hud != null && hud.getWarningDialog() != null && hud.getWarningDialog().isActive()) {
            return;
        }

        // --- Dodge Mechanic (Press Shift) ---
        float currentSpeed = speed;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            boolean dodged = PlayerStats.getInstance().performDodge();
            if (dodged) {
                stateTime = 0; // Reset animation time so dodge roll starts from Frame 1
                System.out.println("Dodged! (-5 Mana)");
            }
        }

        // Apply speed boost during active invulnerability frames (dodge roll)
        if (PlayerStats.getInstance().isInvulnerable()) {
            currentSpeed = speed * 2.2f;
        }

        boolean isMoving = false;

        // 1. Predict the next position based on input instead of moving immediately
        float nextX = x;
        float nextY = y;

        // Movement & Direction Tracking
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += currentSpeed * delta;
            currentDirection = Direction.UP;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= currentSpeed * delta;
            currentDirection = Direction.DOWN;
            isMoving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= currentSpeed * delta;
            isFacingRight = false;
            currentDirection = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += currentSpeed * delta;
            isFacingRight = true;
            currentDirection = Direction.RIGHT;
            isMoving = true;
        }

        // --- Tool Switching (Press 1, 2, 3, 4, or 5) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) currentTool = new Hoe();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) currentTool = new Seed("Tomato");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) currentTool = new Seed("GemFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) currentTool = new Seed("SkillFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) currentTool = new WateringCan();

        // 2. Apply movement ONLY if the predicted position is NOT solid
        if (!isSolid(nextX, y, currentMap)) {
            x = nextX;
        }
        if (!isSolid(x, nextY, currentMap)) {
            y = nextY;
        }

        // Use the tool, harvest crop, or sell items when SPACE key is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float feetX = x + (width / 2.0f);
            float feetY = y + 0.3f;

            int targetGridX = (int) Math.floor(feetX);
            int targetGridY = (int) Math.floor(feetY);

            // 4. Check if interacting with Shipping Bin selling area
            if (ShippingBin.getInstance().isBinLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    ShippingBin.getInstance().interact(hud.getWarningDialog());
                }
                return;
            }

            // 5. Try harvesting a crop first; if no fully grown crop is present, use equipped tool
            boolean harvested = CropManager.getInstance().harvestCrop(targetGridX, targetGridY);
            if (!harvested) {
                currentTool.use(this, targetGridX, targetGridY, currentMap);
            }
        }
    }

    public void render(SpriteBatch batch) {
        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);

        TextureRegion currentFrame;

        // 1. Check if dodging first
        if (PlayerStats.getInstance().isInvulnerable() && dodgeAnimation != null) {
            currentFrame = dodgeAnimation.getKeyFrame(stateTime, false);
        } else if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Flip frame horizontally depending on facing direction
        if (currentFrame.isFlipX() != isFacingRight) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, x, y, width, height);

        // Draw small seed pickup text above the player's head when active
        if (PlayerStats.getInstance().getPickupTextTimer() > 0) {
            pickupFont.draw(batch, PlayerStats.getInstance().getPickupText(), x - (float) 0.2, y + height + (float) 0.2);
        }
    }

    /**
     * Checks if a specific map coordinate has a tile on the "Collision" layer.
     */
    private boolean isSolid(float targetX, float targetY, TiledMap currentMap) {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) currentMap.getLayers().get("Collision");
        if (collisionLayer == null) return false;

        float hitboxWidth = 0.8f;
        float hitboxHeight = 0.8f;

        float left = targetX + (width / 2) - (hitboxWidth / 2);
        float right = targetX + (width / 2) + (hitboxWidth / 2);
        float bottom = targetY + 0.1f;
        float top = targetY + hitboxHeight;

        return isCellBlocked(collisionLayer, left, bottom) ||
            isCellBlocked(collisionLayer, right, bottom) ||
            isCellBlocked(collisionLayer, left, top) ||
            isCellBlocked(collisionLayer, right, top);
    }

    /**
     * Helper method: Asks the map if a specific X/Y coordinate has a painted tile.
     */
    private boolean isCellBlocked(TiledMapTileLayer layer, float x, float y) {
        int tileX = (int) Math.floor(x);
        int tileY = (int) Math.floor(y);
        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);
        return cell != null;
    }

    public void dispose() {
        if (slimerTexture != null) slimerTexture.dispose();
        if (slimerIdleTexture != null) slimerIdleTexture.dispose();
        if (slimerDodgeTexture != null) slimerDodgeTexture.dispose();
        if (pickupFont != null) pickupFont.dispose();
    }

    // --- Getters for the Camera ---
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    // --- Getters for HUD ---
    public String getCurrentToolName() {
        return (currentTool != null) ? currentTool.getName() : "None";
    }
}
