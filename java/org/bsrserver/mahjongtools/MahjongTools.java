package org.bsrserver.mahjongtools;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import org.bsrserver.mahjongtools.command.CalculateHandler;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.server.command.CommandManager.argument;

public class MahjongTools implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("mahjong")
                        .then(CommandManager.literal("calculate")
                                .then(argument("player", StringArgumentType.string())
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
                                        ).then(argument("win", StringArgumentType.string())
                                                .suggests(
                                                        (context, builder) -> {
                                                            if (context.getCommand() == null) {
                                                                builder.suggest(".ron");
                                                                builder.suggest(".tsumo");
                                                                builder.suggest("tenho");
                                                                builder.suggest("chiho");
                                                            }
                                                            return builder.buildFuture();
                                                        }
                                                ).then(argument("riichi", StringArgumentType.string())
                                                        .suggests(
                                                                (context, builder) -> {
                                                                    if (context.getCommand() == null) {
                                                                        String winArgument = getString(context, "win");
                                                                        if (!winArgument.equals("tenho") && !winArgument.equals("chiho")) {
                                                                            builder.suggest("riichi");
                                                                            builder.suggest("w_riichi");
                                                                        }
                                                                        builder.suggest(".none");
                                                                    }
                                                                    return builder.buildFuture();
                                                                }
                                                        ).then(argument("ippatsu", StringArgumentType.string())
                                                                .suggests(
                                                                        (context, builder) -> {
                                                                            if (context.getCommand() == null) {
                                                                                String riichiArgument = getString(context, "riichi");
                                                                                if (!riichiArgument.equals(".none")) {
                                                                                    builder.suggest("ippatsu");
                                                                                }
                                                                                builder.suggest(".none");
                                                                            }
                                                                            return builder.buildFuture();
                                                                        }
                                                                ).then(argument("winning tile", StringArgumentType.string())
                                                                        .suggests(
                                                                                (context, builder) -> {
                                                                                    if (context.getCommand() == null) {
                                                                                        String winArgument = getString(context, "win");
                                                                                        if (winArgument.equals(".tsumo")) {
                                                                                            builder.suggest("haitei");
                                                                                            builder.suggest("rinshan");
                                                                                        } else if (winArgument.equals(".ron")) {
                                                                                            builder.suggest("houtei");
                                                                                            builder.suggest("chankan");
                                                                                        }
                                                                                        builder.suggest(".none");
                                                                                    }
                                                                                    return builder.buildFuture();
                                                                                }
                                                                        ).executes(new CalculateHandler())
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
