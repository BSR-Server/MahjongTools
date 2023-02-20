package org.bsrserver.mahjongtools.player;

import java.util.Arrays;

public class MeldMahjongLocation extends Location {
    boolean rotate;

    public MeldMahjongLocation(int location) {
        switch (location) {
            case 1:
                startX = -947;
                startZ = -2122;
                directionX = 1;
                directionZ = -1;
                rotate = false;
                break;
            case 2:
                startX = -970;
                startZ = -2126;
                directionX = -1;
                directionZ = -1;
                rotate = true;
                break;
            case 3:
                startX = -974;
                startZ = -2103;
                directionX = -1;
                directionZ = 1;
                rotate = false;
                break;
            case 4:
                startX = -951;
                startZ = -2099;
                directionX = 1;
                directionZ = 1;
                rotate = true;
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

    public String[][] getRotatedLocation(String[][] array) {
        if (rotate) {
            String[][] newArray = new String[4][4];
            for (int i = 0; i < array[0].length; i++) {
                for (int j = array.length - 1; j >= 0; j--) {
                    newArray[i][j] = array[j][i];
                }
            }
            array = newArray;
        }
        return array;
    }

    public static void main(String[] args) {
        System.out.println(
                Arrays.deepToString(
                        new MeldMahjongLocation(2).getRotatedLocation(
                                new String[][]{
                                        {"1", "2", "3", "4"},
                                        {"5", "6", "7", "8"},
                                        {"9", "10", "11", "12"},
                                        {"13", "14", "15", "16"}
                                }
                        )
                )
        );
    }
}
