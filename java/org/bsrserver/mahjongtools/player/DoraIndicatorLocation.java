package org.bsrserver.mahjongtools.player;

public class DoraIndicatorLocation extends Location {
    static int startX = -963;
    static int startZ = -2116;
    static int directionX = 1;
    static int directionZ = 0;

    @Override
    int getX(int offset) {
        return startX + directionX * offset;
    }

    @Override
    int getZ(int offset) {
        return startZ + directionZ * offset;
    }
}
