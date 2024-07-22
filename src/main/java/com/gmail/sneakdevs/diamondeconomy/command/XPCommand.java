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

public class XPCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal("convertxp")
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return convertToXp(e, amount);
                                })
                );
    }

    public static int convertToXp(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        if (dm.changeBalance(player.getStringUUID(), -amount)) {
            player.giveExperiencePoints(amount);
            ctx.getSource().sendSuccess(() -> Component.literal("ECO: ").withStyle(ChatFormatting.GREEN)
                    .append(Component.literal("You have converted ").withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("$" + amount).withStyle(ChatFormatting.RED))
                    .append(Component.literal(".").withStyle(ChatFormatting.GOLD)), false);
        } else ctx.getSource().sendFailure(Component.literal("ERR: You have less than $" + amount).withStyle(ChatFormatting.DARK_RED));
        return 1;
    }
}
