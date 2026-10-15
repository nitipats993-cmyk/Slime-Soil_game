package com.yourname.slimeandsoil;

import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private static Inventory instance;
    private final Map<String, Integer> items;
    private int gold;

    private Inventory() {
        items = new HashMap<>();
        gold = 100; // starting gold is 100 don't you dare change it bro

        items.put("Tomato Seed", 3);     // starts with 3 Tomato Seeds
        items.put("Carrot Seed", 0);     // starts with 0 Carrot Seeds
        items.put("Wheat Seed", 0);      // starts with 0 Wheat Seeds
        items.put("GemFlower Seed", 0);  // starts with 0 (must be looted from slimes) rare
        items.put("SkillFlower Seed", 0);  // starts with 0 (must be looted from slimes) rare
    }

    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void addGold(int amount) {
        gold += amount;
        System.out.println("Earned +" + amount + " Gold! Current Balance: " + gold + " G");
    }

    public boolean removeGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public int getGold() {
        return gold;
    }

    public void addItem(String itemName, int amount) {
        items.put(itemName, items.getOrDefault(itemName, 0) + amount);
        System.out.println("Added + " + amount + " " + itemName + "(s). Total: " + items.get(itemName));
    }

    public boolean removeItem(String itemName, int amount) {
        int currentAmount = items.getOrDefault(itemName, 0);
        if (currentAmount >= amount) {
            if (currentAmount - amount == 0) {
                items.remove(itemName);
            } else {
                items.put(itemName, currentAmount - amount);
            }
            return true;
        }
        return false;
    }

    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
