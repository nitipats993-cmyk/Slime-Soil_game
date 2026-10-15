package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.MathUtils;

public class SlimeStats {

    private int maxHealth;
    private int currentHealth;
    private boolean isDead = false;
    private boolean isSpawning = true;
    private float spawnTimer = 1.8f;
    private float speed;
    private int damage;
    private float attackCooldownTimer = 0.0f;
    private static final float ATTACK_COOLDOWN = 1.0f; // 1 second cooldown between hits

    public SlimeStats(int currentWave) {
        int tier = (currentWave - 1) / 3; //scaling vars
        int minHealth = 5 + (tier * 3);
        int maxHealthRange = 7 + (tier * 3);

        this.maxHealth = MathUtils.random(minHealth, maxHealthRange);
        this.currentHealth = this.maxHealth;

        this.damage = 1 + tier;
        this.speed = 2.0f + ((currentWave - 1) * 0.05f);
    }

    public void update(float delta) {
        if (spawnTimer > 0) {
            spawnTimer -= delta;
            if (spawnTimer <= 0) {
                isSpawning = false;
            }
        }

        if (attackCooldownTimer > 0) {
            attackCooldownTimer -= delta;
        }
    }

    public boolean performAttack() {
        if (!isDead && !isSpawning && attackCooldownTimer <= 0) {
            attackCooldownTimer = ATTACK_COOLDOWN;
            return true;
        }
        return false;
    }

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

    //
    // 08% skillflower
    // 12% gemflower
    // 25% tomato
    // 40% arrot/wheat
    // 15% nothing
    //
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
            if (roll2 == 1) {
                Inventory.getInstance().addItem("Wheat Seed", 1);
                PlayerStats.getInstance().showPickupText("+1 Wheat Seed");
            }
            if (roll2 == 2) {
                Inventory.getInstance().addItem("Carrot Seed", 1);
                PlayerStats.getInstance().showPickupText("+1 Carrot Seed");
            }
        }
    }

    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public float getSpeed() { return speed; }
    public int getDamage() { return damage; }
    public boolean isDead() { return isDead; }
    public boolean isSpawning() { return isSpawning; }
}
