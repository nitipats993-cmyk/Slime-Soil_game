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

    // --- Death Animation/Sprite Variables ---
    private Texture slimerDeathTexture;
    private Animation<TextureRegion> deathAnimation;
    private float deathStateTime = 0.0f;

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

        // --- Load Death Texture ---
        String deathPath = null;
        if (Gdx.files.internal("assets/objects/myAssets/slimer_death.png").exists()) {
            deathPath = "assets/objects/myAssets/slimer_death.png";
        } else if (Gdx.files.internal("assets/objects/myAssets/slimer_dead.png").exists()) {
            deathPath = "assets/objects/myAssets/slimer_dead.png";
        }

        if (deathPath != null) {
            slimerDeathTexture = new Texture(Gdx.files.internal(deathPath));

            int dFrameWidth = slimerDeathTexture.getWidth() / 5;
            int dFrameHeight = slimerDeathTexture.getHeight();

            TextureRegion df1 = new TextureRegion(slimerDeathTexture, 0, 0, dFrameWidth, dFrameHeight);
            TextureRegion df2 = new TextureRegion(slimerDeathTexture, dFrameWidth, 0, dFrameWidth, dFrameHeight);
            TextureRegion df3 = new TextureRegion(slimerDeathTexture, dFrameWidth * 2, 0, dFrameWidth, dFrameHeight);
            TextureRegion df4 = new TextureRegion(slimerDeathTexture, dFrameWidth * 3, 0, dFrameWidth, dFrameHeight);
            TextureRegion df5 = new TextureRegion(slimerDeathTexture, dFrameWidth * 4, 0, dFrameWidth, dFrameHeight);

            deathAnimation = new Animation<>((float) 0.12, df1, df2, df3, df4, df5);
            deathAnimation.setPlayMode(Animation.PlayMode.NORMAL); // Hold on final puddle frame
        }

        // Initialize small overhead font scaled for world tile coordinates
        pickupFont = new BitmapFont();
        pickupFont.setUseIntegerPositions(false);
        pickupFont.getData().setScale((float) 0.035);
        pickupFont.setColor(Color.PURPLE);

        // Equip a default tool to start
        currentTool = new Hoe();
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public void update(float delta, TiledMap currentMap, HUD hud) {
        // Stop updating player controls when dead
        if (PlayerStats.getInstance().isDead()) {
            deathStateTime += delta;
            return;
        }

        stateTime += delta;

        // Update player stats & timers (Mana, Shield, Dodge CD, Pickup Text Timer)
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

        // --- Press R to Fast-Forward Daytime to 18:00 (Start of Farm Defense) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (TimeManager.getInstance().isInArena()) {
                PlayerStats.getInstance().showPickupText("Cannot skip to 6:00 PM in the Arena!");
            } else if (hud != null && TimeManager.getInstance().getCurrentHour() < 18.0f && TimeManager.getInstance().getCurrentHour() >= 6.0f) {
                hud.getWarningDialog().showDialog("Skip to 18:00 PM (Start of Farm Defense)?", new Runnable() {
                    @Override
                    public void run() {
                        TimeManager.getInstance().skipToEvening();
                    }
                });
            } else {
                PlayerStats.getInstance().showPickupText("Nighttime! Go to house door to sleep.");
            }
            return;
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

        // Use tool, harvest crop, sell items, enter house/bed, or transition maps on SPACE key
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float feetX = x + (width / 2.0f);
            float feetY = y + 0.3f;

            int targetGridX = (int) Math.floor(feetX);
            int targetGridY = (int) Math.floor(feetY);

            // 1. Check House Door Bed Area (Sleep to Next Day Prompt)
            if (GameScreen.getInstance() != null && GameScreen.getInstance().isHouseBedLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    if (WaveManager.getInstance().getAliveSlimeCount() > 0) {
                        hud.getWarningDialog().showDialog("Clear the slimes attacking your farm before sleeping!", null);
                    } else {
                        hud.getWarningDialog().showDialog("Are you ready to go to the next day?", new Runnable() {
                            @Override
                            public void run() {
                                TimeManager.getInstance().sleepToNextDay();
                            }
                        });
                    }
                }
                return;
            }

            // 2. Check Map Transitions (Prompt Warning Dialog)
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

            // 3. Check Shipping Bin selling area
            if (ShippingBin.getInstance().isBinLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    ShippingBin.getInstance().interact(hud.getWarningDialog());
                }
                return;
            }

            // 4. Check Skill Shop (Trainer NPC)
            if (SkillShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSkillShopDialog().open();
                }
                return;
            }

            // 5. Check Sword Shop (Blacksmith NPC)
            if (SwordShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSwordShopDialog().open();
                }
                return;
            }

            // 6. Try harvesting a crop first; if no fully grown crop is present, use equipped tool
            boolean harvested = CropManager.getInstance().harvestCrop(targetGridX, targetGridY);
            if (!harvested) {
                currentTool.use(this, targetGridX, targetGridY, currentMap);
            }
        }
    }

    /**
     * Executes a sword attack and damages active slimes in front of the player.
     */
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

        // --- Damage active slimes in sword hit zone ---
        for (Slime slime : WaveManager.getInstance().getSlimes()) {
            if (slime.getStats().isDead() || slime.getStats().isSpawning()) continue;

            float slimeCenterX = slime.getX() + (slime.getWidth() / 2.0f);
            float slimeCenterY = slime.getY() + (slime.getHeight() / 2.0f);

            if (Math.abs(slimeCenterX - attackX) < 1.5f && Math.abs(slimeCenterY - attackY) < 1.5f) {
                slime.takeDamage(totalDmg);
            }
        }
    }

    public void render(SpriteBatch batch) {
        // --- Game Over State: Player Death ---
        if (PlayerStats.getInstance().isDead()) {
            if (deathAnimation != null) {
                TextureRegion currentDeathFrame = deathAnimation.getKeyFrame(deathStateTime, false);
                if (currentDeathFrame.isFlipX() != isFacingRight) {
                    currentDeathFrame.flip(true, false);
                }
                batch.draw(currentDeathFrame, x, y, width, height);
            } else if (slimerDeathTexture != null) {
                batch.draw(slimerDeathTexture, x, y, width, height);
            } else {
                TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
                batch.draw(currentFrame, x, y, width, height);
            }
            return;
        }

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

        // Draw Procedural Sword Swing Arc if attacking
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
                    0.5f, 0.5f,          // Origin pivot at sword hilt
                    1.5f, 1.5f,          // Sword render size
                    1.0f, 1.0f,          // Scale
                    swingAngle,          // Procedural rotation angle
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
        if (slimerDeathTexture != null) slimerDeathTexture.dispose();
        if (pickupFont != null) pickupFont.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public String getCurrentToolName() {
        return (currentTool != null) ? currentTool.getName() : "None";
    }
}
