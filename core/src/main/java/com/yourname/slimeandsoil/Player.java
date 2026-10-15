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
    // slimer walk
    private Texture slimerTexture;
    private Animation<TextureRegion> walkAnimation;

    // smiler idle
    private Texture slimerIdleTexture;
    private Animation<TextureRegion> idleAnimation;

    // slimer_dodge
    private Texture slimerDodgeTexture;
    private Animation<TextureRegion> dodgeAnimation;

    // slimer-death
    private Texture slimerDeathTexture;
    private Animation<TextureRegion> deathAnimation;
    private float deathStateTime = 0.0f;

    //  notis text
    private BitmapFont pickupFont;

    private float stateTime = 0;

    private float x = 5;
    private float y = 5;
    private float speed = 4;
    private boolean isFacingRight = false;
    private float width = (float) 2.0;
    private float height = (float) 2.0;

    // movement+tool select update
    private Tool currentTool;
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    private Direction currentDirection = Direction.DOWN; // Default facing direction

    // swing sword update time
    private boolean isAttacking = false;
    private float attackTimer = 0.0f;
    private static final float ATTACK_DURATION = 0.20f; // 0.2s quick sword swing

    public Player() {
        slimerTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer.png"));
        int frameWidth = slimerTexture.getWidth() / 4; // 4 frames so divide by four
        int frameHeight = slimerTexture.getHeight();   // height stays the same

        TextureRegion frame1 = new TextureRegion(slimerTexture, 0, 0, frameWidth, frameHeight); //walk frame cycle
        TextureRegion frame2 = new TextureRegion(slimerTexture, frameWidth, 0, frameWidth, frameHeight);
        TextureRegion frame3 = new TextureRegion(slimerTexture, frameWidth * 2, 0, frameWidth, frameHeight);
        TextureRegion frame4 = new TextureRegion(slimerTexture, frameWidth * 3, 0, frameWidth, frameHeight);

        walkAnimation = new Animation<>((float) 0.15, frame1, frame2, frame3, frame4); // execute walk frames

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        slimerIdleTexture = new Texture(Gdx.files.internal("assets/objects/myAssets/slimer_idle.png"));
        int idleFrameWidth = slimerIdleTexture.getWidth() / 2;
        int idleFrameHeight = slimerIdleTexture.getHeight();

        TextureRegion idle1 = new TextureRegion(slimerIdleTexture, 0, 0, idleFrameWidth, idleFrameHeight);
        TextureRegion idle2 = new TextureRegion(slimerIdleTexture, idleFrameWidth, 0, idleFrameWidth, idleFrameHeight);

        idleAnimation = new Animation<>((float) 0.4, idle1, idle2);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

        // all warning and notis text settings
        pickupFont = new BitmapFont();
        pickupFont.setUseIntegerPositions(false);
        pickupFont.getData().setScale((float) 0.035);
        pickupFont.setColor(Color.PURPLE);

        // init tool to hoe (1)
        currentTool = new Hoe();
    }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    public void update(float delta, TiledMap currentMap, HUD hud) {
        //stop players input when dead
        if (PlayerStats.getInstance().isDead()) {
            deathStateTime += delta;
            return;
        }

        stateTime += delta;

        // update all player stats
        PlayerStats.getInstance().update(delta);

        // if warning dialog is opened stop players input
        if (hud != null) {
            if ((hud.getWarningDialog() != null && hud.getWarningDialog().isActive()) ||
                (hud.getSkillShopDialog() != null && hud.getSkillShopDialog().isActive()) ||
                (hud.getSwordShopDialog() != null && hud.getSwordShopDialog().isActive()) ||
                (hud.getTransitionDialog() != null && hud.getTransitionDialog().isActive())) {
                return;
            }
        }

        // skip to evening input
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

        // attack input
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

        // dodge input
        float currentSpeed = speed;
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            boolean dodged = PlayerStats.getInstance().performDodge();
            if (dodged) {
                stateTime = 0; // Reset animation time so dodge roll starts from Frame 1
                System.out.println("Dodged! (-5 Mana)");
            }
        }
        if (PlayerStats.getInstance().isInvulnerable()) {
            currentSpeed = speed * 2.2f;
        }
        boolean isMoving = false;
        float nextX = x;
        float nextY = y;
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
        if (!isSolid(nextX, y, currentMap)) {
            x = nextX;
        }
        if (!isSolid(x, nextY, currentMap)) {
            y = nextY;
        }

        // hotbar inputs
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) currentTool = new Hoe();
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) currentTool = new Seed("Tomato");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) currentTool = new Seed("GemFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) currentTool = new Seed("SkillFlower");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) currentTool = new Seed("Carrot");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) currentTool = new Seed("Wheat");
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) currentTool = new WateringCan();

        // interact input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float feetX = x + (width / 2.0f);
            float feetY = y + 0.3f;

            int targetGridX = (int) Math.floor(feetX);
            int targetGridY = (int) Math.floor(feetY);

            // next day interaction check/farm wave check
            if (GameScreen.getInstance() != null && GameScreen.getInstance().isHouseBedLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    float currentHour = TimeManager.getInstance().getCurrentHour();

                    // restrict sleeping before 6 PM
                    if (currentHour >= 6.0f && currentHour < 18.0f) {
                        PlayerStats.getInstance().showPickupText("You can only sleep after 6:00 PM!");
                        return;
                    }

                    // warning: slimes alive text
                    if (WaveManager.getInstance().getAliveSlimeCount() > 0) {
                        hud.getWarningDialog().showDialog("Slimes are attacking! Sleeping will destroy all crops. Sleep anyway?", new Runnable() {
                            @Override
                            public void run() {
                                TimeManager.getInstance().sleepToNextDay();
                            }
                        });
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

            // map transition check
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

            // sell shop interaction box check
            if (ShippingBin.getInstance().isBinLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    ShippingBin.getInstance().interact(hud.getWarningDialog());
                }
                return;
            }

            // skillshop interaction box check
            if (SkillShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSkillShopDialog().open();
                }
                return;
            }

            // swordshop interaction box check
            if (SwordShop.getInstance().isShopLocation(targetGridX, targetGridY)) {
                if (hud != null) {
                    hud.getSwordShopDialog().open();
                }
                return;
            }

            // tool use check
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

        //check if sword range hit slime, if hit apply dmg to slime
        for (Slime slime : WaveManager.getInstance().getSlimes()) {
            if (slime.getStats().isDead() || slime.getStats().isSpawning()) continue;

            float slimeCenterX = slime.getX() + (slime.getWidth() / 2.0f);
            float slimeCenterY = slime.getY() + (slime.getHeight() / 2.0f);

            if (Math.abs(slimeCenterX - attackX) < 1.5f && Math.abs(slimeCenterY - attackY) < 1.5f) {
                slime.takeDamage(totalDmg);
            }
        }
    }

    //load player actions
    public void render(SpriteBatch batch) {
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

        //player anim
        if (PlayerStats.getInstance().isInvulnerable() && dodgeAnimation != null) {
            currentFrame = dodgeAnimation.getKeyFrame(stateTime, false);
        } else if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // flip sprite when turning
        if (currentFrame.isFlipX() != isFacingRight) {
            currentFrame.flip(true, false);
        }
        batch.draw(currentFrame, x, y, width, height);

        // seed pickup text
        if (PlayerStats.getInstance().getPickupTextTimer() > 0) {
            pickupFont.draw(batch, PlayerStats.getInstance().getPickupText(), x - (float) 0.2, y + height + (float) 0.2);
        }

        // attack
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

    //collision
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
