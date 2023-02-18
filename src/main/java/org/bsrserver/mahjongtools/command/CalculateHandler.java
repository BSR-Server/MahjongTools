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
import org.bsrserver.mahjongtools.algorithm.Tile;
import org.bsrserver.mahjongtools.algorithm.Mahjong;
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

            // get data
            String[] mainMahjongNames = player.getMainMahjongNames();
            String[][] meldMahjongNames = player.getMeldMahjongNames();
            boolean[] meldArrows = player.getMeldArrows();
            String[] doraIndicators = player.getDoraIndicators();
            String[] hiddenDoreIndicators = player.getHiddenDoraIndicators();
            String prevailingWind = player.getPrevailingWind();
            String seatWind = player.getSeatWind();
            LogManager.getLogger().info("Player " + playerArgument + " has main mahjong names: " + Arrays.toString(mainMahjongNames));
            LogManager.getLogger().info("Player " + playerArgument + " has meld mahjong names: " + Arrays.deepToString(meldMahjongNames));
            LogManager.getLogger().info("Player " + playerArgument + " has meld arrows: " + Arrays.toString(meldArrows));
            LogManager.getLogger().info("Player " + playerArgument + " has dora indicators: " + Arrays.toString(doraIndicators));
            LogManager.getLogger().info("Player " + playerArgument + " has hidden dora indicators: " + Arrays.toString(hiddenDoreIndicators));
            LogManager.getLogger().info("Player " + playerArgument + " has prevailing wind: " + prevailingWind);
            LogManager.getLogger().info("Player " + playerArgument + " has seat wind: " + seatWind);

            // new Mahjong instance
            Mahjong mahjong = new Mahjong(
                    Arrays.copyOfRange(mainMahjongNames, 0, 13),
                    mainMahjongNames[13],
                    meldMahjongNames,
                    meldArrows,
                    doraIndicators,
                    hiddenDoreIndicators
            );
            mahjong.setPrevailingWind(new Tile(prevailingWind));
            mahjong.setSeatWind(new Tile(player.getSeatWind()));
            mahjong.tryToSplit();
            mahjong.getMaxScore();

            // reply
            reply(context.getSource().getMinecraftServer().getPlayerManager().getPlayerList(), new LiteralText(mahjong.toString()));
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
