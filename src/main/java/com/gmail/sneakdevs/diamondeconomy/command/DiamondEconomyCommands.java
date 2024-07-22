package com.gmail.sneakdevs.diamondeconomy.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DiamondEconomyCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("economy").then(BalanceCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(TopCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(DepositCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(SendCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(SetCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(WithdrawCommand.buildCommand()));
        dispatcher.register(Commands.literal("economy").then(XPCommand.buildCommand()));
    }
}