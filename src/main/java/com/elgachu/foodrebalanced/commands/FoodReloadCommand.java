package com.elgachu.foodrebalanced.commands;

import com.elgachu.foodrebalanced.config.FoodConfigManager;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class FoodReloadCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
            Commands.literal("foodreload")
                .requires(source -> source.hasPermission(2)) // OP only
                .executes(context -> {

                    FoodConfigManager.loadConfig();

                    context.getSource().sendSuccess(
                        () -> Component.literal("Food config reloaded."),
                        true
                    );

                    return 1;
                })
        );
    }
}
