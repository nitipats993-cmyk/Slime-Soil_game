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

    // --- Dodge Animation Variables ---
    private Texture slimerDodgeTexture;
    private Animation<TextureRegion> dodgeAnimation;

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

    // Sword Swinging
    private boolean isAttacking = false;
    private float attackTimer = 0.0f;
    private static final float ATTACK_DURATION = 0.20f; // 0.2s quick sword swing

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

            // Fast frame rate (0.08s) so the full animation finishes during the 0.35s dodge window
            dodgeAnimation = new Animation<>((float) 0.032, dFrame1, dFrame2, dFrame3, dFrame4, dFrame5, dFrame6, dFrame7, dFrame8, dFrame9, dFrame10);
        }

        // Equip a default tool to start
        currentTool = new Hoe();
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public void update(float delta, TiledMap currentMap, HUD hud) {
        // Time must always pass so walk, idle, and dodge animations can play!
        stateTime += delta;

        // Update player stats & timers (Mana, Shield, Dodge CD)
        PlayerStats.getInstance().update(delta);

        // Block player movement and action inputs while any UI Dialog is open
        if (hud != null) {
            if ((hud.getWarningDialog() != null && hud.getWarningDialog().isActive()) ||
                (hud.getSkillShopDialog() != null && hud.getSkillShopDialog().isActive()) ||
                (hud.getSwordShopDialog() != null && hud.getSwordShopDialog().isActive()) ||
                (hud.getTransitionDialog() != null && hud.getTransitionDialog().isActive())) {
                return;
            }
        }

        // --- Sword Attack Trigger (Press J or Left Click) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.J) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (!isAttacking) {
                isAttacking = true;
                attackTimer = 0.0f;
                executeSwordAttack();
            }
        }

        if (isAttacking) {
            attackTimer += delta;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
            }
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

        // Create a switch that resets to false at the start of every single frame
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

        // --- Tool & Seed Switching (Keys 1 through 7) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) currentTool = new Hoe();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) currentTool = new Seed("Tomato");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) currentTool = new Seed("GemFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) currentTool = new Seed("SkillFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) currentTool = new Seed("Carrot");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) currentTool = new Seed("Wheat");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) currentTool = new WateringCan();

        // 2. Apply movement ONLY if the predicted position is NOT solid
        if (!isSolid(nextX, y, currentMap)) {
            x = nextX;
        }
        if (!isSolid(x, nextY, currentMap)) {
            y = nextY;
        }

        // Use tool, harvest crop, sell items, or transition maps on SPACE key
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float feetX = x + (width / 2.0f);
            float feetY = y + 0.3f;

            int targetGridX = (int) Math.floor(feetX);
            int targetGridY = (int) Math.floor(feetY);

            // 1. Check Map Transitions (Prompt Warning Dialog)
            if (GameScreen.getInstance() != null) {
                MapTransition transition = GameScreen.getInstance().checkTransition(feetX, feetY);
                if (transition != null && hud != null) {
                    hud.getTransitionDialog().open(
                        transition.getTargetMap(),
                        transition.getSpawnX(),
                        transition.getSpawnY()
                    );
                    return;
                }
            }

            // 2. Check Shipping Bin selling area
            if (ShippingBin.getInstance().isBinLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    ShippingBin.getInstance().interact(hud.getWarningDialog());
                }
                return;
            }

            // 3. Check Skill Shop (Trainer NPC)
            if (SkillShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSkillShopDialog().open();
                }
                return;
            }

            // 4. Check Sword Shop (Blacksmith NPC)
            if (SwordShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSwordShopDialog().open();
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

    private void executeSwordAttack() {
        float feetX = x + (width / 2.0f);
        float feetY = y + 0.3f;

        float attackX = feetX;
        float attackY = feetY;
        if (currentDirection == Direction.UP) attackY += 1.0f;
        if (currentDirection == Direction.DOWN) attackY -= 1.0f;
        if (currentDirection == Direction.LEFT) attackX -= 1.0f;
        if (currentDirection == Direction.RIGHT) attackX += 1.0f;

        int totalDmg = PlayerStats.getInstance().getTotalAttackDamage();
        System.out.println("Attacked facing " + currentDirection + " dealing " + totalDmg + " damage!");
    }

    public void render(SpriteBatch batch) {
        boolean isMoving = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D);

        TextureRegion currentFrame;

        if (PlayerStats.getInstance().isInvulnerable() && dodgeAnimation != null) {
            currentFrame = dodgeAnimation.getKeyFrame(stateTime, false);
        } else if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        if (currentFrame.isFlipX() != isFacingRight) {
            currentFrame.flip(true, false);
        }

        batch.draw(currentFrame, x, y, width, height);

        if (isAttacking) {
            Texture swordTex = SwordData.getSwordTexture(PlayerStats.getInstance().getSwordTier());
            if (swordTex != null) {
                float progress = attackTimer / ATTACK_DURATION;
                float baseAngle = 0;
                if (currentDirection == Direction.UP) baseAngle = 90;
                if (currentDirection == Direction.DOWN) baseAngle = 270;
                if (currentDirection == Direction.LEFT) baseAngle = 180;
                if (currentDirection == Direction.RIGHT) baseAngle = 0;

                float swingAngle = baseAngle + (60.0f - (progress * 120.0f));

                float handX = x + (width / 2.0f) - 0.5f;
                float handY = y + (height / 2.0f) - 0.5f;

                batch.draw(
                    swordTex,
                    handX, handY,
                    0.5f, 0.5f,
                    1.5f, 1.5f,
                    1.0f, 1.0f,
                    swingAngle,
                    0, 0,
                    swordTex.getWidth(), swordTex.getHeight(),
                    false, false
                );
            }
        }
    }

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
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public String getCurrentToolName() {
        return (currentTool != null) ? currentTool.getName() : "None";
    }
}
