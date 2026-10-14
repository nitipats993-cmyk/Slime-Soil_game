package com.yourname.slimeandsoil;

public class PlayerStats {

    private static PlayerStats instance;

    // --- Skill Points ---
    private int skillPoints = 1;

    // --- Health Stat (Start: 5, Max Upgrade: 20) ---
    private int maxHealth = 5;
    private int currentHealth = 5;

    // --- Regenerative Shield Stat (Start: 2, Max Upgrade: 10) ---
    private int maxShield = 2;
    private int currentShield = 2;
    private float shieldRegenTimer = 0.0f;
    private static final float SHIELD_REGEN_DELAY = 3.0f;
    private float shieldTickTimer = 0.0f;

    // --- Mana Stat (Start: 5, Max Upgrade: 25) ---
    private int maxMana = 5;
    private float currentMana = 5.0f;
    private static final float MANA_REGEN_RATE = 1.0f; // i feel llike 1 sec is good no?

    // --- Attack Damage Stat ---
    private float atkMultiplier = 1.0f;
    private int swordTier = 0;     // 0: Wooden, 1: Iron, ..., 6: ???
    private int swordSubLevel = 0; // 0 to 5 sub-upgrades per tier

    // --- Dodge Mechanic Stat ---
    private float dodgeCooldownTimer = 0.0f;
    private static final float DODGE_COOLDOWN = 1.0f;
    private static final int DODGE_MANA_COST = 5;
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
        if (dodgeCooldownTimer > 0) dodgeCooldownTimer -= delta;

        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) isInvulnerable = false;
        }

        if (currentMana < maxMana) {
            currentMana = Math.min(maxMana, currentMana + (MANA_REGEN_RATE * delta));
        }

        shieldRegenTimer += delta;
        if (shieldRegenTimer >= SHIELD_REGEN_DELAY && currentShield < maxShield) {
            shieldTickTimer += delta;
            if (shieldTickTimer >= 1.0f) {
                currentShield++;
                shieldTickTimer = 0.0f;
            }
        }
    }

    public boolean performDodge() {
        if (dodgeCooldownTimer <= 0 && currentMana >= DODGE_MANA_COST) {
            currentMana -= DODGE_MANA_COST;
            dodgeCooldownTimer = DODGE_COOLDOWN;
            isInvulnerable = true;
            invulnerabilityTimer = 0.35f;
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        if (isInvulnerable) return;

        shieldRegenTimer = 0.0f;
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
    }

    public int getBaseSwordDamage() {
        return SwordData.calculateDamage(swordTier, swordSubLevel);
    }

    public int getTotalAttackDamage() {
        return Math.round(getBaseSwordDamage() * atkMultiplier);
    }

    // --- Sword Upgrade Logic ---
    public boolean canUpgradeSword() {
        if (swordTier >= 6) return false; // Max tier ("???") reached
        int cost = SwordData.getUpgradeCost(swordTier, swordSubLevel);
        return Inventory.getInstance().getGold() >= cost;
    }

    public boolean upgradeSword() {
        if (!canUpgradeSword()) return false;

        int cost = SwordData.getUpgradeCost(swordTier, swordSubLevel);
        Inventory.getInstance().removeGold(cost);

        if (swordSubLevel < 5) {
            swordSubLevel++;
        } else {
            // Reached 5 sub-upgrades -> Tier up to next sword
            swordTier++;
            swordSubLevel = 0;
            System.out.println("NEW SWORD UNLOCKED: " + SwordData.TIERS[swordTier].name);
        }
        return true;
    }

    // --- Skill Point Upgrades ---
    public int getSkillPoints() { return skillPoints; }
    public void addSkillPoints(int amount) { skillPoints += amount; }

    public boolean upgradeMaxHealth() {
        if (skillPoints >= 1 && maxHealth + 1 <= 20) {
            skillPoints--;
            maxHealth++;
            currentHealth = maxHealth;
            return true;
        }
        return false;
    }

    public boolean upgradeMaxShield() {
        if (skillPoints >= 1 && maxShield + 1 <= 10) {
            skillPoints--;
            maxShield++;
            currentShield = maxShield;
            return true;
        }
        return false;
    }

    public boolean upgradeMaxMana() {
        if (skillPoints >= 1 && maxMana + 5 <= 25) {
            skillPoints--;
            maxMana += 5;
            currentMana = maxMana;
            return true;
        }
        return false;
    }

    public boolean upgradeAtkMultiplier() {
        if (skillPoints >= 1 && atkMultiplier < 2.50f) {
            skillPoints--;
            atkMultiplier = Math.min(2.5f, atkMultiplier + 0.15f);
            return true;
        }
        return false;
    }

    // Getters
    public int getSwordTier() { return swordTier; }
    public int getSwordSubLevel() { return swordSubLevel; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentShield() { return currentShield; }
    public int getMaxShield() { return maxShield; }
    public int getCurrentMana() { return (int) currentMana; }
    public int getMaxMana() { return maxMana; }
    public float getAtkMultiplier() { return atkMultiplier; }
    public boolean isInvulnerable() { return isInvulnerable; }
}
