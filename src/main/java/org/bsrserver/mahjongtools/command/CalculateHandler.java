package org.bsrserver.mahjongtools.command;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.ServerCommandSource;

import org.bsrserver.mahjongtools.Utils;
import org.bsrserver.mahjongtools.player.Player;
import org.bsrserver.mahjongtools.exception.IllegalPlayerException;

public class CalculateHandler implements Command<ServerCommandSource> {
    private ServerWorld overworld;

    private Player getPlayer(String location) {
        if (location.equals("E") || location.equals("S") || location.equals("W") || location.equals("N")) {
            if (location.equals(Utils.getItemFrameNameByPos(overworld, -956, 32, -2108))) {
                return new Player(overworld, location, 1);
            } else if (location.equals(Utils.getItemFrameNameByPos(overworld, -956, 32, -2117))) {
                return new Player(overworld, location, 2);
            } else if (location.equals(Utils.getItemFrameNameByPos(overworld, -965, 32, -2117))) {
                return new Player(overworld, location, 3);
            } else if (location.equals(Utils.getItemFrameNameByPos(overworld, -965, 32, -2108))) {
                return new Player(overworld, location, 4);
            }
        }
        throw new IllegalPlayerException();
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        try {
            // save overworld
            overworld = context.getSource().getMinecraftServer().getOverworld();

            // get names
            String playerArgument = context.getArgument("player", String.class);
            Player player = getPlayer(playerArgument);

            // log
            String mainMahjongNames = Arrays.toString(player.getMainMahjongNames());
            String meldMahjongNames = Arrays.deepToString(player.getMeldMahjongNames());
            String meldArrows = Arrays.toString(player.getMeldArrows());
            LogManager.getLogger().info("Player " + playerArgument + " has main mahjong names: " + mainMahjongNames);
            LogManager.getLogger().info("Player " + playerArgument + " has meld mahjong names: " + meldMahjongNames);
            LogManager.getLogger().info("Player " + playerArgument + " has meld arrows: " + meldArrows);

            // reply
            for (ServerPlayerEntity serverPlayerEntity : context.getSource().getMinecraftServer().getPlayerManager().getPlayerList()) {
                serverPlayerEntity.sendMessage(new LiteralText(mainMahjongNames), false);
                serverPlayerEntity.sendMessage(new LiteralText(meldMahjongNames), false);
                serverPlayerEntity.sendMessage(new LiteralText(meldArrows), false);
            }
        } catch (IllegalPlayerException ignored) {
            context.getSource().sendError(new LiteralText("Illegal Player!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }
}
