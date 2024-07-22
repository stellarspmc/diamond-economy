package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal("withdraw")
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return withdrawCommand(e, amount);
                                })
                );
    }

    public static int withdrawCommand(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        if (dm.changeBalance(player.getStringUUID(), -amount)) ctx.getSource().sendSuccess(() -> Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("You have withdrew ").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("$" + (amount - DiamondUtils.dropItem(amount, player))).withStyle(ChatFormatting.RED))
                .append(Component.literal(".").withStyle(ChatFormatting.GOLD)), false);
        else ctx.getSource().sendSuccess(() -> Component.literal("ERR: You have less than $" + amount).withStyle(ChatFormatting.DARK_RED), false);
        return 1;
    }

}
