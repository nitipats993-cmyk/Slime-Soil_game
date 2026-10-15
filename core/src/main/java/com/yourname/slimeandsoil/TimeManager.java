package com.yourname.slimeandsoil;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimeManager {

    private static TimeManager instance;

    // Time representation: 0.0 to 24.0 hours
    private float currentHour = 6.0f; // Day starts at 6:00 AM
    private int currentDay = 1;

    // Real-time duration of 1 full in-game day (300 seconds = 5 minutes)
    private static final float DAY_DURATION_SECONDS = 300.0f;
    private static final float HOURS_PER_SECOND = 24.0f / DAY_DURATION_SECONDS;

    // Color tints for Day/Night cycle
    private final Color dayColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);        // Daylight (Normal)
    private final Color sunsetColor = new Color(0.9f, 0.65f, 0.5f, 1.0f);    // Sunset (Warm Orange)
    private final Color nightColor = new Color(0.35f, 0.4f, 0.65f, 1.0f);    // Night (Navy Blue)
    private final Color dawnColor = new Color(0.7f, 0.6f, 0.8f, 1.0f);       // Dawn (Soft Purple)
    private final Color currentAmbientColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    // Trackers for Daily Limits & Evening Farm Wave
    private boolean enteredDungeonToday = false;
    private boolean farmWaveTriggeredToday = false;

    // Arena State & 7:00 PM (1 Hour After 6:00 PM) Crop Penalty Tracker
    private boolean inArena = false;
    private boolean arenaCropPenaltyTriggered = false;

    private TimeManager() {}

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public void update(float delta) {
        float previousHour = currentHour;
        currentHour += delta * HOURS_PER_SECOND;

        // 1. If still inside the Arena at 7:00 PM (19.0f, 1 hour after 6:00 PM), delete all farm crops
        if (inArena && (currentHour >= 19.0f || currentHour < 6.0f) && !arenaCropPenaltyTriggered) {
            arenaCropPenaltyTriggered = true;
            farmWaveTriggeredToday = true; // Farm was overrun while player was away
            CropManager.getInstance().clearAllCrops();
            PlayerStats.getInstance().showPickupText("7:00 PM! Missed Farm Defense - Crops Destroyed!");
            System.out.println("=== 7:00 PM (1 hr after 6 PM) while still in Arena! All farm crops deleted! ===");
        }

        // 2. Wrap clock at midnight (24:00 -> 0:00)
        if (currentHour >= 24.0f) {
            currentHour -= 24.0f;
        }

        // 3. When 6:00 AM arrives, check if Farm Defense slimes are still alive
        if (previousHour < 6.0f && currentHour >= 6.0f) {
            if (WaveManager.getInstance().getAliveSlimeCount() > 0) {
                CropManager.getInstance().clearAllCrops();
                WaveManager.getInstance().dispose();
                PlayerStats.getInstance().showPickupText("6:00 AM! Slimes destroyed your crops!");
                System.out.println("=== 6:00 AM arrived with slimes still alive! All crops deleted! ===");
            }
            startNewDay();
        }

        updateAmbientLighting();
    }

    public void startArenaTimer() {
        this.inArena = true;
    }

    public void stopArenaTimer() {
        this.inArena = false;
    }

    public boolean isInArena() {
        return inArena;
    }

    private void updateAmbientLighting() {
        if (currentHour >= 6.0f && currentHour < 17.0f) {
            currentAmbientColor.set(dayColor);
        } else if (currentHour >= 17.0f && currentHour < 19.0f) {
            float alpha = (currentHour - 17.0f) / 2.0f;
            currentAmbientColor.set(dayColor).lerp(sunsetColor, alpha);
        } else if (currentHour >= 19.0f && currentHour < 21.0f) {
            float alpha = (currentHour - 19.0f) / 2.0f;
            currentAmbientColor.set(sunsetColor).lerp(nightColor, alpha);
        } else if (currentHour >= 21.0f || currentHour < 4.0f) {
            currentAmbientColor.set(nightColor);
        } else if (currentHour >= 4.0f && currentHour < 5.0f) {
            float alpha = (currentHour - 4.0f) / 1.0f;
            currentAmbientColor.set(nightColor).lerp(dawnColor, alpha);
        } else {
            float alpha = (currentHour - 5.0f) / 1.0f;
            currentAmbientColor.set(dawnColor).lerp(dayColor, alpha);
        }
    }

    public void applyLightTint(SpriteBatch batch) {
        batch.setColor(currentAmbientColor);
    }

    public void resetLightTint(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
    }

    /**
     * Fast forwards daytime directly to 18:00 (6:00 PM Evening) for Farm Defense
     */
    public void skipToEvening() {
        if (inArena) return; // Cannot skip to 6:00 PM while inside the Arena

        if (currentHour < 18.0f && currentHour >= 6.0f) {
            System.out.println("Resting... Fast-forwarding to 18:00 PM (Start of Farm Defense).");
            currentHour = 18.0f;
        }
    }

    /**
     * Full rest at the house door to advance to 6:00 AM of the Next Day & refill stats
     */
    public void sleepToNextDay() {
        System.out.println("Sleeping until 6:00 AM of the next day...");
        currentHour = 6.0f;
        startNewDay();
        PlayerStats.getInstance().fullRestore();
    }

    private void startNewDay() {
        currentDay++;
        enteredDungeonToday = false;        // Reset daily dungeon entry limit
        farmWaveTriggeredToday = false;     // Reset evening farm wave trigger for the new day
        arenaCropPenaltyTriggered = false;  // Reset 7:00 PM arena crop penalty tracker
        System.out.println("=== Day " + currentDay + " Has Started ===");
        CropManager.getInstance().advanceDay();
    }

    public boolean hasEnteredDungeonToday() { return enteredDungeonToday; }
    public void setEnteredDungeonToday(boolean value) { this.enteredDungeonToday = value; }

    public boolean isFarmWaveTriggeredToday() { return farmWaveTriggeredToday; }
    public void setFarmWaveTriggeredToday(boolean value) { this.farmWaveTriggeredToday = value; }

    public int getCurrentDay() { return currentDay; }
    public float getCurrentHour() { return currentHour; }
}
