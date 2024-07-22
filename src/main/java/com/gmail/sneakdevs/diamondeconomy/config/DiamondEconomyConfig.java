package com.gmail.sneakdevs.diamondeconomy.config;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

@Config(name = DiamondEconomy.MODID)
public class DiamondEconomyConfig implements ConfigData {

    @Comment("List of items used as currency")
    public String[] currencies = {"minecraft:diamond","minecraft:diamond_block"};

    @Comment("Values of each currency in the same order (must be in ascending order unless greedyWithdraw is disabled)")
    public int[] currencyValues = {1,9};

    @Comment("Where the diamondeconomy.sqlite file is located (ex: \"C:/Users/example/Desktop/server/world/diamondeconomy.sqlite\")")
    public String fileLocation = null;

    @Comment("Symbol for the currency used in chat. You can specify a prefix and suffix")
    public String currencySymbolPrefix = "$";

    public static Item getCurrency(int num) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(DiamondEconomyConfig.getInstance().currencies[num]));
    }

    public static int[] getCurrencyValues() {
        return DiamondEconomyConfig.getInstance().currencyValues;
    }

    public static DiamondEconomyConfig getInstance() {
        return AutoConfig.getConfigHolder(DiamondEconomyConfig.class).getConfig();
    }

    /* This is implemented with Component such that it can be extended to used custom styles, e.g. via config */
    public static MutableComponent currencyToLiteral(double c) {
        DiamondEconomyConfig inst = DiamondEconomyConfig.getInstance();
        String currencyStr = inst.currencySymbolPrefix + c;
        return Component.literal(currencyStr).withStyle(ChatFormatting.RED);
    }

    public static int getRandomizedCurrencyValue(ServerPlayer player, int i) {
        return (int) Math.round(getCurrencyValues()[i] * (0.9 + (player.getRandom().nextDouble() / 5)));
    }
}