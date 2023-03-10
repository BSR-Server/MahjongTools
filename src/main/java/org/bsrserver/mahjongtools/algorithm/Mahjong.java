package org.bsrserver.mahjongtools.algorithm;

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


    private ArrayList<Hand> waysToSplit;
    private Hand maxScoreHand;

    public Mahjong(String[] hand, String winningTile, String[][] openMelds, boolean[] meldsFromSelf, String[] doraIndicators, String[] hiddenDoraIndicators) {
        this.winningTile = new Tile(Tile.renameTile(winningTile));
        Arrays.sort(hand);
        origHand = new ArrayList<Tile>();
        redTileCount += CountRedTiles(hand);
        hand = renameTiles(hand);
        for (String t: hand){
            origHand.add(new Tile(t));
        }
        this.openMelds = new ArrayList<Meld>();
        closedHand = true;
        for (int i = 0; i < openMelds.length; i++) {
            String[] m = openMelds[i];
            redTileCount += CountRedTiles(m);
            m = renameTiles(m);
            if (m.length > 0) {
                Meld meld = new Meld(m);
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
                if (type > 20 && closedHand) {
                    closedHand = false;
                }
                meld.setType(type);
                this.openMelds.add(meld);
            }
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
            return origHand + " " + openMelds + "  " + winningTile + " " + (selfDrawn? "??????": "???") + "\n" +
                    Arrays.toString(doraIndicators) + "                " + Arrays.toString(hiddenDoraIndicators);
        }
        else {
            return origHand + " " + winningTile + " " + (selfDrawn? "??????": "???")+ "\n" +
            Arrays.toString(doraIndicators) + "                " + Arrays.toString(hiddenDoraIndicators);
        }
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



    /**
     * ??????????????????????????????????????????????????? <br>
     *?????????????????????????????????????????????????????????
     * @param doraIndicators ?????????????????????????????????
     * @return ????????????????????????
     */
    private ArrayList<Tile> getDoras(ArrayList<Tile> doraIndicators) {
        ArrayList<Tile> doras = new ArrayList<Tile>();
        for (Tile t: doraIndicators){
            doras.add(t.getNext());
        }
        return doras;
    }

    /**
     * ??????????????????????????????????????????????????????????????? {@code waysToSplit}
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
        print(waysToSplit);
        System.out.println();
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
        }
        maxScoreHand = maxHand;
    }

//    public int[] othersNeedToPay(){
//        if (selfDrawn){
//            if (seatWind.equals(new Tile(z1))){
//
//            }
//        }
//    }

    @Override
    public String toString(){
//        if (maxScoreHand != null) {
            return getTiles() + "\n\n" + maxScoreHand;
//        }
//        else {
//            return "?????????????????????";
//        }
    }


    /**
     * ?????????????????????????????????<br>
     * ????????????????????????????????????4???m5, p5, s5????????????
     * @param tiles ??????????????????????????????????????????
     * @return ??????????????????
     */
    private static int CountRedTiles(String[] tiles) {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            String tile = tiles[i];
            if (tile.matches("[mps]5_.4")) {
                count++;
            }
        }
        return count;
    }

    /**
     * ??????????????????????????????????????????????????????????????????<br>
     * ??????"m2_a1" -> "m2", "p5_b4" -> "p0"
     * @param ts ??????????????????????????????????????????
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
     * ??????????????????????????????????????????????????????
     * @return ??????????????????????????????????????????, ??????????????????????????????????????????????????????????????????
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
                trial.add(pair);//?????????????????????????????????????????????

                int meldsCount = hands.size() / 3;//?????????????????????????????????????????????????????????????????????????????????4??????
                for (int j = 0; j < meldsCount; j++) {
                    //????????????????????????????????????????????????3???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     * ?????? {@code waysToSplit()} ????????????????????????3????????????????????????????????????????????????????????? <br>
     * ??? 11122233344466, ?????? {@code waysToSplit()} ??????????????? 111 222 333 444 66???????????????????????? 123 123 123 444 66 <br>
     * ???????????????????????????????????????????????????3???????????????????????????????????????3?????????????????????3?????????????????????3??????????????????????????????3?????????
     * @param wayToSplit ??????????????????
     * @return ??????????????????????????????
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
     * ?????????????????????????????????????????????
     * @param origHands ????????????
     * @return ??????????????????????????????????????????????????????????????????null
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
     * ??????????????????????????????????????????
     * @param origHands ????????????
     * @return ????????????????????????????????? ??????????????????????????????null
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
     * ??????????????????????????????????????????????????????
     * @param tile ????????????
     * @param hands ????????????
     * @return ?????????????????????????????????????????????
     * ???????????????????????????????????????????????????null
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
     * ????????????????????????????????????index????????????????????????
     * @param index ???????????????index
     * @param hands ????????????
     * @return ?????????????????????????????????????????????
     * ???????????????????????????????????????????????????null
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
     * ????????????????????????????????????index????????????????????????
     * @param index ???????????????index
     * @param hands ????????????
     * @return ?????????????????????????????????????????????
     * ???????????????????????????????????????????????????null
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
     * ??????????????????????????????????????????????????????????????????????????????
     * @param hands ????????????
     */
    public static void removeDupe(ArrayList<Tile> hands) {
        for (int i = hands.size() - 1; i > 0; i--) {
            if (hands.get(i - 1).equals(hands.get(i))) {
                hands.remove(i);
            }
        }
    }


    public static void main(String[] args){
        String[] hand = {"z1_b1","z1_b2","z1_b3","z2_b1","z2_b2","z2_b3","z5_b2","","","","","",""};
        String winningTile = "z5_b1";
        String[][] openMelds = {{"z3_b1", "z3_b2", "z3_b3", "z3_b4"}, {"z4_b4", "z4_b1", "z4_b2", "z4_b4"}, {"", "", "", ""}, {"", "", "", ""}};
        boolean[] meldsFromSelf = {true, true};
        String[] doraIndicators = {"", "", "", "", ""};
        String[] hiddenDoraIndicators = {"", "", "", "", ""};
        Mahjong m = new Mahjong(hand, winningTile, openMelds, meldsFromSelf, doraIndicators, hiddenDoraIndicators);
        m.setPrevailingWind(new Tile(z1));
        m.setSeatWind(new Tile(z1));
        m.setSelfDrawn(false);
        m.setRiichi(0);
        m.tryToSplit();
        m.getMaxScore();
        System.out.println(m);
    }
}