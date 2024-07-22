package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class BalanceCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal("balance")
                .then(
                        Commands.argument("playerName", StringArgumentType.string())
                                .executes(e -> {
                                    String string = StringArgumentType.getString(e, "playerName");
                                    return balanceCommand(e, string);
                                })
                )
                .then(
                        Commands.argument("player", EntityArgument.player())
                                .executes(e -> {
                                    String player = EntityArgument.getPlayer(e, "player").getName().getString();
                                    return balanceCommand(e, player);
                                })
                )
                .executes(e -> balanceCommand(e, e.getSource().getPlayerOrException().getName().getString()));
    }

    public static int balanceCommand(CommandContext<CommandSourceStack> ctx, String player) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        int bal = dm.getBalanceFromName(player);
        if ((bal > -1)) ctx.getSource().sendSuccess(() ->
                {
                    try {
                        return Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                                    .append(Component.literal((ctx.getSource().getPlayerOrException().getName().getString().equals(player) ? "You" : player) + " have ").withStyle(ChatFormatting.GOLD))
                                    .append(Component.literal("$" + bal).withStyle(ChatFormatting.RED))
                                    .append(Component.literal(".").withStyle(ChatFormatting.GOLD));
                    } catch (CommandSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
        , false);
        else ctx.getSource().sendSuccess(() -> Component.literal("ERR: No account was found for player with the name ").withStyle(ChatFormatting.DARK_RED)
                .append(Component.literal(player).withStyle(ChatFormatting.GOLD))
                .append(Component.literal(".").withStyle(ChatFormatting.DARK_RED)), false);
        return 1;
    }
}
