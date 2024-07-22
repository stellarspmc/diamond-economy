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

public class TopCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal("top")
                .then(
                        Commands.argument("page", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int page = IntegerArgumentType.getInteger(e, "page");
                                    return topCommand(e, page);
                                })
                )
                .executes(e -> topCommand(e, 1));
    }

    public static int topCommand(CommandContext<CommandSourceStack> ctx, int page) throws CommandSyntaxException {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        String output = dm.top(ctx.getSource().getPlayerOrException().getStringUUID(), page);
        ctx.getSource().sendSuccess(() -> Component.literal(" ----").withStyle(ChatFormatting.YELLOW)
                .append(" EconomyTop ").withStyle(ChatFormatting.GOLD)
                .append("----").withStyle(ChatFormatting.YELLOW)
                .append("\n" + output), false);
        return 1;
    }
}
