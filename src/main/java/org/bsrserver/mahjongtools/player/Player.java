package org.bsrserver.mahjongtools.player;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.server.world.ServerWorld;

import org.bsrserver.mahjongtools.Utils;

import java.util.Objects;

public class Player {
    private final ServerWorld serverWorld;
    private final MainMahjongLocation mainMahjongLocation;
    private final MeldMahjongLocation meldMahjongLocation;
    private final MeldArrowLocation meldArrowLocation;

    public Player(ServerWorld serverWorld, int location) {
        this.serverWorld = serverWorld;
        this.mainMahjongLocation = new MainMahjongLocation(location);
        this.meldMahjongLocation = new MeldMahjongLocation(location);
        this.meldArrowLocation = new MeldArrowLocation(location);
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
}
