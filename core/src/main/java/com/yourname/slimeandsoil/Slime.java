package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;

public class Slime {
    // --- Slime Variables ---
    private Texture slimeTexture;
    private Animation<TextureRegion> walkAnimation;

    // --- Idle Animation Variables ---
    private Animation<TextureRegion> idleAnimation;

    // --- Death Animation Variables ---
    private Animation<TextureRegion> deathAnimation;

    // --- Spawn & Warning Box Animation Variables ---
    private Texture warningTexture;
    private Animation<TextureRegion> warningAnimation;
    private Animation<TextureRegion> spawnAnimation;

    private float stateTime = 0;

    private float x = 5;
    private float y = 5;
    private float speed = 2;
    private boolean isFacingRight = false;
    private boolean isMoving = false;

    // Slime size set to 2.0 for precise tile alignment
    private float width = (float) 2.0;
    private float height = (float) 2.0;

    // --- Slime Stats & Color Reskin ---
    private SlimeStats stats;
    private Color slimeColor;

    public Slime(float startX, float startY, int currentWave) {
        this.x = startX;
        this.y = startY;
        this.stats = new SlimeStats(currentWave);
        this.speed = stats.getSpeed();

        // Randomize slime color reskin
        this.slimeColor = new Color(
            MathUtils.random((float) 0.4, (float) 1.0),
            MathUtils.random((float) 0.4, (float) 1.0),
            MathUtils.random((float) 0.4, (float) 1.0),
            (float) 1.0
        );

        // --- Load Warning Box Animation (2 frames) ---
        warningTexture = new Texture(Gdx.files.internal("assets/objects/Enemies/warning-box.png"));

        int warnFrameWidth = warningTexture.getWidth() / 2;
        int warnFrameHeight = warningTexture.getHeight();

        TextureRegion warn1 = new TextureRegion(warningTexture, 0, 0, warnFrameWidth, warnFrameHeight);
        TextureRegion warn2 = new TextureRegion(warningTexture, warnFrameWidth, 0, warnFrameWidth, warnFrameHeight);

        warningAnimation = new Animation<>((float) 0.15, warn1, warn2);

        // --- Load Slime Sprite Sheet (8 columns x 4 rows) ---
        slimeTexture = new Texture(Gdx.files.internal("assets/objects/Enemies/Slime_Green.png"));

        int frameWidth = slimeTexture.getWidth() / 8;
        int frameHeight = slimeTexture.getHeight() / 4;

        // Slicing Row 1 (Y = 0): Idle Animation (4 frames)
        TextureRegion idle1 = new TextureRegion(slimeTexture, 0, 0, frameWidth, frameHeight);
        TextureRegion idle2 = new TextureRegion(slimeTexture, frameWidth, 0, frameWidth, frameHeight);
        TextureRegion idle3 = new TextureRegion(slimeTexture, frameWidth * 2, 0, frameWidth, frameHeight);
        TextureRegion idle4 = new TextureRegion(slimeTexture, frameWidth * 3, 0, frameWidth, frameHeight);

        idleAnimation = new Animation<>((float) 0.2, idle1, idle2, idle3, idle4);

        // Slicing Row 2 (Y = frameHeight): Walk / Jump Animation (8 frames)
        TextureRegion walk1 = new TextureRegion(slimeTexture, 0, frameHeight, frameWidth, frameHeight);
        TextureRegion walk2 = new TextureRegion(slimeTexture, frameWidth, frameHeight, frameWidth, frameHeight);
        TextureRegion walk3 = new TextureRegion(slimeTexture, frameWidth * 2, frameHeight, frameWidth, frameHeight);
        TextureRegion walk4 = new TextureRegion(slimeTexture, frameWidth * 3, frameHeight, frameWidth, frameHeight);
        TextureRegion walk5 = new TextureRegion(slimeTexture, frameWidth * 4, frameHeight, frameWidth, frameHeight);
        TextureRegion walk6 = new TextureRegion(slimeTexture, frameWidth * 5, frameHeight, frameWidth, frameHeight);
        TextureRegion walk7 = new TextureRegion(slimeTexture, frameWidth * 6, frameHeight, frameWidth, frameHeight);
        TextureRegion walk8 = new TextureRegion(slimeTexture, frameWidth * 7, frameHeight, frameWidth, frameHeight);

        walkAnimation = new Animation<>((float) 0.12, walk1, walk2, walk3, walk4, walk5, walk6, walk7, walk8);

        // Slicing Row 3 (Y = frameHeight * 2): Death Animation (8 frames)
        TextureRegion death1 = new TextureRegion(slimeTexture, 0, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death2 = new TextureRegion(slimeTexture, frameWidth, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death3 = new TextureRegion(slimeTexture, frameWidth * 2, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death4 = new TextureRegion(slimeTexture, frameWidth * 3, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death5 = new TextureRegion(slimeTexture, frameWidth * 4, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death6 = new TextureRegion(slimeTexture, frameWidth * 5, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death7 = new TextureRegion(slimeTexture, frameWidth * 6, frameHeight * 2, frameWidth, frameHeight);
        TextureRegion death8 = new TextureRegion(slimeTexture, frameWidth * 7, frameHeight * 2, frameWidth, frameHeight);

        deathAnimation = new Animation<>((float) 0.08, death1, death2, death3, death4, death5, death6, death7, death8);

        // Slicing Row 4 (Y = frameHeight * 3): Spawn Animation (3 frames, 0.2s per frame = 0.6s total)
        TextureRegion spawn1 = new TextureRegion(slimeTexture, 0, frameHeight * 3, frameWidth, frameHeight);
        TextureRegion spawn2 = new TextureRegion(slimeTexture, frameWidth, frameHeight * 3, frameWidth, frameHeight);
        TextureRegion spawn3 = new TextureRegion(slimeTexture, frameWidth * 2, frameHeight * 3, frameWidth, frameHeight);

        spawnAnimation = new Animation<>((float) 0.2, spawn1, spawn2, spawn3);
    }

    public void update(float delta, Player player, TiledMap currentMap) {
        // Time must always pass so warning, spawn, walk, idle, and death animations can play
        stateTime += delta;

        // Update slime stats & timers (Spawn Timer & Attack Cooldown)
        stats.update(delta);

        // Do not move or attack while spawning or dead
        if (stats.isSpawning() || stats.isDead()) {
            return;
        }

        isMoving = false;

        // 1. Unstuck check: if already overlapping another slime, push them apart
        for (Slime other : WaveManager.getInstance().getSlimes()) {
            if (other == this || other.getStats().isDead() || other.getStats().isSpawning()) continue;

            if (Math.abs(x - other.getX()) < (float) 0.7 && Math.abs(y - other.getY()) < (float) 0.7) {
                if (x <= other.getX()) x -= speed * delta;
                else x += speed * delta;

                if (y <= other.getY()) y -= speed * delta;
                else y += speed * delta;
            }
        }

        // 2. Predict the next position based on the Player's position
        float nextX = x;
        float nextY = y;

        // Path towards the Player
        if (player.getY() > y + (float) 0.1) {
            nextY += speed * delta;
            isMoving = true;
        }
        if (player.getY() < y - (float) 0.1) {
            nextY -= speed * delta;
            isMoving = true;
        }

        if (player.getX() < x - (float) 0.1) {
            nextX -= speed * delta;
            isFacingRight = false;
            isMoving = true;
        }
        if (player.getX() > x + (float) 0.1) {
            nextX += speed * delta;
            isFacingRight = true;
            isMoving = true;
        }

        // 3. Apply movement (ignores map collision, only checks other slimes)
        if (!isCollidingWithSlimes(nextX, y)) {
            x = nextX;
        }
        if (!isCollidingWithSlimes(x, nextY)) {
            y = nextY;
        }

        // 4. Deal contact damage when touching the Player
        if (Math.abs(player.getX() - x) < (float) 0.8 && Math.abs(player.getY() - y) < (float) 0.8) {
            if (stats.performAttack()) {
                PlayerStats.getInstance().takeDamage(stats.getDamage());
            }
        }
    }

    public void render(SpriteBatch batch) {
        // 1. Check if currently in the 1.8s Spawning Phase
        if (stats.isSpawning()) {
            if (stateTime < (float) 1.2) {
                // First 1.2s: Draw flashing warning box at normal tile size (1.0x1.0) centered on the 2.0x2.0 slime spot
                TextureRegion warnFrame = warningAnimation.getKeyFrame(stateTime, true);
                batch.draw(warnFrame, x + (float) 0.5, y + (float) 0.5, (float) 1.0, (float) 1.0);
            } else {
                // Remaining 0.6s: Draw the 3-frame slime spawn animation with the slime's color tint
                TextureRegion spawnFrame = spawnAnimation.getKeyFrame(stateTime - (float) 1.2, false);
                batch.setColor(slimeColor);
                batch.draw(spawnFrame, x, y, width, height);
                TimeManager.getInstance().applyLightTint(batch);
            }
            return;
        }

        TextureRegion currentFrame;

        // 2. Normal state: Check if dead first, then moving, then idle
        if (stats.isDead()) {
            currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        } else if (isMoving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Flip frame horizontally depending on facing direction
        if (currentFrame.isFlipX() != isFacingRight) {
            currentFrame.flip(true, false);
        }

        // Apply randomized slime color reskin, draw, then restore ambient lighting tint
        batch.setColor(slimeColor);
        batch.draw(currentFrame, x, y, width, height);
        TimeManager.getInstance().applyLightTint(batch);
    }

    public void takeDamage(int damage) {
        boolean died = stats.takeDamage(damage);
        if (died) {
            stateTime = 0; // Reset animation time so death animation starts from Frame 1
        }
    }

    /**
     * Checks if the target position overlaps with any other alive slime.
     */
    private boolean isCollidingWithSlimes(float targetX, float targetY) {
        for (Slime other : WaveManager.getInstance().getSlimes()) {
            // Skip checking against itself, dead slimes, or slimes still spawning
            if (other == this || other.getStats().isDead() || other.getStats().isSpawning()) {
                continue;
            }

            if (Math.abs(targetX - other.getX()) < (float) 0.7 && Math.abs(targetY - other.getY()) < (float) 0.7) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeathFinished() {
        return stats.isDead() && deathAnimation.isAnimationFinished(stateTime);
    }

    public void dispose() {
        if (slimeTexture != null) slimeTexture.dispose();
        if (warningTexture != null) warningTexture.dispose();
    }

    // --- Getters ---
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public SlimeStats getStats() { return stats; }
}
