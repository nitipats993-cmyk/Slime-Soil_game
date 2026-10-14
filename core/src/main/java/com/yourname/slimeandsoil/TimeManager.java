package com.yourname.slimeandsoil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TimeManager {

    private static TimeManager instance;

    // Time representation: 0.0 to 24.0 hours
    private float currentHour = 6.0f; // Day starts at 6:00 AM (6.0f is default, if you wanna test for night try 22.0f)
    private int currentDay = 1;

    // Real-time duration of 1 full in-game day (300 seconds = 5 minutes)
    private static final float DAY_DURATION_SECONDS = 300.0f; // default is 300.0f
    private static final float HOURS_PER_SECOND = 24.0f / DAY_DURATION_SECONDS;

    // Color tints for Day/Night cycle
    private final Color dayColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);        // Daylight (Normal)
    private final Color sunsetColor = new Color(0.9f, 0.65f, 0.5f, 1.0f);    // Sunset (Warm Orange)
    private final Color nightColor = new Color(0.35f, 0.4f, 0.65f, 1.0f);    // Night (Navy Blue)
    private final Color dawnColor = new Color(0.7f, 0.6f, 0.8f, 1.0f);       // Dawn (Soft Purple)
    private final Color currentAmbientColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

    // Tracker for DungeonEntries
    private boolean enteredDungeonToday = false;

    private TimeManager() {}

    public static TimeManager getInstance() {
        if (instance == null) {
            instance = new TimeManager();
        }
        return instance;
    }

    public void update(float delta) {
        currentHour += delta * HOURS_PER_SECOND;

        // Reset clock at midnight
        if (currentHour >= 24.0f) {
            currentHour -= 24.0f;
            startNewDay();
        }

        // Press 'N' to sleep and fast-forward to 6:00 AM
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            sleepToNextDay();
        }

        updateAmbientLighting();
    }

    private void updateAmbientLighting() {
        if (currentHour >= 6.0f && currentHour < 17.0f) {
            // Daylight (6:00 AM - 5:00 PM)
            currentAmbientColor.set(dayColor);
        } else if (currentHour >= 17.0f && currentHour < 19.0f) {
            // Sunset: Day -> Sunset (5:00 PM - 7:00 PM)
            float alpha = (currentHour - 17.0f) / 2.0f;
            currentAmbientColor.set(dayColor).lerp(sunsetColor, alpha);
        } else if (currentHour >= 19.0f && currentHour < 21.0f) {
            // Dusk: Sunset -> Night (7:00 PM - 9:00 PM)
            float alpha = (currentHour - 19.0f) / 2.0f;
            currentAmbientColor.set(sunsetColor).lerp(nightColor, alpha);
        } else if (currentHour >= 21.0f || currentHour < 4.0f) {
            // Nighttime (9:00 PM - 4:00 AM)
            currentAmbientColor.set(nightColor);
        } else if (currentHour >= 4.0f && currentHour < 5.0f) {
            // Night -> Dawn (4:00 AM - 5:00 AM)
            float alpha = (currentHour - 4.0f) / 1.0f;
            currentAmbientColor.set(nightColor).lerp(dawnColor, alpha);
        } else {
            // Sunrise: Dawn -> Day (5:00 AM - 6:00 AM)
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

    public void sleepToNextDay() {
        System.out.println("Sleeping... Fast-forwarding to 6:00 AM.");
        currentHour = 6.0f;
        startNewDay();
    }

    private void startNewDay() {
        currentDay++;
        enteredDungeonToday = false; // Reset daily dungeon entry limit on new day
        System.out.println("=== Day " + currentDay + " Has Started ===");
        CropManager.getInstance().advanceDay();
    }

    public boolean hasEnteredDungeonToday() {
        return enteredDungeonToday;
    }

    public void setEnteredDungeonToday(boolean value) {
        this.enteredDungeonToday = value;
    }

    public int getCurrentDay() { return currentDay; }
    public float getCurrentHour() { return currentHour; }
}
