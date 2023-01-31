package org.bsrserver.mahjongtools.player;

public class MeldMahjongLocation extends Location {
    public MeldMahjongLocation(int location) {
        switch (location) {
            case 1:
                startX = -947;
                startZ = -2122;
                directionX = 1;
                directionZ = -1;
                break;
            case 2:
                startX = -970;
                startZ = -2126;
                directionX = -1;
                directionZ = -1;
                break;
            case 3:
                startX = -974;
                startZ = -2103;
                directionX = -1;
                directionZ = 1;
                break;
            case 4:
                startX = -951;
                startZ = -2099;
                directionX = 1;
                directionZ = 1;
                break;
        }
    }

    public int get2DArrayX(int offset) {
        return offset / 4;
    }

    public int get2DArrayY(int offset) {
        return offset % 4;
    }

    @Override
    public int getX(int offset) {
        return startX + get2DArrayX(offset) * directionX;
    }

    @Override
    public int getZ(int offset) {
        return startZ + get2DArrayY(offset) * directionZ;
    }
}
