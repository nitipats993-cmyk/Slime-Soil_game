package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.MathUtils;

public class SlimeStats {

    // --- Health Stat (Wave 1-3 starts at 3-5 HP, scales every 3 waves) ---
    private int maxHealth;
    private int currentHealth;
    private boolean isDead = false;

    // --- Spawning Invincibility Stat (1.2s warning box + 0.6s spawn anim = 1.8s) ---
    private boolean isSpawning = true;
    private float spawnTimer = 1.8f;

    // --- Movement Speed Stat (Starts at 2.0, increases slightly each wave) ---
    private float speed;

    // --- Attack Damage & Cooldown Stat ---
    private int baseDamage = 1;
    private float attackCooldownTimer = 0.0f;
    private static final float ATTACK_COOLDOWN = 1.0f; // 1 second cooldown between hits

    public SlimeStats(int currentWave) {
        // Scale health range every 3 waves (Waves 1-3: 3-5 HP, Waves 4-6: 6-8 HP, Waves 7-9: 9-11 HP...)
        int tier = (currentWave - 1) / 3;
        int minHealth = 3 + (tier * 3);
        int maxHealthRange = 5 + (tier * 3);

        this.maxHealth = MathUtils.random(minHealth, maxHealthRange);
        this.currentHealth = this.maxHealth;

        // Increase speed by a tiny amount (+0.05) each wave
        this.speed = 2.0f + ((currentWave - 1) * 0.05f);
    }

    public void update(float delta) {
        // 1. Handle Spawning Invincibility Timer
        if (spawnTimer > 0) {
            spawnTimer -= delta;
            if (spawnTimer <= 0) {
                isSpawning = false;
            }
        }

        // 2. Handle Attack Cooldown
        if (attackCooldownTimer > 0) {
            attackCooldownTimer -= delta;
        }
    }

    /**
     * Checks if the slime is ready to deal contact damage and resets the cooldown.
     */
    public boolean performAttack() {
        if (!isDead && !isSpawning && attackCooldownTimer <= 0) {
            attackCooldownTimer = ATTACK_COOLDOWN;
            return true;
        }
        return false;
    }

    /**
     * Takes damage and triggers loot drops when health reaches 0.
     * Slime is invincible while isSpawning is true.
     */
    public boolean takeDamage(int damage) {
        if (isDead || isSpawning) return false;

        currentHealth = Math.max(0, currentHealth - damage);
        System.out.println("Slime HP: " + currentHealth + "/" + maxHealth);

        if (currentHealth == 0) {
            isDead = true;
            System.out.println("Slime Defeated!");
            dropLoot();
            return true;
        }
        return false;
    }

    /**
     * Single roll so each slime death can only drop at most ONE type of seed:
     * - 08% low chance: SkillFlower Seed
     * - 12% low chance: GemFlower Seed
     * - 25% medium chance: Tomato Seed
     * - 40% high chance: Other Seed
     * - 15% : Nothing
     */
    private void dropLoot() {
        float roll = MathUtils.random();

        if (roll < 0.08f) {
            Inventory.getInstance().addItem("SkillFlower Seed", 1);
            PlayerStats.getInstance().showPickupText("+1 SkillFlower Seed");
        } else if (roll < 0.20f) {
            Inventory.getInstance().addItem("GemFlower Seed", 1);
            PlayerStats.getInstance().showPickupText("+1 GemFlower Seed");
        } else if (roll < 0.45f) {
            Inventory.getInstance().addItem("Tomato Seed", 1);
            PlayerStats.getInstance().showPickupText("+1 Tomato Seed");
        } else if (roll < 0.85f) {
            int roll2 = MathUtils.random(1, 2);
            if (roll2 == 1){ Inventory.getInstance().addItem("Wheat", 1);
            PlayerStats.getInstance().showPickupText("+1 Wheat");}
            if (roll2 == 2){ Inventory.getInstance().addItem("Carrot", 1);
            PlayerStats.getInstance().showPickupText("+1 Carrot Seed");}
        }
    }

    // --- Getters ---
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public float getSpeed() { return speed; }
    public int getBaseDamage() { return baseDamage; }
    public boolean isDead() { return isDead; }
    public boolean isSpawning() { return isSpawning; }
}
