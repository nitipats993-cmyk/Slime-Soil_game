package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    private float stateTime = 0; // Removed the 'f' for whole numbers

    // Removed the 'f' suffix; Java handles whole numbers automatically
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

        // Pass the individual frames directly into the Animation, using (float) for the decimal
        walkAnimation = new Animation<>((float) 0.15, frame1, frame2, frame3, frame4);

        // Load the Idle Animation (Updated for 2 frames)
        slimerIdleTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer_idle.png"));

        // Divide by 2 because there are only 2 frames in the image
        int idleFrameWidth = slimerIdleTexture.getWidth() / 2;
        int idleFrameHeight = slimerIdleTexture.getHeight();

        TextureRegion idle1 = new TextureRegion(slimerIdleTexture, 0, 0, idleFrameWidth, idleFrameHeight);
        TextureRegion idle2 = new TextureRegion(slimerIdleTexture, idleFrameWidth, 0, idleFrameWidth, idleFrameHeight);

        // I slowed the idle animation down to 0.4 seconds per frame so it looks like relaxed breathing
        idleAnimation = new Animation<>((float) 0.4, idle1, idle2);

        // Equip a default tool to start
        currentTool = new Hoe();
    }

    public void update(float delta, TiledMap currentMap, HUD hud) {
        // Time must always pass so both walk and idle animations can play!
        stateTime += delta;

        // Block player movement and action inputs while Warning Dialog is open
        if (hud != null && hud.getWarningDialog() != null && hud.getWarningDialog().isActive()) {
            return;
        }

        // Create a switch that resets to false at the start of every single frame
        boolean isMoving = false;

        // 1. Predict the next position based on input instead of moving immediately
        float nextX = x;
        float nextY = y;

        // Movement & Direction Tracking
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += speed * delta;
            currentDirection = Direction.UP;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= speed * delta;
            currentDirection = Direction.DOWN;
            isMoving = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= speed * delta;
            isFacingRight = false;
            currentDirection = Direction.LEFT;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += speed * delta;
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
        // Checking X and Y separately allows the player to slide smoothly along walls
        if (!isSolid(nextX, y, currentMap)) {
            x = nextX;
        }
        if (!isSolid(x, nextY, currentMap)) {
            y = nextY;
        }

        // Use the tool, harvest crop, or sell items when SPACE key is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Target the exact grid tile directly underneath the cat's feet (bottom-center)
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
        // Determine if moving based on current key presses for the animation state
        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);

        TextureRegion currentFrame;
        // If moving, play the walk animation. If standing still, play the idle animation!
        if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Now it will properly flip when isFacingRight is true
        if (currentFrame.isFlipX() != isFacingRight) {
            currentFrame.flip(true, false);
        }

        // We don't call batch.begin() here because GameScreen manages the batch
        batch.draw(currentFrame, x, y, width, height);
    }

    /**
     * Checks if a specific map coordinate has a tile on the "Collision" layer.
     */
    private boolean isSolid(float targetX, float targetY, TiledMap currentMap) {
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) currentMap.getLayers().get("Collision");
        if (collisionLayer == null) return false;

        // Hitbox scaled to fit 2.0x2.0 character
        float hitboxWidth = 0.8f;  // 1 tile wide
        float hitboxHeight = 0.8f; // slightly shorter than 1 tile high

        // Calculate the 4 edges of the hitbox based on the player's center
        float left = targetX + (width / 2) - (hitboxWidth / 2);
        float right = targetX + (width / 2) + (hitboxWidth / 2);
        float bottom = targetY + 0.1f; // slightly above the absolute bottom pixel
        float top = targetY + hitboxHeight;

        // Check all 4 corners of the hitbox. If ANY of them touch a red tile, block movement.
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
