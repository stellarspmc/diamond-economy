package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SendCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal("send")
                .then(
                        Commands.argument("player", EntityArgument.player())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(e -> {
                                                    ServerPlayer player = EntityArgument.getPlayer(e, "player");
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return sendCommand(e, player, e.getSource().getPlayerOrException(), amount);
                                                })));
    }

    public static int sendCommand(CommandContext<CommandSourceStack> ctx, ServerPlayer player, ServerPlayer player1, int amount) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        long newValue = dm.getBalanceFromUUID(player.getStringUUID()) + amount;
        if (newValue < Integer.MAX_VALUE && dm.changeBalance(player1.getStringUUID(), -amount)) {
            dm.changeBalance(player.getStringUUID(), amount);
            player.displayClientMessage(Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("You received ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("$" + amount).withStyle(ChatFormatting.RED))
                    .append(Component.literal(" from ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(player1.getName().getString()).withStyle(ChatFormatting.RED))
                    .append(Component.literal(".").withStyle(ChatFormatting.GOLD)), false);
            ctx.getSource().sendSuccess(() -> Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("You sent ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("$" + amount).withStyle(ChatFormatting.RED))
                    .append(Component.literal(" to ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(player.getName().getString()).withStyle(ChatFormatting.RED))
                    .append(Component.literal(".").withStyle(ChatFormatting.GOLD)), false);
        } else ctx.getSource().sendSuccess(() -> Component.literal("ERR: Transaction failed.").withStyle(ChatFormatting.DARK_RED), false);
        return 1;
    }
}
