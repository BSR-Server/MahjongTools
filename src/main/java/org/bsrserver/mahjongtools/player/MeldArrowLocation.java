package org.bsrserver.mahjongtools.player;

public class MeldArrowLocation extends Location {
    private int selfRotation;

    public MeldArrowLocation(int location) {
        switch (location) {
            case 1:
                startX = -947;
                startZ = -2121;
                directionX = 1;
                directionZ = 0;
                selfRotation = 1;
                break;
            case 2:
                startX = -969;
                startZ = -2126;
                directionX = 0;
                directionZ = -1;
                selfRotation = 7;
                break;
            case 3:
                startX = -974;
                startZ = -2104;
                directionX = -1;
                directionZ = 0;
                selfRotation = 5;
                break;
            case 4:
                startX = -952;
                startZ = -2099;
                directionX = 0;
                directionZ = 1;
                selfRotation = 3;
                break;
        }
    }

    @Override
    public int getX(int offset) {
        return startX + directionX * offset;
    }

    @Override
    public int getZ(int offset) {
        return startZ + directionZ * offset;
    }

    public int getSelfRotation() {
        return selfRotation;
    }
}
