package org.bsrserver.mahjongtools.command;

import java.util.List;
import java.util.Arrays;

import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import org.apache.logging.log4j.LogManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.world.World;
import org.bsrserver.mahjongtools.Utils;
import org.bsrserver.mahjongtools.player.Player;
import org.bsrserver.mahjongtools.exception.PlayerNotPlayingException;
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

    private boolean isPlayerInRange(ServerPlayerEntity serverPlayerEntity) {
        boolean isOverWorld = serverPlayerEntity.getEntityWorld().getRegistryKey() == World.OVERWORLD;
        boolean isInXRange = -979 < serverPlayerEntity.getPos().getX() && serverPlayerEntity.getPos().getX() < -942;
        boolean isInYRange = 31 < serverPlayerEntity.getPos().getY() && serverPlayerEntity.getPos().getY() < 37;
        boolean isInZRange = -2131 < serverPlayerEntity.getPos().getZ() && serverPlayerEntity.getPos().getZ() < -2094;
        return isOverWorld && isInXRange && isInYRange && isInZRange;
    }

    private void reply(List<ServerPlayerEntity> serverPlayerEntities, MutableText message) {
        for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
            if (isPlayerInRange(serverPlayerEntity)) {
                serverPlayerEntity.sendMessage(message, false);
            }
        }
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        try {
            // check player location
            Entity entity = context.getSource().getEntity();
            boolean shouldThrowException = true;
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
                if (isPlayerInRange(serverPlayerEntity)) {
                    shouldThrowException = false;
                }
            }
            if (shouldThrowException) {
                throw new PlayerNotPlayingException();
            }

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
            reply(context.getSource().getMinecraftServer().getPlayerManager().getPlayerList(), new LiteralText(""));
        } catch (IllegalPlayerException exception) {
            context.getSource().sendError(new LiteralText("Illegal Player!"));
        } catch (PlayerNotPlayingException exception) {
            context.getSource().sendError(new LiteralText("You are not playing!"));
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendError(new LiteralText(e.toString()));
            for (StackTraceElement element : e.getStackTrace()) {
                context.getSource().sendError(new LiteralText(element.toString()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
