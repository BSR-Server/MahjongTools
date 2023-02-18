package org.bsrserver.mahjongtools.algorithm;

public class Yakuman {
    
    private String yakuman;
    private int times;
    
    public static final Yakuman THIRTEEN_ORPHANS                  = new Yakuman("国士无双", 1);
    public static final Yakuman THIRTEEN_ORPHANS_13_WAITS         = new Yakuman("国士无双十三面", 1);
    public static final Yakuman FOUR_CLOSED_TRIPLETS              = new Yakuman("四暗刻", 1);
    public static final Yakuman FOUR_CLOSED_TRIPLETS_SINGLE_WAIT  = new Yakuman("四暗刻单骑", 1);
    public static final Yakuman BIG_THREE_DRAGONS                 = new Yakuman("大三元", 1);
    public static final Yakuman LITTLE_FOUR_WINDS                 = new Yakuman("小四喜", 1);
    public static final Yakuman BIG_FOUR_WINDS                    = new Yakuman("大四喜", 1);
    public static final Yakuman ALL_HONORS                        = new Yakuman("字一色", 1);
    public static final Yakuman ALL_TERMINALS                     = new Yakuman("清老头", 1);
    public static final Yakuman ALL_GREEN                         = new Yakuman("绿一色", 1);
    public static final Yakuman NINE_GATES                        = new Yakuman("九莲宝灯", 1);
    public static final Yakuman NINE_GATES_9_WAITS                = new Yakuman("纯正九莲宝灯", 1);
    public static final Yakuman FOUR_QUADS                        = new Yakuman("四杠子", 1);
    public static final Yakuman HEAVENLY_HAND                     = new Yakuman("天和", 1);
    public static final Yakuman HAND_OF_EARTH                     = new Yakuman("地和", 1);
    
    public Yakuman(String yakuman, int times){
        this.yakuman = yakuman;
        this.times = times;
    }
    
    public String toString(){
        return yakuman + "\n";
    }
}
