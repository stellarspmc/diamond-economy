package com.gmail.sneakdevs.diamondeconomy;

import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.gmail.sneakdevs.diamondeconomy.sql.SQLiteDatabaseManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DiamondUtils {
    public static void registerTable(String query){
        DiamondEconomy.tableRegistry.add(query);
    }

    public static DatabaseManager getDatabaseManager() {
        return new SQLiteDatabaseManager();
    }

    public static int dropItem(int amount, ServerPlayer player) {
        for (int i = DiamondEconomyConfig.getCurrencyValues().length - 1; i >= 0 && amount > 0; i--) {

            int val = DiamondEconomyConfig.getCurrencyValues()[i];
            Item curr = DiamondEconomyConfig.getCurrency(i);

            while (amount >= val * curr.getDefaultMaxStackSize()) {
                ItemEntity itemEntity = player.drop(new ItemStack(curr, curr.getDefaultMaxStackSize()), true);
                assert itemEntity != null;
                itemEntity.setNoPickUpDelay();
                amount -= val * curr.getDefaultMaxStackSize();
            }

            if (amount >= val) {
                ItemEntity itemEntity = player.drop(new ItemStack(curr, amount / val), true);
                assert itemEntity != null;
                itemEntity.setNoPickUpDelay();
                amount -= amount / val * val;
            }

        }

        DatabaseManager dm = getDatabaseManager();
        dm.changeBalance(player.getStringUUID(), amount);

        return amount;
    }
}
