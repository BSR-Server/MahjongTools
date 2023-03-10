package org.bsrserver.mahjongtools.player;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.world.ServerWorld;

import org.bsrserver.mahjongtools.Utils;

import java.util.Objects;

public class Player {
    private final ServerWorld serverWorld;
    private final String seatWind;
    private final MainMahjongLocation mainMahjongLocation;
    private final MeldMahjongLocation meldMahjongLocation;
    private final MeldArrowLocation meldArrowLocation;
    private final DoraIndicatorLocation doraIndicatorLocation;
    private final HiddenDoraIndicatorLocation hiddenDoraIndicatorLocation;

    public Player(ServerWorld serverWorld, String seatWind, int location) {
        this.serverWorld = serverWorld;
        this.seatWind = seatWind;
        this.mainMahjongLocation = new MainMahjongLocation(location);
        this.meldMahjongLocation = new MeldMahjongLocation(location);
        this.meldArrowLocation = new MeldArrowLocation(location);
        this.doraIndicatorLocation = new DoraIndicatorLocation();
        this.hiddenDoraIndicatorLocation = new HiddenDoraIndicatorLocation();
    }

    public String[] getMainMahjongNames() {
        String[] result = new String[14];
        for (int i = 0; i < 14; i++) {
            result[i] = Utils.getItemFrameNameByPos(serverWorld, mainMahjongLocation.getX(i), 32, mainMahjongLocation.getZ(i));
        }
        return result;
    }

    public String[][] getMeldMahjongNames() {
        String[][] result = new String[4][4];
        for (int i = 0; i < 16; i++) {
            String meldMahjongName = Utils.getItemFrameNameByPos(serverWorld, meldMahjongLocation.getX(i), 32, meldMahjongLocation.getZ(i));
            result[meldMahjongLocation.get2DArrayX(i)][meldMahjongLocation.get2DArrayY(i)] = meldMahjongName;
        }
        return meldMahjongLocation.getRotatedLocation(result);
    }

    public boolean[] getMeldArrows() {
        boolean[] result = new boolean[4];
        for (int i = 0; i < 4; i++) {
            ItemFrameEntity itemFrameEntity = Utils.getItemFrameEntity(serverWorld, meldArrowLocation.getX(i), 32, meldArrowLocation.getZ(i));
            result[i] = meldArrowLocation.getSelfRotation() == Objects.requireNonNull(itemFrameEntity).getRotation();
        }
        return result;
    }

    public String[] getDoraIndicators() {
        String[] result = new String[5];
        for (int i = 0; i < 5; i++) {
            result[i] = Utils.getItemFrameNameByPos(serverWorld, doraIndicatorLocation.getX(i), 32, doraIndicatorLocation.getZ(i));
        }
        return result;
    }

    public String[] getHiddenDoraIndicators() {
        String[] result = new String[5];
        for (int i = 0; i < 5; i++) {
            result[i] = Utils.getItemFrameNameByPos(serverWorld, hiddenDoraIndicatorLocation.getX(i), 32, hiddenDoraIndicatorLocation.getZ(i));
        }
        return result;
    }

    public String getPrevailingWind() {
        return Utils.getItemFrameNameByPos(serverWorld, -958, 32, -2115);
    }

    public String getSeatWind() {
        switch (seatWind) {
            case "E":
                return "z1";
            case "S":
                return "z2";
            case "W":
                return "z3";
            case "N":
                return "z4";
            default:
                return null;
        }
    }
}
