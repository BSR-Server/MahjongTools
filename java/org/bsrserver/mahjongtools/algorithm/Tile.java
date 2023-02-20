package org.bsrserver.mahjongtools.algorithm;

public class Tile implements Comparable{

    private char suit;
    private int value;
    private boolean isRed;

    public static final String m0 = "m5_a4";
    public static final String m1 = "m1";
    public static final String m2 = "m2";
    public static final String m3 = "m3";
    public static final String m4 = "m4";
    public static final String m5 = "m5";
    public static final String m6 = "m6";
    public static final String m7 = "m7";
    public static final String m8 = "m8";
    public static final String m9 = "m9";
    public static final String p0 = "p5_a4";
    public static final String p1 = "p1";
    public static final String p2 = "p2";
    public static final String p3 = "p3";
    public static final String p4 = "p4";
    public static final String p5 = "p5";
    public static final String p6 = "p6";
    public static final String p7 = "p7";
    public static final String p8 = "p8";
    public static final String p9 = "p9";
    public static final String s0 = "s5_a4";
    public static final String s1 = "s1";
    public static final String s2 = "s2";
    public static final String s3 = "s3";
    public static final String s4 = "s4";
    public static final String s5 = "s5";
    public static final String s6 = "s6";
    public static final String s7 = "s7";
    public static final String s8 = "s8";
    public static final String s9 = "s9";
    public static final String z1 = "z1";
    public static final String z2 = "z2";
    public static final String z3 = "z3";
    public static final String z4 = "z4";
    public static final String z5 = "z5";
    public static final String z6 = "z6";
    public static final String z7 = "z7";




    public Tile(String tile){
        suit = tile.charAt(0);
        value = tile.charAt(1) - '0';
        isRed = value == 0;
        if (isRed){
            value = 5;
        }
    }

    public char getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public boolean isRed() {
        return isRed;
    }

    /**
     * 获取这张牌的下一张牌
     * @return 这张牌的下一张牌
     */
    public Tile getNext() {
        if (suit == 'z') {
            if (value < 5) {
                return new Tile(suit + "" + (value % 4 + 1));
            } else {
                return new Tile(suit + "" + (4 + (value - 4) % 3 + 1));
            }
        } else {
            return new Tile(suit + "" + (value % 9 + 1));
        }
    }

    /**
     * 检查输入的牌是否是能与这张牌构成顺子的下一张
     * @return true 如果输入的牌是能与这张牌构成顺子的下一张，否则返回 false
     */
    public boolean isContinue(Tile other) {
        if (this.suit != other.suit || this.suit == 'z') {
            return false;
        } else if (other.value - this.value != 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查这张牌是否是三元牌
     * @return true如果是三元牌，否则返回false
     */
    public boolean isDragon(){
        return suit == 'z' && value >= 5;
    }

    /**
     * 检查这张牌是否是风牌
     * @return true如果是风牌，否则返回false
     */
    public boolean isWind(){
        return suit == 'z' && value < 5;
    }

    /**
     * 检查这张牌是否是幺九牌
     * @return true如果是幺九牌，否则返回false
     */
    public boolean isTerminal(){
        return suit != 'z' && (value == 1 || value == 9);
    }

    public boolean isGreen(){
        return suit == 's' && (value == 2 || value == 3 || value == 4 || value == 6 || value == 8) || suit == 'z' && value == 6;
    }

    public boolean isTerminalOrHonor(){
        return suit == 'z' || value == 1 || value == 9;
    }

    @Override
    public String toString(){
        return suit + "" + (isRed? 0: value);
    }

    public boolean equals(Tile other){
        return this.suit == other.suit && this.value == other.value;
    }

    public static String renameTile(String tile){
        if (tile.matches("[mps]5_.4")) {
            return tile.charAt(0) + "0";
        }
        return tile.substring(0, 2);
    }

    @Override
    public int compareTo(Object o) {
        Tile other = (Tile) o;
        if (this.suit < other.suit || this.suit == other.suit && this.value < other.value) {
            return -1;
        }
        else if(this.equals(other)){
            return 0;
        }
        else {
            return 1;
        }
    }


}
