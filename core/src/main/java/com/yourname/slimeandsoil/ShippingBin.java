package com.yourname.slimeandsoil;

import com.badlogic.gdx.math.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class ShippingBin {

    private static ShippingBin instance;
    private final Map<String, Integer> cropPrices;

    // Dynamic rectangular area loaded directly from Tiled
    private final Rectangle binArea = new Rectangle(-1, -1, 0, 0);

    private ShippingBin() {
        cropPrices = new HashMap<>();
        cropPrices.put("Tomato", 25);
        cropPrices.put("GemFlower", 75);
        cropPrices.put("SkillFlower", 120);
        cropPrices.put("Carrot", 15);
        cropPrices.put("Wheat", 10);
    }

    public static ShippingBin getInstance() {
        if (instance == null) {
            instance = new ShippingBin();
        }
        return instance;
    }

    /**
     * Called by GameScreen to set the bounds drawn in Tiled (converted to grid unit coordinates).
     */
    public void setBinArea(float gridX, float gridY, float width, float height) {
        this.binArea.set(gridX, gridY, width, height);
        System.out.println(String.format("Shipping Bin Area set in Tiled: X[%.1f to %.1f], Y[%.1f to %.1f]",
            gridX, gridX + width, gridY, gridY + height));
    }

    /**
     * Checks if the target tile grid coordinate falls inside the Tiled area.
     */
    public boolean isBinLocation(int x, int y) {
        // Checks if the center point of target tile (x + 0.5, y + 0.5) is inside the drawn area
        return binArea.contains(x + 0.5f, y + 0.5f);
    }

    /**
     * Opens the Warning Dialog before selling all crops in inventory.
     */
    public void interact(WarningDialog warningDialog) {
        Inventory inv = Inventory.getInstance();
        int totalEarnings = 0;
        int totalCropCount = 0;

        for (Map.Entry<String, Integer> entry : cropPrices.entrySet()) {
            String cropName = entry.getKey();
            int price = entry.getValue();
            int count = inv.getItemCount(cropName);

            if (count > 0) {
                totalEarnings += count * price;
                totalCropCount += count;
            }
        }

        if (totalCropCount == 0) {
            System.out.println("Shipping Bin is empty! You have no harvested crops to sell.");
            return;
        }

        final int earnings = totalEarnings;
        warningDialog.show(
            "WARNING: SELL CROPS",
            "Sell " + totalCropCount + " harvested crop(s) for " + earnings + " Gold?",
            () -> sellAllCrops(earnings)
        );
    }

    private void sellAllCrops(int earnings) {
        Inventory inv = Inventory.getInstance();
        for (String cropName : cropPrices.keySet()) {
            int count = inv.getItemCount(cropName);
            if (count > 0) {
                inv.removeItem(cropName, count);
            }
        }
        inv.addGold(earnings);
        System.out.println("Sold crops for " + earnings + " Gold!");
    }
}
