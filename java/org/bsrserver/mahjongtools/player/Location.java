package org.bsrserver.mahjongtools.player;

public abstract class Location {
    int startX;
    int startZ;
    int directionX;
    int directionZ;

    abstract int getX(int offset);

    abstract int getZ(int offset);
}
