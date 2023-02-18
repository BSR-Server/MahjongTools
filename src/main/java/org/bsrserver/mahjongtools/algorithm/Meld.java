package org.bsrserver.mahjongtools.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

public class Meld implements Comparable{

    private Tile[] tiles;
    private int type;


    public static final int SEQUENCE = 1;
    public static final int OPEN_SEQUENCE = 21;
    public static final int TRIPLET = 2;
    public static final int CLOSED_QUAD = 12;
    public static final int OPEN_TRIPLET = 22;
    public static final int OPEN_QUAD = 32;
    public static final int PAIR = 3;
    public static final int THIRTEEN_ORPHANS = 4;

    public Meld(Tile[] tiles){
        this.tiles = tiles;
    }

    public Meld(String[] ts){
        this.tiles = new Tile[ts.length];
        for (int i = 0; i < ts.length; i++){
            tiles[i] = new Tile(ts[i]);
        }
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public char getSuit(){
        return tiles[0].getSuit();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTriplet(){
        if (type == TRIPLET || type == OPEN_TRIPLET){
            return true;
        }
        if (tiles.length != 3){
            return false;
        }
        return tiles[0].equals(tiles[1]) && tiles[1].equals(tiles[2]);
    }

    public boolean isSequence(){
        if (type == SEQUENCE || type == OPEN_SEQUENCE){
            return true;
        }
        if (tiles.length != 3){
            return false;
        }
        Arrays.sort(tiles);
        return tiles[0].isContinue(tiles[1]) && tiles[1].isContinue(tiles[2]);
    }

    public boolean isQuad(){
        if (type == OPEN_QUAD || type == CLOSED_QUAD){
            return true;
        }
        if (tiles.length != 4){
            return false;
        }
        return tiles[0].equals(tiles[1]) && tiles[1].equals(tiles[2]) && tiles[2].equals(tiles[3]);
    }

    public int indexOf(Tile tile){
        return Arrays.binarySearch(tiles, tile);
    }

    public boolean onlyContains(Tile[] tiles){
        for (Tile t: this.tiles){
            if (Arrays.binarySearch(tiles, t) == -1){
                return false;
            }
        }
        return true;
    }

    public String toString(){
        String type = "";
        switch (this.type){
            case PAIR:
                type = "pair";
                break;
            case SEQUENCE:
                type = "sequence";
                break;
            case TRIPLET:
                type = "triplet";
                break;
            case OPEN_SEQUENCE:
                type = "open_sequence";
                break;
            case OPEN_TRIPLET:
                type = "open_triplet";
                break;
            case CLOSED_QUAD:
                type = "closed_quad";
                break;
            case OPEN_QUAD:
                type = "open_quad";
                break;
            case THIRTEEN_ORPHANS:
                type = "thirteen_orphans";
                break;
            default:
        }
        return type + ": " + Arrays.toString(tiles);
    }

    public boolean equals(Meld other){
        return this.type == other.type && this.tiles[0].equals(other.tiles[0]);
    }

    public boolean hasEqualValues(Meld other){
        if (this.type % 10 == other.type % 10){
            return this.tiles[0].getValue() == other.tiles[0].getValue();
        }
        else {
            return false;
        }
    }

    @Override
    public int compareTo(Object o) {
        Meld other = (Meld) o;
        if (this.type % 10 != other.type % 10){
            return this.type % 10 - other.type % 10;
        }
        else {
            return this.tiles[0].compareTo(other.tiles[0]);
        }
    }
}
