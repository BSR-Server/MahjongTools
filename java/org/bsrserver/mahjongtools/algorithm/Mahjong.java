package org.bsrserver.mahjongtools.algorithm;

import org.bsrserver.mahjongtools.exception.IllegalHandException;
import org.bsrserver.mahjongtools.exception.IncompleteHandException;
import org.bsrserver.mahjongtools.exception.NoYakuException;
import org.bsrserver.mahjongtools.exception.NotWinException;

import java.util.*;

import static org.bsrserver.mahjongtools.algorithm.Tile.*;
import static org.bsrserver.mahjongtools.algorithm.Tile.z1;

public class Mahjong {

    private ArrayList<Tile> origHand;
    private ArrayList<Meld> openMelds;
    private Tile winningTile;

    private ArrayList<Tile> doraIndicators;
    private ArrayList<Tile> doras;
    private ArrayList<Tile> hiddenDoraIndicators;
    private ArrayList<Tile> hiddenDoras;
    private int redTileCount;

    private Tile seatWind;
    private Tile prevailingWind;


    private boolean closedHand;
    private boolean selfDrawn;
    private int riichi;
    private boolean oneShot;
    private boolean lastTileFromTheWall;
    private boolean lastDiscard;
    private boolean deadWallDraw;
    private boolean robbingAQuad;
    private boolean heavenlyHand;
    private boolean handOfEarth;

    private ArrayList<Hand> waysToSplit;
    private Hand maxScoreHand;

    public Mahjong(String[] hand, String winningTile, String[][] openMelds, boolean[] meldsFromSelf, String[] doraIndicators, String[] hiddenDoraIndicators) {
        hand = renameTiles(hand);
        if (winningTile.equals("") || hand.length % 3 != 1){
            throw new IncompleteHandException();
        }
        this.winningTile = new Tile(Tile.renameTile(winningTile));
        Arrays.sort(hand, (t1, t2) -> {
            String s1 = t1.charAt(1) == '0'? t1.charAt(0) + "5" : t1;
            String s2 = t2.charAt(1) == '0'? t2.charAt(0) + "5" : t2;
            return s1.compareTo(s2);
        });
        origHand = new ArrayList<Tile>();

        for (String t: hand){
            origHand.add(new Tile(t));
        }
        redTileCount += CountRedTiles(origHand);
        this.openMelds = new ArrayList<Meld>();
        closedHand = true;
        for (int i = 0; i < openMelds.length; i++) {
            String[] m = openMelds[i];
            m = renameTiles(m);
            if (m.length > 0) {
                Meld meld = new Meld(m);
                redTileCount += CountRedTiles(Arrays.asList(meld.getTiles()));
                int type = 0;
                if (meld.isSequence()) {
                    type = Meld.OPEN_SEQUENCE;
                } else if (meld.isTriplet()) {
                    type = Meld.OPEN_TRIPLET;
                } else if (meld.isQuad()) {
                    if (meldsFromSelf[i]) {
                        type = Meld.CLOSED_QUAD;
                    } else {
                        type = Meld.OPEN_QUAD;
                    }
                }
                else {
                    System.out.println("wrong open melds");
                    throw new IllegalHandException();
                }
                if (type > 20 && closedHand) {
                    closedHand = false;
                }
                meld.setType(type);
                this.openMelds.add(meld);
            }
        }

        if(this.openMelds.size() * 3 + origHand.size() != 13){
            throw new IncompleteHandException();
        }

        this.doraIndicators = new ArrayList<Tile>();
        doraIndicators = renameTiles(doraIndicators);
        for (String t: doraIndicators){
            this.doraIndicators.add(new Tile(t));
        }
        this.doras = getDoras(this.doraIndicators);
        this.hiddenDoraIndicators = new ArrayList<Tile>();
        hiddenDoraIndicators = renameTiles(hiddenDoraIndicators);
        for (String t: hiddenDoraIndicators){
            this.hiddenDoraIndicators.add(new Tile(t));
        }
        this.hiddenDoras = getDoras(this.hiddenDoraIndicators);
    }

    public String getTiles(){
        String hand = origHand.toString();

        if (this.openMelds.size() > 0) {
            String openMelds = "";
            for (Meld m : this.openMelds) {
                if (m.getType() == Meld.CLOSED_QUAD){
                    Tile[] t = m.getTiles();
                    openMelds += "[__, " + t[1] + ", " + t[2] + ", __]";
                }
                else {
                    openMelds += Arrays.toString(m.getTiles());
                }
            }
            hand += openMelds;
        }

        hand += " " + winningTile;

        String[] doraIndicators = new String[5];
        Arrays.fill(doraIndicators,"__");
        for (int i = 0; i < this.doraIndicators.size(); i++){
            doraIndicators[i] = this.doraIndicators.get(i).toString();
        }
        String[] hiddenDoraIndicators = new String[5];
        Arrays.fill(hiddenDoraIndicators,"__");
        for (int i = 0; i < this.hiddenDoraIndicators.size(); i++){
            hiddenDoraIndicators[i] = this.hiddenDoraIndicators.get(i).toString();
        }

        return hand + " " + (selfDrawn? "自摸": "荣和")+ "\n" +
            Arrays.toString(doraIndicators) + "                " + Arrays.toString(hiddenDoraIndicators);

    }

    public ArrayList<Meld> getOpenMelds() {
        return openMelds;
    }

    public Tile getWinningTile() {
        return winningTile;
    }

    public ArrayList<Hand> getWaysToSplit() {
        return waysToSplit;
    }

    public ArrayList<Tile> getDoras() {
        return doras;
    }

    public ArrayList<Tile> getHiddenDoras() {
        return hiddenDoras;
    }

    public int getRedTileCount() {
        return redTileCount;
    }

    public Tile getSeatWind() {
        return seatWind;
    }

    public Tile getPrevailingWind() {
        return prevailingWind;
    }

    public boolean isClosedHand() {
        return closedHand;
    }

    public boolean isSelfDrawn() {
        return selfDrawn;
    }

    public int getRiichi() {
        return riichi;
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public boolean isLastTileFromTheWall() {
        return lastTileFromTheWall;
    }

    public boolean isLastDiscard() {
        return lastDiscard;
    }

    public boolean isDeadWallDraw() {
        return deadWallDraw;
    }

    public boolean isRobbingAQuad() {
        return robbingAQuad;
    }

    public boolean isHeavenlyHand() {
        return heavenlyHand;
    }

    public boolean isHandOfEarth() {
        return handOfEarth;
    }

    public void setSeatWind(Tile seatWind) {
        this.seatWind = seatWind;
    }

    public void setPrevailingWind(Tile prevailingWind) {
        this.prevailingWind = prevailingWind;
    }

    public void setSelfDrawn(boolean selfDrawn) {
        this.selfDrawn = selfDrawn;
    }

    public void setRiichi(int riichi) {
        this.riichi = riichi;
    }

    public void setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
    }

    public void setLastTileFromTheWall(boolean lastTileFromTheWall) {
        this.lastTileFromTheWall = lastTileFromTheWall;
    }

    public void setLastDiscard(boolean lastDiscard) {
        this.lastDiscard = lastDiscard;
    }

    public void setDeadWallDraw(boolean deadWallDraw) {
        this.deadWallDraw = deadWallDraw;
    }

    public void setRobbingAQuad(boolean robbingAQuad) {
        this.robbingAQuad = robbingAQuad;
    }

    public void setHeavenlyHand(boolean heavenlyHand) {
        this.heavenlyHand = heavenlyHand;
    }

    public void setHandOfEarth(boolean handOfEarth) {
        this.handOfEarth = handOfEarth;
    }



    /**
     * 将（里）宝牌指示牌转化为对应的宝牌 <br>
     *（里）宝牌指示牌的下一张牌是（里）宝牌
     * @param doraIndicators （里）宝牌指示牌的数组
     * @return （里）宝牌的列表
     */
    private ArrayList<Tile> getDoras(ArrayList<Tile> doraIndicators) {
        ArrayList<Tile> doras = new ArrayList<Tile>();
        for (Tile t: doraIndicators){
            doras.add(t.getNext());
        }
        return doras;
    }

    /**
     * 尝试拆解手牌并将可能的拆解方式的列表赋值给 {@code waysToSplit}
     */
    public void tryToSplit(){
        waysToSplit = new ArrayList<Hand>();
        ArrayList<Tile> hand = (ArrayList<Tile>) origHand.clone();
        hand.add(winningTile);
        hand.sort(null);
        Hand trail;
        if ((trail = checkThirteenOrphans(hand)) != null){
            waysToSplit.add(trail);
        }
        else {
            waysToSplit = waysToSplitMeldHand(hand);
            if (waysToSplit.size() == 0){
                if ((trail = splitSevenPairs(hand)) != null){
                    waysToSplit.add(trail);
                }
            }
            else {
                if (openMelds.size() > 0) {
                    for (Hand wayToSplit : waysToSplit) {
                        wayToSplit.addAll(openMelds);
                    }
                }
            }
        }
//        print(waysToSplit);
//        System.out.println();

        if (waysToSplit.size() == 0){
            throw new NotWinException();
        }
        for (Hand h: waysToSplit){
            h.setMahjong(this);
        }
    }

    public void getMaxScore(){
        int max = 0;
        Hand maxHand = null;
        for (Hand h: waysToSplit){
            int score = h.getScore();
            if (score > max){
                max = score;
                maxHand = h;
            }
            else if (score == max){
                if (h.getHan() > maxHand.getHan()){
                    maxHand = h;
                }
            }
        }
        maxScoreHand = maxHand;
        if (maxScoreHand.getHan() == 0 && maxScoreHand.getTimes() == 0){
            throw new NoYakuException();
        }
    }

    @Override
    public String toString(){
        return getTiles() + "\n\n" + maxScoreHand;
    }


    /**
     * 统计数组中红宝牌的数量<br>
     * 在地图物品的命名中标号为4的m5, p5, s5为红宝牌
     * @param tiles 读入的地图画物品的命名的数组
     * @return 红宝牌的数量
     */
    private static int CountRedTiles(Collection<Tile> tiles) {
        int count = 0;
        for (Tile tile: tiles) {
            if (tile.isRed()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 将读入的地图画物品的命名转换为表示牌的字符串<br>
     * 如："m2_a1" -> "m2", "p5_b4" -> "p0"
     * @param ts 读入的地图画物品的命名的数组
     * @return
     */
    private static String[] renameTiles(String[] ts){
        int count = 0;
        for (String t: ts){
            if (!t.equals("")){
                count++;
            }
        }
        String[] tiles = new String[count];

        for (int i = 0, j = 0; i < ts.length; i++) {
            String tile = ts[i];
            if (!tile.equals("")) {
                tiles[j] = Tile.renameTile(tile);
                j++;
            }
        }
        return tiles;
    }

    /**
     * 寻找手牌以面子手的形式可能的拆分方式
     * @return 按面子手形式拆分的手牌的列表, 若无法按面子手的形式进行拆分则返回一个空列表
     */
    private static ArrayList<Hand> waysToSplitMeldHand(ArrayList<Tile> origHands) {
        ArrayList<Hand> res = new ArrayList<Hand>();
        ArrayList<Tile> hands = (ArrayList<Tile>) origHands.clone();
        ArrayList<Tile> tiles = (ArrayList<Tile>) origHands.clone();
        removeDupe(tiles);
        Outer:
        for (int i = 0; i < tiles.size(); i++) {
            Hand trial = new Hand(Hand.MELD_HAND);

            Meld pair = removePair(tiles.get(i), hands);
            if (pair != null) {
//                System.out.println(pair + "\n" + hands + "\n----------");
                trial.add(pair);//尝试以每种多于两张的牌作为雀头

                int meldsCount = hands.size() / 3;//计算手上应有的面子数（在副露的情况下手上的面子不一定是4个）
                for (int j = 0; j < meldsCount; j++) {
                    //按从左至右的顺序，如果一张牌多于3张，将其作为刻子取出，否则尝试寻找顺子，若某一张牌刻子顺子都找不到，说明雀头的选择有误，则继续尝试下一个雀头的选择
                    Meld meld = removeTriplet(0, hands);
                    if (meld != null) {
                        trial.add(meld);
                    } else {
                        meld = removeSequence(0, hands);
                        if (meld != null) {
                            trial.add(meld);
                        } else {
//                            System.out.println(meld + "\n" + hands + "\n----------");
                            hands = (ArrayList<Tile>) origHands.clone();
                            continue Outer;
                        }
                    }
//                    System.out.println(meld + "\n" + hands + "\n----------");
                }
//                System.out.println("success" + trial + "\n----------");
                res.add(trial);
                res.addAll(newWaysToSplitForChainedTriplets(trial));
                hands = (ArrayList<Tile>) origHands.clone();
            }
//            System.out.println("==========");
        }
        return res;
    }

    /**
     * 方法 {@code waysToSplit()} 无法在手牌里存在3个计以上的暗刻时找出所有可能的拆分方式 <br>
     * 如 11122233344466, 方法 {@code waysToSplit()} 只能拆分为 111 222 333 444 66，但还可以拆分为 123 123 123 444 66 <br>
     * 在已有的拆分的手牌中，如果拆分出了3个以上的暗刻，检查是否存在3连刻。如果找到3连刻，就把这组3连刻重新组合为对应的3个顺子
     * @param wayToSplit 已拆分的手牌
     * @return 其他的拆分方法的列表
     */
    private static ArrayList<Hand> newWaysToSplitForChainedTriplets(Hand wayToSplit) {
        ArrayList<Hand> newWaysToSplit = new ArrayList<Hand>();
        ArrayList<Tile> tilesOfTriplets = new ArrayList<Tile>();
        for (int i = 0; i < wayToSplit.size(); i++) {
            Meld meld = wayToSplit.get(i);
            if (meld.getType() == Meld.TRIPLET) {
                tilesOfTriplets.add(meld.getTiles()[0]);
            }
        }
        int tripletCount = tilesOfTriplets.size();
        Meld sequence1 = removeSequence(0, (ArrayList<Tile>) tilesOfTriplets.clone());
        Meld sequence2 = removeSequence(1, tilesOfTriplets);
        if (sequence1 != null) {
            Hand newWayToSplit = new Hand(Hand.MELD_HAND);
            newWayToSplit.add(wayToSplit.get(0));
            if (tripletCount == 3) {
                for (int i = 1; i < wayToSplit.getMelds().length; i++) {
                    Meld meld = wayToSplit.get(i);
                    if (meld.getType() != Meld.TRIPLET) {
                        newWayToSplit.add(meld);
                    }

                }
            } else {
                newWayToSplit.add(wayToSplit.get(4));
            }
            newWayToSplit.add(sequence1);
            newWayToSplit.add(sequence1);
            newWayToSplit.add(sequence1);
            newWaysToSplit.add(newWayToSplit);
        }
        if (sequence2 != null) {
            Hand newWayToSplit = new Hand(Hand.MELD_HAND);
            newWayToSplit.add(wayToSplit.get(0));
            newWayToSplit.add(wayToSplit.get(1));
            newWayToSplit.add(sequence2);
            newWayToSplit.add(sequence2);
            newWayToSplit.add(sequence2);

            newWaysToSplit.add(newWayToSplit);
        }
        return newWaysToSplit;
    }

    /**
     * 检查输入的手牌能否拆分为七对子
     * @param origHands 一副手牌
     * @return 拆分为七对子的手牌，如果无法拆分为七对子返回null
     */
    private static Hand splitSevenPairs(ArrayList<Tile> origHands){
        ArrayList<Tile> hand = (ArrayList<Tile>)origHands.clone();
        Hand trial = new Hand(Hand.SEVEN_PAIRS);
        for (int i = 0; i < 7; i++){
            Meld pair = removePair(hand.get(0), hand);
            if (pair != null && (i == 0 || !pair.equals(trial.get(i - 1)))) {
                trial.add(pair);
            }
            else {
                return null;
            }
        }
        return trial;
    }

    /**
     * 检查输入的手牌是否是国士无双
     * @param origHands 一副手牌
     * @return 拆分为国士无双的手牌， 如果不是国士无双返回null
     */
    private static Hand checkThirteenOrphans(ArrayList<Tile> origHands){
        ArrayList<Tile> hand = (ArrayList<Tile>)origHands.clone();
        int countTerminalAndHonor = 0;
        removeDupe(hand);
        for (Tile t: hand){
            if (t.isTerminalOrHonor()){
                countTerminalAndHonor++;
            }
        }
        if (countTerminalAndHonor == 13){
            Meld m = new Meld(origHands.toArray(new Tile[0]));
            m.setType(Meld.THIRTEEN_ORPHANS);
            Hand res = new Hand(Hand.THIRTEEN_ORPHANS);
            res.add(m);
            return res;
        }
        else {
            return null;
        }
    }

    public static void print(ArrayList<Hand> waysToSplit){
        if(waysToSplit.size() == 0){
            System.out.println("This is not a winning hand");
        }
        for (Hand hand: waysToSplit){
            System.out.println(Arrays.toString(hand.getMelds()));
        }
    }

    /**
     * 从输入手牌中寻找由指定的牌构成的雀头
     * @param tile 指定的牌
     * @param hands 一副手牌
     * @return 代表由指定的牌构成的雀头的数组
     * 如果未能找到由该牌构成的雀头，返回null
     */
    public static Meld removePair(Tile tile, ArrayList<Tile> hands) {
        int index = hands.indexOf(tile);
//        System.out.println(index);
        if (index < hands.size() - 1 && hands.get(index + 1).equals(tile)) {
            Tile[] tiles = {tile, tile};
            Meld res = new Meld(tiles);
            res.setType(Meld.PAIR);
            hands.remove(index + 1);
            hands.remove(index);
            return res;
        } else {
            return null;
        }
    }

    /**
     * 从输入手牌中寻找由在指定index上的牌构成的暗刻
     * @param index 指定的牌的index
     * @param hands 一副手牌
     * @return 代表由指定的牌构成的暗刻的数组
     * 如果未能找到由该牌构成的暗刻，返回null
     */
    public static Meld removeTriplet(int index, ArrayList<Tile> hands) {
        if (index < hands.size() - 2) {
            Tile tile = hands.get(index);
            if (index < hands.size() - 2 && hands.get(index + 1).equals(tile) && hands.get(index + 2).equals(tile)) {
                Tile[] tiles = {tile, tile, tile};
                Meld res = new Meld(tiles);
                res.setType(Meld.TRIPLET);
                hands.remove(index + 2);
                hands.remove(index + 1);
                hands.remove(index);
                return res;
            }
        }
        return null;
    }

    /**
     * 从输入手牌中寻找由在指定index上的牌构成的顺子
     * @param index 指定的牌的index
     * @param hands 一副手牌
     * @return 代表由指定的牌构成的顺子的数组
     * 如果未能找到由该牌构成的顺子，返回null
     */
    public static Meld removeSequence(int index, ArrayList<Tile> hands) {
        if (index < hands.size() - 2) {
            Tile tile = hands.get(index);
            Tile tile2;
            for (int i = index + 1; i < hands.size(); i++) {
                tile2 = hands.get(i);
                if (i < hands.size() - 1 && tile.isContinue(tile2)) {
                    for (int j = index + 2; j < hands.size(); j++) {
                        Tile tile3 = hands.get(j);
                        if (tile2.isContinue(tile3)) {
                            Tile[] tiles = {tile, tile2, tile3};
                            Meld res = new Meld(tiles);
                            res.setType(Meld.SEQUENCE);
                            hands.remove(j);
                            hands.remove(i);
                            hands.remove(index);
                            return res;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 删除输入手牌中重复的牌（等效于统计手牌中出现过的牌）
     * @param hands 一副手牌
     */
    public static void removeDupe(ArrayList<Tile> hands) {
        for (int i = hands.size() - 1; i > 0; i--) {
            if (hands.get(i - 1).equals(hands.get(i))) {
                hands.remove(i);
            }
        }
    }


    public static void main(String[] args){
//        String[] hand = {m1, m1, m1, m1, m2, m2, m2, m2, m3, m3, m3, m3, m5};
//        String winningTile = m5;
//        String[][] openMelds = {};
//        boolean[] meldsFromSelf = {};
//        String[] doraIndicators = {"", "", "", "", ""};
//        String[] hiddenDoraIndicators = {"", "", "", "", ""};
//        Mahjong m = new Mahjong(hand, winningTile, openMelds, meldsFromSelf, doraIndicators, hiddenDoraIndicators);
//        m.setPrevailingWind(new Tile(z1));
//        m.setSeatWind(new Tile(z1));
//        m.setSelfDrawn(false);
//        m.setRiichi(0);
//        m.tryToSplit();
//        m.getMaxScore();
//        System.out.println(m);
        System.out.println((Integer.toHexString((int)'自')) + " " + Integer.toHexString((int)'摸'));
        System.out.println("\u81ea\u6478");
    }
}