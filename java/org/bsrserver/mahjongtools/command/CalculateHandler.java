package org.bsrserver.mahjongtools.command;

import java.util.List;
import java.util.Arrays;

import com.mojang.brigadier.arguments.StringArgumentType;
import org.apache.logging.log4j.LogManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.LiteralText;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

import org.bsrserver.mahjongtools.Utils;
import org.bsrserver.mahjongtools.algorithm.Tile;
import org.bsrserver.mahjongtools.algorithm.Mahjong;
import org.bsrserver.mahjongtools.exception.*;
import org.bsrserver.mahjongtools.player.Player;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

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
            String playerArgument = getString(context,"player");
            System.out.println(playerArgument);
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

            String winArgument;
            try {
                winArgument = getString(context, "win");
            }catch (IllegalArgumentException e){
                winArgument = ".ron";
            }
            switch (winArgument){
                case "tenho":
                    mahjong.setHeavenlyHand(true);
                    break;
                case "chiho":
                    mahjong.setHandOfEarth(true);
                    break;
                case ".tsumo":
                    mahjong.setSelfDrawn(true);
                    break;
                case ".ron":
                    break;
                default:
                    throw new IllegalParamException();
            }

            String riichiArgument;
            try {
                riichiArgument = getString(context, "riichi");
            }
            catch (IllegalArgumentException e){
                riichiArgument = ".none";
            }
            switch (riichiArgument){
                case "riichi":
                    if (winArgument.equals("tenho") || winArgument.equals("chiho")){
                        throw new IllegalParamException();
                    }
                    mahjong.setRiichi(1);
                    break;
                case "w_riichi":
                    if (winArgument.equals("tenho") || winArgument.equals("chiho")){
                        throw new IllegalParamException();
                    }
                    mahjong.setRiichi(2);
                case ".none":
                    break;
                default:
                    throw new IllegalParamException();
            }

            String ippatsuArgument;
            try {
                ippatsuArgument = getString(context, "ippatsu");
            }catch (IllegalArgumentException e){
                ippatsuArgument = ".none";
            }
            switch (ippatsuArgument){
                case "ippatsu":
                    if (riichiArgument.equals(".none")){
                        throw new IllegalParamException();
                    }
                    mahjong.setOneShot(true);
                    break;
                case ".none":
                    break;
                default:
                    throw new IllegalParamException();
            }

            String winningTileArgument;
            try {
                winningTileArgument = getString(context, "winning tile");
            }catch (IllegalArgumentException e){
                winningTileArgument = ".none";
            }
            switch (winningTileArgument){
                case "haitei":
                    if (!winArgument.equals(".tsumo")){
                        throw new IllegalParamException();
                    }
                    mahjong.setLastTileFromTheWall(true);
                    break;
                case "houtei":
                    if (!winArgument.equals(".ron")){
                        throw new IllegalParamException();
                    }
                    mahjong.setLastDiscard(true);
                    break;
                case "rinshan":
                    if (!winArgument.equals(".tsumo")){
                        throw new IllegalParamException();
                    }
                    mahjong.setDeadWallDraw(true);
                    break;
                case "chankan":
                    if (!winArgument.equals(".ron")){
                        throw new IllegalParamException();
                    }
                    mahjong.setRobbingAQuad(true);
                case ".none":
                    break;
                default:
                    throw new IllegalParamException();
            }


            mahjong.tryToSplit();
            mahjong.getMaxScore();

            // reply
            reply(context.getSource().getMinecraftServer().getPlayerManager().getPlayerList(), new LiteralText("\n" + mahjong.toString()));

        } catch (IllegalPlayerException exception) {
            context.getSource().sendError(new LiteralText("该玩家不存在！"));
        } catch (PlayerNotPlayingException exception) {
            context.getSource().sendError(new LiteralText("你不在麻将机的范围内！"));
        } catch (IllegalParamException e) {
            context.getSource().sendError(new LiteralText("参数不合法！"));
        } catch (IncompleteHandException e) {
            context.getSource().sendError(new LiteralText("手牌不完整！"));
        } catch (IllegalHandException e) {
            context.getSource().sendError(new LiteralText("手牌不合法！"));
        } catch (NotWinException e) {
            context.getSource().sendError(new LiteralText("不是可以胡牌的牌型！"));
        } catch (NoYakuException e) {
            context.getSource().sendError(new LiteralText("无役！"));
        }

        catch (Exception e) {
            e.printStackTrace();
            context.getSource().sendError(new LiteralText(e.toString()));
            for (StackTraceElement element : e.getStackTrace()) {
                context.getSource().sendError(new LiteralText(element.toString()));
            }
        }
        return Command.SINGLE_SUCCESS;
    }
}
