package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.MathUtils;

public class SlimeStats {

    // --- Health Stat (Wave 1-3 starts at 3-5 HP, scales every 3 waves) ---
    private int maxHealth;
    private int currentHealth;
    private boolean isDead = false;

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
        // Handle Attack Cooldown
        if (attackCooldownTimer > 0) {
            attackCooldownTimer -= delta;
        }
    }

    /**
     * Checks if the slime is ready to deal contact damage and resets the cooldown.
     */
    public boolean performAttack() {
        if (!isDead && attackCooldownTimer <= 0) {
            attackCooldownTimer = ATTACK_COOLDOWN;
            return true;
        }
        return false;
    }

    /**
     * Takes damage and triggers loot drops when health reaches 0.
     * Returns true if this hit defeated the slime.
     */
    public boolean takeDamage(int damage) {
        if (isDead) return false;

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
     * Medium chance of dropping normal crops, low chance of dropping GemFlower and SkillFlower seeds.
     */
    private void dropLoot() {
        // Medium chance (50%) of dropping normal crops
        if (MathUtils.randomBoolean(0.50f)) {
            int roll = MathUtils.random(1, 3);
            if (roll == 1) Inventory.getInstance().addItem("Tomato", 1);
            if (roll == 2) Inventory.getInstance().addItem("Carrot", 1);
            if (roll == 3) Inventory.getInstance().addItem("Wheat", 1);
        }

        // Low chance (15%) of dropping GemFlower Seed
        if (MathUtils.randomBoolean(0.15f)) {
            Inventory.getInstance().addItem("GemFlower Seed", 1);
        }

        // Low chance (10%) of dropping SkillFlower Seed
        if (MathUtils.randomBoolean(0.10f)) {
            Inventory.getInstance().addItem("SkillFlower Seed", 1);
        }
    }

    // --- Getters ---
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public float getSpeed() { return speed; }
    public int getBaseDamage() { return baseDamage; }
    public boolean isDead() { return isDead; }
}
