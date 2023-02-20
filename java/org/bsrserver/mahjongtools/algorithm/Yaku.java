package org.bsrserver.mahjongtools.algorithm;

public class Yaku {

    private String yaku;
    private int han;

    public static final Yaku RIICHI                             = new Yaku("立直", 1);
    public static final Yaku CLOSED_HAND_SELF_DRAWN             = new Yaku("门前清自摸和", 1);
    public static final Yaku ONE_SHOT                           = new Yaku("一发",1);
    public static final Yaku LAST_TILE_FROM_THE_WALL            = new Yaku("海底捞月",1);
    public static final Yaku LAST_DISCARD                       = new Yaku("河底摸鱼",1);
    public static final Yaku DEAD_WALL_DRAW                     = new Yaku("岭上开花",1);
    public static final Yaku ROBBING_A_QUAD                     = new Yaku("抢杠",1);
    public static final Yaku DOUBLE_RIICHI                      = new Yaku("双立直",2);

    public static final Yaku SEVEN_PAIRS                        = new Yaku("七对子", 2);
    public static final Yaku NO_POINT_HAND                      = new Yaku("平和",1);
    public static final Yaku ONE_SET_OF_IDENTICAL_SEQUENCES     = new Yaku("一杯口",1);
    public static final Yaku THREE_COLOR_SEQUENCES              = new Yaku("三色同顺",2);
    public static final Yaku THREE_COLOR_SEQUENCES_             = new Yaku("三色同顺",1);
    public static final Yaku STRAIGHT                           = new Yaku("一气贯通",2);
    public static final Yaku STRAIGHT_                          = new Yaku("一气贯通",1);
    public static final Yaku TWO_SETS_OF_IDENTICAL_SEQUENCES    = new Yaku("两杯口",3);
    public static final Yaku ALL_TRIPLETS                       = new Yaku("对对和",2);
    public static final Yaku THREE_CLOSED_TRIPLETS              = new Yaku("三暗刻",2);
    public static final Yaku THREE_COLOR_TRIPLETS               = new Yaku("三色同刻",2);
    public static final Yaku THREE_QUADS                        = new Yaku("三杠子",2);
    public static final Yaku ALL_SIMPLES                        = new Yaku("断幺九",1);
    public static final Yaku HONOR_WHITE                        = new Yaku("役牌：白",1);
    public static final Yaku HONOR_GREEN                        = new Yaku("役牌：发",1);
    public static final Yaku HONOR_RED                          = new Yaku("役牌：中",1);
    public static final Yaku SEAT_WIND                          = new Yaku("役牌：门风牌",1);
    public static final Yaku PREVAILING_WIND                    = new Yaku("役牌：场风牌",1);
    public static final Yaku TERMINAL_OR_HONOR_IN_EACH_MELD     = new Yaku("混全带幺九",2);
    public static final Yaku TERMINAL_OR_HONOR_IN_EACH_MELD_    = new Yaku("混全带幺九",1);
    public static final Yaku TERMINAL_IN_EACH_MELD              = new Yaku("纯全带幺九",3);
    public static final Yaku TERMINAL_IN_EACH_MELD_             = new Yaku("纯全带幺九",2);
    public static final Yaku ALL_TERMINALS_AND_HONORS           = new Yaku("混老头",2);
    public static final Yaku LITTLE_THREE_DRAGONS               = new Yaku("小三元",2);
    public static final Yaku HALF_FLUSH                         = new Yaku("混一色",3);
    public static final Yaku HALF_FLUSH_                        = new Yaku("混一色",2);
    public static final Yaku FLUSH                              = new Yaku("清一色",6);
    public static final Yaku FLUSH_                             = new Yaku("清一色",5);

    public static final Yaku DORA                               = new Yaku("宝牌",1);
    public static final Yaku RED_TILE                           = new Yaku("赤宝牌",1);
    public static final Yaku HIDDEN_DORA                        = new Yaku("里宝牌",1);

    public Yaku(String yaku, int han) {
        this.yaku = yaku;
        this.han = han;
    }

    public int getHan(){
        return han;
    }

    public void setHan(int han){
        this.han = han;
    }

    public String toString(){
        return yaku + " " + han + "番\n";
    }
}
