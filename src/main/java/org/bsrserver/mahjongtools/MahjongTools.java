package org.bsrserver.mahjongtools;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import org.bsrserver.mahjongtools.command.CalculateHandler;

public class MahjongTools implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("mahjong")
                        .then(CommandManager.literal("calculate")
                                .then(CommandManager.argument("player", StringArgumentType.string())
                                        .suggests(
                                                (context, builder) -> {
                                                    if (context.getCommand() == null) {
                                                        builder.suggest("E");
                                                        builder.suggest("S");
                                                        builder.suggest("W");
                                                        builder.suggest("N");
                                                    }
                                                    return builder.buildFuture();
                                                }
                                        )
                                        .executes(new CalculateHandler())
                                )
                        )
                )
        );
    }
}
