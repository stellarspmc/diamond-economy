package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Style;

public class DepositCommand {

    private static final MutableComponent MAX_BALANCE_ERROR = Component.literal("ERR: Transaction failed.").withStyle(ChatFormatting.DARK_RED);
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal("deposit")
                .executes(DepositCommand::depositHandCommand)
                .then(Commands.literal("all").executes(DepositCommand::depositAllCommand)
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    final int n = IntegerArgumentType.getInteger(ctx, "amount");
                                    return depositAllCommand(ctx, n);
                                }))
                );
    }

    public static int depositAllCommand(CommandContext<CommandSourceStack> ctx, int n) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        int currencyCount = 0;
        for (int i = DiamondEconomyConfig.getCurrencyValues().length - 1; i >= 0; i--) {
            double currencyMultiplier = DiamondEconomyConfig.getRandomizedCurrencyValue(player, i);
            if (n != -1 && (currencyCount + currencyMultiplier) > n) break; // maximal deposit reached
            for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
                if (player.getInventory().getItem(j).getItem().equals(DiamondEconomyConfig.getCurrency(i))) {
                    int takeAmount = player.getInventory().getItem(j).getCount();
                    if (n != -1)// if there is a max deposit, reduce takeAmount if necessary
                        takeAmount = (int) Math.min(takeAmount,  (n - currencyCount) / currencyMultiplier);
                    if (takeAmount > 0) {
                        currencyCount += (int) (takeAmount * currencyMultiplier);
                        player.getInventory().removeItem(j, takeAmount);
                    }
                }
            }
        }
        if (dm.changeBalance(player.getStringUUID(), currencyCount)) {
            int finalCurrencyCount = currencyCount;
            ctx.getSource().sendSuccess(() ->
                            Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                                    .append(Component.literal("Added ").withStyle(ChatFormatting.GOLD))
                                    .append(DiamondEconomyConfig.currencyToLiteral(finalCurrencyCount))
                                    .append(Component.literal(" to your account.").withStyle(ChatFormatting.GOLD))
                    ,false);
            return 1;
        } else {
            DiamondUtils.dropItem(currencyCount, player);
            ctx.getSource().sendFailure(MAX_BALANCE_ERROR);
            return -1;
        }
    }

    public static int depositAllCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return depositAllCommand(ctx, -1);
    }

    public static int depositHandCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack hand = player.getInventory().getSelected();

        // Find out which currency item is in Hand
        int i;
        for (i = DiamondEconomyConfig.getCurrencyValues().length - 1; i >= 0; i--) {
            if (hand.getItem().equals(DiamondEconomyConfig.getCurrency(i))) break;
        }
        if (hand.isEmpty() || i == -1) {
            // Hand hold no currency item
            ctx.getSource().sendFailure(
                    Component.literal("ERR: Hold a currency item in your hand or use the ").withStyle(ChatFormatting.DARK_RED)
                            .append(Component.literal("deposit all [amount] ").withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withItalic(true)))
                            .append(Component.literal("command.").withStyle(ChatFormatting.DARK_RED))
            );
            return -1;
        }

        // add balance to account
        final int currencyCount = hand.getCount() * DiamondEconomyConfig.getRandomizedCurrencyValue(player, i);
        if (dm.changeBalance(player.getStringUUID(), currencyCount)) {
            ctx.getSource().sendSuccess(() ->
                    Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                            .append(Component.literal("Added ").withStyle(ChatFormatting.GOLD))
                            .append(DiamondEconomyConfig.currencyToLiteral(currencyCount))
                            .append(Component.literal(" to your account.").withStyle(ChatFormatting.GOLD)), false);
            player.getInventory().removeFromSelected(true); // remove whole stack
            return 1;
        } else {
            ctx.getSource().sendFailure(MAX_BALANCE_ERROR);
            return -1;
        }
    }
}