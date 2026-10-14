package com.yourname.slimeandsoil;

public class PlayerStats {

    private static PlayerStats instance;

    // --- Health Stat (Start: 5, Max Upgrade: 20) ---
    private int maxHealth = 5;
    private int currentHealth = 5;

    // --- Regenerative Shield Stat (Start: 2, Max Upgrade: 10) ---
    private int maxShield = 2;
    private int currentShield = 2;
    private float shieldRegenTimer = 0.0f;
    private static final float SHIELD_REGEN_DELAY = 3.0f; // Seconds after taking hit before shield regens
    private float shieldTickTimer = 0.0f;

    // --- Mana Stat (Start: 200, Max Upgrade: 400, Increments of +20) ---
    private int maxMana = 5;
    private float currentMana = 5.0f;
    private static final float MANA_REGEN_RATE = 1.0f; // Mana regenerated per second

    // --- Attack Damage Stat ---
    private float atkMultiplier = 1.0f; // Start: 1.0x, Max Upgrade: 2.5x
    private int swordBaseDamage = 3;   // Swords give +3, +5, +9

    // --- Dodge Mechanic Stat ---
    private float dodgeCooldownTimer = 0.0f;
    private static final float DODGE_COOLDOWN = 1.0f; // 1 second cooldown
    private static final int DODGE_MANA_COST = 5;      // Costs 5 Mana
    private boolean isInvulnerable = false;
    private float invulnerabilityTimer = 0.0f;

    private PlayerStats() {}

    public static PlayerStats getInstance() {
        if (instance == null) {
            instance = new PlayerStats();
        }
        return instance;
    }

    public void update(float delta) {
        // 1. Handle Dodge Cooldown
        if (dodgeCooldownTimer > 0) {
            dodgeCooldownTimer -= delta;
        }

        // 2. Handle Invulnerability Frames (i-frames)
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }

        // 3. Mana Regeneration Over Time
        if (currentMana < maxMana) {
            currentMana = Math.min(maxMana, currentMana + (MANA_REGEN_RATE * delta));
        }

        // 4. Soul Knight Shield Regeneration (Regens 1 shield/sec after 3s delay)
        shieldRegenTimer += delta;
        if (shieldRegenTimer >= SHIELD_REGEN_DELAY && currentShield < maxShield) {
            shieldTickTimer += delta;
            if (shieldTickTimer >= 1.0f) {
                currentShield++;
                shieldTickTimer = 0.0f;
            }
        }
    }

    /**
     * Executes Dodge if player has enough Mana and Dodge is off cooldown.
     */
    public boolean performDodge() {
        if (dodgeCooldownTimer <= 0 && currentMana >= DODGE_MANA_COST) {
            currentMana -= DODGE_MANA_COST;
            dodgeCooldownTimer = DODGE_COOLDOWN;
            isInvulnerable = true;
            invulnerabilityTimer = 0.35f; // 0.35s of i-frames during dash
            return true;
        }
        return false;
    }

    /**
     * Takes damage: Shield absorbs damage first; excess leaks to Health.
     */
    public void takeDamage(int damage) {
        if (isInvulnerable) return;

        shieldRegenTimer = 0.0f; // Reset shield regen timer when hit
        shieldTickTimer = 0.0f;

        if (currentShield > 0) {
            if (currentShield >= damage) {
                currentShield -= damage;
                return;
            } else {
                damage -= currentShield;
                currentShield = 0;
            }
        }

        currentHealth = Math.max(0, currentHealth - damage);
        if (currentHealth == 0) {
            System.out.println("Player Defeated!");
        }
    }

    /**
     * Total Damage = (Sword Base Atk) * (Atk Multiplier)
     */
    public int getTotalAttackDamage() {
        return Math.round(swordBaseDamage * atkMultiplier);
    }

    // --- Upgrade Methods for Skill Shop ---
    public boolean upgradeMaxHealth(int amount) {
        if (maxHealth + amount <= 20) {
            maxHealth += amount;
            currentHealth = maxHealth;
            return true;
        }
        return false;
    }

    public boolean upgradeMaxShield(int amount) {
        if (maxShield + amount <= 10) {
            maxShield += amount;
            currentShield = maxShield;
            return true;
        }
        return false;
    }

    public boolean upgradeMaxMana(int amount) {
        if (maxMana + amount <= 400) {
            maxMana += amount;
            currentMana = maxMana;
            return true;
        }
        return false;
    }

    public boolean upgradeAtkMultiplier(float amount) {
        if (atkMultiplier + amount <= 2.51f) {
            atkMultiplier = Math.min(2.5f, atkMultiplier + amount);
            return true;
        }
        return false;
    }

    public void setSwordBaseDamage(int damage) {
        this.swordBaseDamage = damage;
    }

    // --- Getters ---
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentShield() { return currentShield; }
    public int getMaxShield() { return maxShield; }
    public int getCurrentMana() { return (int) currentMana; }
    public int getMaxMana() { return maxMana; }
    public float getAtkMultiplier() { return atkMultiplier; }
    public int getSwordBaseDamage() { return swordBaseDamage; }
    public boolean isInvulnerable() { return isInvulnerable; }
}
