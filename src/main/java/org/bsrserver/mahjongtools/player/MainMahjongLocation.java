package org.bsrserver.mahjongtools.player;

public class MainMahjongLocation extends Location {
    public MainMahjongLocation(int location) {
        switch (location) {
            case 1:
                startX = -948;
                startZ = -2105;
                directionX = 0;
                directionZ = -1;
                break;
            case 2:
                startX = -953;
                startZ = -2125;
                directionX = -1;
                directionZ = 0;
                break;
            case 3:
                startX = -973;
                startZ = -2120;
                directionX = 0;
                directionZ = 1;
                break;
            case 4:
                startX = -968;
                startZ = -2100;
                directionX = 1;
                directionZ = 0;
                break;
        }
    }

    @Override
    public int getX(int offset) {
        if (offset < 13) {
            return startX + directionX * offset;
        } else {
            return startX + directionX * 15;
        }
    }

    @Override
    public int getZ(int offset) {
        if (offset < 13) {
            return startZ + directionZ * offset;
        } else {
            return startZ + directionZ * 15;
        }
    }
}
