package org.bsrserver.mahjongtools.algorithm;

import org.bsrserver.mahjongtools.exception.IllegalHandException;

import java.util.*;

import static org.bsrserver.mahjongtools.algorithm.Tile.*;

public class Hand {

    Mahjong m;

    private int type;
    private Meld[] melds;
    private int size;

    private Tile winningTile;
    private ArrayList<int[]> winningTilePoses;
    private Tile seatWind;
    private Tile prevailingWind;
    private int riichi;
    private boolean selfDrawn;
    private boolean closedHand;
    private boolean noPointHand;



    ArrayList<Yaku> yakus;
    ArrayList<Yakuman> yakumans;
    private int doraCount;
    private int hiddenDoraCount;
    private int redTileCount;
    private int times;
    private int han;
    private int fu;
    private int score;
    private String scoreName;
    private int[] othersNeedToPay;

    private String[][] outputLines;


    public static final int MELD_HAND = 5;
    public static final int SEVEN_PAIRS = 7;
    public static final int THIRTEEN_ORPHANS = 1;

    public Hand(int type){
        melds = new Meld[type];
        this.type = type;
        size = 0;
    }

    public int getType(){
        return type;
    }

    public Meld[] getMelds() {
        return melds;
    }

    public int getHan(){
        return han;
    }

    public int getTimes() {
        return times;
    }

    public int getScore(){
        return score;
    }

    public void setMahjong(Mahjong m){
        this.m = m;
        winningTile = m.getWinningTile();
        seatWind = m.getSeatWind();
        prevailingWind = m.getPrevailingWind();
        riichi = m.getRiichi();
        selfDrawn = m.isSelfDrawn();
        closedHand = m.isClosedHand();
        redTileCount = m.getRedTileCount();
        calcScore();
    }

    public int size(){
        return size;
    }

    public void add(Meld m){
        if (size < melds.length) {
            melds[size] = m;
            size++;
        }
    }

    public void addAll(ArrayList<Meld> ms){
        for (Meld m: ms){
            add(m);
        }
    }

    public Meld get(int i){
        if (i >= 0 && i < size){
            return melds[i];
        }
        else {
            return null;
        }
    }

    public void checkWinningTilePos(){
        winningTilePoses = new ArrayList<int[]>();
        for (int i = 0; i < melds.length; i++){
            Meld m = melds[i];
            int index = m.indexOf(winningTile);
            if (index >= 0){
                int[] pos = {m.getType(), index};
                winningTilePoses.add(pos);
                if (m.getType() == Meld.TRIPLET){
                    if (!selfDrawn){
                        m.setType(Meld.OPEN_TRIPLET);
                    }
                }
            }
        }
    }

    public void checkYakuman(){
        yakumans = new ArrayList<Yakuman>();
        heavenlyHand();
        handOfEarth();
        thirteenOrphans();
        fourClosedTriplets();
        bigThreeDragons();
        fourWinds();
        allHonors();
        allTerminals();
        allGreen();
        nineGates();
        fourQuads();
    }

    public void checkYaku(){
        yakus = new ArrayList<Yaku>();
        riichi();
        oneShot();
        closedHandSelfDrawn();
        lastDiscard();
        lastTileFromTheWall();
        deadWallDraw();
        robbingAQuad();
        sevenPairs();
        noPointHand();
        identicalSequences();
        threeColorSequences();
        straight();
        allTriplets();
        threeClosedTriplets();
        threeColorTriplets();
        threeQuads();
        allSimples();
        honors();
        terminalOrHonorInEachMeld();
//        allTerminalsAndHonor();
        littleThreeDragons();
        flush();
        doras();
        redTile();
        if (riichi > 0){
            hiddenDoras();
        }
    }

    public void calcHan(){
        checkWinningTilePos();
        if(type == MELD_HAND){
            Arrays.sort(melds);
//            System.out.println(Arrays.toString(melds));
        }
//        System.out.println(Arrays.toString(melds));
        checkYakuman();
        if (times > 0){
            return;
        }
        checkYaku();
    }

    public void calcFu(){
        if (type == SEVEN_PAIRS){
            fu = 25;
        }
        else if (type == MELD_HAND){
            fu = 20;

            for (Meld m : melds) {
                switch (m.getType()) {
                    case Meld.PAIR:
                        if (m.getTiles()[0].isDragon()) {
                            fu += 2;
                        } else {
                            if (m.getTiles()[0].equals(prevailingWind)) {
                                fu += 2;
                            }
                            if (m.getTiles()[0].equals(seatWind)) {
                                fu += 2;
                            }
                        }
                        break;
                    case Meld.OPEN_TRIPLET:
                        if (m.getTiles()[0].isTerminalOrHonor()) {
                            fu += 4;
                        } else {
                            fu += 2;
                        }
                        break;
                    case Meld.TRIPLET:
                        if (m.getTiles()[0].isTerminalOrHonor()) {
                            fu += 8;
                        } else {
                            fu += 4;
                        }
                        break;
                    case Meld.OPEN_QUAD:
                        if (m.getTiles()[0].isTerminalOrHonor()) {
                            fu += 16;
                        } else {
                            fu += 8;
                        }
                        break;
                    case Meld.CLOSED_QUAD:
                        if (m.getTiles()[0].isTerminalOrHonor()) {
                            fu += 32;
                        } else {
                            fu += 16;
                        }
                        break;
                }
            }

            for (int[] pos : winningTilePoses) {
                if (pos[0] == Meld.PAIR) {
                    fu += 2;
                } else if (pos[0] % 10 == Meld.SEQUENCE) {
                    if (pos[1] == 1) {
                        fu += 2;
                    } else if (pos[1] == 0 && winningTile.getValue() == 7 || pos[1] == 2 && winningTile.getValue() == 3) {
                        fu += 2;
                    }
                }
            }

            if (fu == 20){
                if (!selfDrawn){
                    fu += 2;
                }
            }
            else {
                if (closedHand && !selfDrawn) {
                    fu += 10;
                } else if (selfDrawn) {
                    fu += 2;
                }
            }
            fu = (fu + 9) / 10 * 10;
        }
    }

    public void calcScore(){
        calcHan();
        calcFu();
        othersNeedToPay = new int[3];
        if (times > 0){
            score = 32000 * times;
        }
        else if (han >= 5){
            switch (han){
                case 5:
                    score = 8000;
                    scoreName = "满贯";
                    break;
                case 6:
                case 7:
                    score = 12000;
                    scoreName = "跳满";
                    break;
                case 8:
                case 9:
                case 10:
                    score = 18000;
                    scoreName = "倍满";
                    break;
                case 11:
                case 12:
                    score = 24000;
                    scoreName = "三倍满";
                    break;
                default:
                    score = 32000;
                    scoreName = "累计役满";

            }
        }
        else {
            score = 4 * fu * (int)(Math.pow(2, han + 2));
            if (score >= 8000){
                score = 8000;
                scoreName = "满贯";
            }
            else {
                scoreName = "";
            }
        }
        if (seatWind.equals(new Tile(z1))){
            score = score / 2 * 3;
        }
        if (score < 12000){
            if (selfDrawn){
                if (seatWind.equals(new Tile(z1))){
                    othersNeedToPay[0] = (int)Math.ceil(score / 300.0) * 100;
                    othersNeedToPay[1] = othersNeedToPay[0];
                    othersNeedToPay[2] = othersNeedToPay[1];
                }
                else {
                    othersNeedToPay[0] = (int) Math.ceil(score / 200.0) * 100;
                    othersNeedToPay[1] = (int) Math.ceil(score / 400.0) * 100;
                    othersNeedToPay[2] = othersNeedToPay[1];
                }
                score = othersNeedToPay[0] + othersNeedToPay[1] + othersNeedToPay[2];
            }
            else {
                score = (int)Math.ceil(score / 100.0) * 100;
                othersNeedToPay[0] = score;
            }
        }
        else {
            if (selfDrawn){
                if (seatWind.equals(new Tile(z1))){
                    othersNeedToPay[0] = score / 3;
                    othersNeedToPay[1] = othersNeedToPay[0];
                    othersNeedToPay[2] = othersNeedToPay[1];
                }
                else {
                    othersNeedToPay[0] = score / 2;
                    othersNeedToPay[1] = score / 4;
                    othersNeedToPay[2] = othersNeedToPay[1];
                }
            }
            else {
                othersNeedToPay[0] = score;
            }
        }
    }



    public void heavenlyHand(){
        if (m.isHeavenlyHand()){
            if (!seatWind.equals(new Tile(z1)) || m.getOpenMelds().size() != 0){
                System.out.println("wrong heavenly hand");
                throw new IllegalHandException();
            }
            yakumans.add(Yakuman.HEAVENLY_HAND);
            times += 1;
        }
    }

    public  void handOfEarth(){
        if (m.isHandOfEarth()){
            if (seatWind.equals(new Tile(z1)) || m.getOpenMelds().size() != 0){
                System.out.println("wrong earth hand");
                throw new IllegalHandException();
            }
            yakumans.add(Yakuman.HAND_OF_EARTH);
            times += 1;
        }
    }

    public void thirteenOrphans(){
        if (type == THIRTEEN_ORPHANS){
            int count = 0;
            for (Tile t: melds[0].getTiles()){
                if (t.equals(winningTile)){
                    count++;
                }
            }
            if (count == 2){
                yakumans.add(Yakuman.THIRTEEN_ORPHANS_13_WAITS);
                times += 2;
            }
            else {
                yakumans.add(Yakuman.THIRTEEN_ORPHANS);
                times += 1;
            }
        }
    }

    public void fourClosedTriplets(){
        if (type == MELD_HAND && closedHand) {
            int countClosedTriplets = 0;
            for (Meld m : melds) {
                if (m.getType() == Meld.TRIPLET || m.getType() == Meld.CLOSED_QUAD) {
                    countClosedTriplets++;
                }
            }
            if (countClosedTriplets == 4) {
                for (int[] pos : winningTilePoses) {
                    if (pos[0] == Meld.PAIR) {
                        yakumans.add(Yakuman.FOUR_CLOSED_TRIPLETS_SINGLE_WAIT);
                        times += 2;
                        return;
                    }
                }
                yakumans.add(Yakuman.FOUR_CLOSED_TRIPLETS);
                times += 1;
            }
        }
    }

    public void bigThreeDragons(){
        if (type == MELD_HAND) {
            int countDragonTriplets = 0;
            for (Meld m : melds) {
                if ((m.isTriplet() || m.isQuad()) && m.getTiles()[0].isDragon()) {
                    countDragonTriplets++;
                }
            }
            if (countDragonTriplets == 3) {
                yakumans.add(Yakuman.BIG_THREE_DRAGONS);
                times += 1;
            }
        }
    }

    public void fourWinds(){
        if (type == MELD_HAND){
            boolean windPair = false;
            int countWindTriplets = 0;
            for (Meld m: melds){
                if (m.getType() == Meld.PAIR && m.getTiles()[0].isWind()){
                    windPair = true;
                }
                else if ((m.isTriplet() || m.isQuad()) && m.getTiles()[0].isWind()){
                    countWindTriplets++;
                }
            }
            if (countWindTriplets == 4){
                yakumans.add(Yakuman.BIG_FOUR_WINDS);
                times += 2;
            }
            else if (countWindTriplets == 3 && windPair){
                yakumans.add(Yakuman.LITTLE_FOUR_WINDS);
                times += 1;
            }
        }
    }

    public void allHonors(){
        for (Meld m: melds){
            if (m.getSuit() != 'z'){
                return;
            }
        }
        yakumans.add(Yakuman.ALL_HONORS);
        times += 1;
    }

    public void allTerminals(){
        if (type == MELD_HAND){
            for (Meld m: melds){
                if (!((m.isTriplet() || m.isQuad() || m.getType() == Meld.PAIR) && m.getTiles()[0].isTerminal())){
                    return;
                }
            }
            yakumans.add(Yakuman.ALL_TERMINALS);
            times += 1;
        }
    }

    public void allGreen(){
        for (Meld m: melds){
            for (Tile t: m.getTiles()){
                if(!t.isGreen()){
                    return;
                }
            }
        }
        yakumans.add(Yakuman.ALL_GREEN);
        times += 1;
    }

    public void nineGates(){
        if (type == MELD_HAND && closedHand) {
            char suit = melds[0].getSuit();
            if (suit != 'z') {
                int[] tileCount = new int[9];
                for (Meld m : melds) {
                    if (m.getSuit() != suit) {
                        return;
                    }
                    for (Tile t : m.getTiles()) {
                        tileCount[t.getValue() - 1]++;
                    }
                }
                for (int key = 1; key <= 9; key++) {
                    if (key == 1 || key == 9) {
                        if (tileCount[key - 1] < 3) {
                            return;
                        }
                    } else {
                        if (tileCount[key - 1] < 1) {
                            return;
                        }
                    }
                }
                if (tileCount[winningTile.getValue() - 1] % 2 == 0) {
                    yakumans.add(Yakuman.NINE_GATES_9_WAITS);
                    times += 2;
                } else {
                    yakumans.add(Yakuman.NINE_GATES);
                    times += 1;
                }
            }
        }
    }

    public void fourQuads(){
        if (type == MELD_HAND){
            int quadCount = 0;
            for (Meld m: melds){
                if (m.isQuad()){
                    quadCount++;
                }
            }
            if (quadCount == 4){
                yakumans.add(Yakuman.FOUR_QUADS);
                times += 1;
            }
        }
    }


    public void riichi(){
        if (riichi > 0 && !closedHand){
            System.out.println("wrong riichi");
            throw new IllegalHandException();
        }
        if (riichi == 1){
            yakus.add(Yaku.RIICHI);
            han += 1;
        }
        else if (riichi == 2){
            yakus.add(Yaku.DOUBLE_RIICHI);
            han += 2;
        }
    }

    public void oneShot(){
        if (m.isOneShot()){
            yakus.add(Yaku.ONE_SHOT);
            han += 1;
        }
    }

    public void lastTileFromTheWall(){
        if (m.isLastTileFromTheWall()){
            yakus.add(Yaku.LAST_TILE_FROM_THE_WALL);
            han += 1;
        }
    }

    public void lastDiscard(){
        if (m.isLastDiscard()){
            yakus.add(Yaku.LAST_DISCARD);
            han += 1;
        }
    }

    public void deadWallDraw(){
        if (m.isDeadWallDraw()){
            yakus.add(Yaku.DEAD_WALL_DRAW);
            han += 1;
        }
    }

    public void robbingAQuad(){
        if (m.isRobbingAQuad()){
            yakus.add(Yaku.ROBBING_A_QUAD);
            han += 1;
        }
    }

    public void sevenPairs(){
        if (type == SEVEN_PAIRS){
            yakus.add(Yaku.SEVEN_PAIRS);
            han += 2;
        }
    }

    public void closedHandSelfDrawn(){
        if (closedHand && selfDrawn){
            yakus.add(Yaku.CLOSED_HAND_SELF_DRAWN);
            han += 1;
        }
    }

    public void noPointHand(){
        if (type == MELD_HAND && closedHand){
            for (Meld m: melds){
                if (m.getType() == Meld.TRIPLET){
                    return;
                }
                else if(m.getType() == Meld.PAIR){
                    Tile t = m.getTiles()[0];
                    if (t.isDragon() || t.equals(seatWind) || t.equals(prevailingWind)){
                        return;
                    }
                }
            }
            for(int i = 0; i < winningTilePoses.size(); i++){
                int[] pos = winningTilePoses.get(i);
                if (pos[0] == Meld.SEQUENCE && pos[1] != 1){
                    for (int j = winningTilePoses.size() - 1; j >= 0; j--){
                        if (j != i){
                            winningTilePoses.remove(j);
                        }
                    }
                    noPointHand = true;
                    yakus.add(Yaku.NO_POINT_HAND);
                    han += 1;
                    return;
                }
            }
        }
    }

    public void identicalSequences(){
        if (type == MELD_HAND && closedHand){
            int count = 0;
            for (int i = 0; i < melds.length - 2; i++){
                if (melds[i].getType() == Meld.SEQUENCE && melds[i].equals(melds[i + 1])){
                    count++;
                    i++;
                }
            }
            if (count == 1){
                yakus.add(Yaku.ONE_SET_OF_IDENTICAL_SEQUENCES);
                han += 1;
            }
            else if(count == 2){
                yakus.add(Yaku.TWO_SETS_OF_IDENTICAL_SEQUENCES);
                han += 3;
            }
        }
    }

    public void threeColorSequences(){
        if (type == MELD_HAND){
            for (int i = 0; i < melds.length - 2; i++){
                Meld m1 = melds[i];
                if (m1.isSequence()){
                    for (int j = 0; j < melds.length - 1; j++){
                        Meld m2 = melds[j];
                        if (m2.isSequence() && m2.getSuit() != m1.getSuit() && m2.hasEqualValues(m1)){
                            for (int k = 0; k < melds.length; k++){
                                Meld m3 = melds[k];
                                if (m3.isSequence() &&
                                        m3.getSuit() != m1.getSuit() &&
                                        m3.getSuit() != m2.getSuit() &&
                                        m3.hasEqualValues(m2)){
                                    if (closedHand){
                                        yakus.add(Yaku.THREE_COLOR_SEQUENCES);
                                        han += 2;
                                    }
                                    else {
                                        yakus.add(Yaku.THREE_COLOR_SEQUENCES_);
                                        han += 1;
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void straight(){
        if (type == MELD_HAND){
            for (int i = 0; i < melds.length - 2; i++){
                Meld m1 = melds[i];
                if (m1.isSequence() && m1.getTiles()[0].getValue() == 1){
                    for (int j = i + 1; j < melds.length - 1; j++){
                        Meld m2 = melds[j];
                        if (m2.isSequence() && m2.getSuit() == m1.getSuit() && m2.getTiles()[0].getValue() == 4){
                            for (int k = j + 1; k < melds.length; k++){
                                Meld m3 = melds[k];
                                if (m3.isSequence() && m3.getSuit() == m2.getSuit() && m3.getTiles()[0].getValue() == 7){
                                    if (closedHand){
                                        yakus.add(Yaku.STRAIGHT);
                                        han += 2;
                                    }
                                    else {
                                        yakus.add(Yaku.STRAIGHT_);
                                        han += 1;
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void allTriplets(){
        if (type == MELD_HAND){
            for (Meld m: melds){
                if (m.isSequence()){
                    return;
                }
            }
            yakus.add(Yaku.ALL_TRIPLETS);
            han += 2;
        }
    }

    public void threeClosedTriplets(){
        if (type == MELD_HAND) {
            int countClosedTriplets = 0;
            for (Meld m : melds) {
                if (m.getType() == Meld.TRIPLET || m.getType() == Meld.CLOSED_QUAD) {
                    countClosedTriplets++;
                }
            }
            if (countClosedTriplets == 3) {
                yakus.add(Yaku.THREE_CLOSED_TRIPLETS);
                han += 2;
            }
        }
    }

    public void threeColorTriplets(){
        if (type == MELD_HAND){
            for (int i = 0; i < melds.length - 2; i++){
                Meld m1 = melds[i];
                if (m1.isTriplet() || m1.isQuad()){
                    for (int j = 0; j < melds.length - 1; j++){
                        Meld m2 = melds[j];
                        if ((m2.isTriplet() || m2.isQuad()) && m2.getSuit() != m1.getSuit() && m2.hasEqualValues(m1)){
                            for (int k = 0; k < melds.length; k++){
                                Meld m3 = melds[k];
                                if ((m3.isTriplet() || m3.isQuad()) &&
                                        m3.getSuit() != m1.getSuit() &&
                                        m3.getSuit() != m2.getSuit() &&
                                        m3.hasEqualValues(m2)){
                                    yakus.add(Yaku.THREE_COLOR_TRIPLETS);
                                    han += 2;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void threeQuads(){
        if (type == MELD_HAND){
            int quadCount = 0;
            for (Meld m: melds){
                if (m.isQuad()){
                    quadCount++;
                }
            }
            if (quadCount == 3){
                yakus.add(Yaku.THREE_QUADS);
                han += 2;
            }
        }
    }

    public void allSimples(){
        for (Meld m: melds){
            for (Tile t: m.getTiles())
                if (t.isTerminalOrHonor()){
                    return;
                }
            }
        yakus.add(Yaku.ALL_SIMPLES);
        han += 1;
    }

    public void honors(){
        if (type == MELD_HAND){
            for (Meld m: melds){
                if (m.isTriplet() || m.isQuad()){
                    Tile t = m.getTiles()[0];
                    if (t.isDragon()) {
                        switch (t.getValue()){
                            case 5:
                                yakus.add(Yaku.HONOR_WHITE);
                                break;
                            case 6:
                                yakus.add(Yaku.HONOR_GREEN);
                                break;
                            case 7:
                                yakus.add(Yaku.HONOR_RED);
                                break;
                        }
                        han += 1;
                    }
                    else {
                        if (t.equals(prevailingWind)) {
                            yakus.add(Yaku.PREVAILING_WIND);
                            han += 1;
                        }
                        if (t.equals(seatWind)) {
                            yakus.add(Yaku.SEAT_WIND);
                            han += 1;
                        }
                    }
                }
            }
        }
    }

    public void terminalOrHonorInEachMeld(){
        if (type == MELD_HAND){
            boolean terminal = true, allTerminalsAndHonors = true;
            for (Meld m: melds){
                if (m.isSequence()){
                    allTerminalsAndHonors = false;
                    if (!(m.getTiles()[0].isTerminal() || m.getTiles()[2].isTerminal())){
                        return;
                    }
                }
                else {
                    if (!m.getTiles()[0].isTerminal()){
                        terminal = false;
                        if (m.getSuit() != 'z'){
                            return;
                        }
                    }
                }
            }
            if (terminal) {
                if (closedHand) {
                    yakus.add(Yaku.TERMINAL_IN_EACH_MELD);
                    han += 3;
                }
                else {
                    yakus.add(Yaku.TERMINAL_IN_EACH_MELD_);
                    han += 2;
                }
            }
            else if (allTerminalsAndHonors){
                yakus.add(Yaku.ALL_TERMINALS_AND_HONORS);
                han += 2;
            }
            else {
                if (closedHand) {
                    yakus.add(Yaku.TERMINAL_OR_HONOR_IN_EACH_MELD);
                    han += 2;
                }
                else {
                    yakus.add(Yaku.TERMINAL_OR_HONOR_IN_EACH_MELD_);
                    han += 1;
                }
            }
        }
    }

    public void littleThreeDragons(){
        if (type == MELD_HAND) {
            int countDragonTriplets = 0;
            boolean dragonPair = false;
            for (Meld m : melds) {
                if ((m.isTriplet() || m.isQuad()) && m.getTiles()[0].isDragon()) {
                    countDragonTriplets++;
                }
                if (m.getType() == Meld.PAIR && m.getTiles()[0].isDragon()){
                    dragonPair = true;
                }
            }
            if (countDragonTriplets == 2 && dragonPair) {
                yakus.add(Yaku.LITTLE_THREE_DRAGONS);
                han += 2;
            }
        }
    }

    public void flush(){
        boolean flush = true;
        char suit = 0;
        for (Meld m: melds){
            if (m.getSuit() == 'z'){
                flush = false;
            }
            else {
                if (suit == 0){
                    suit = m.getSuit();
                }
                else {
                    if (m.getSuit() != suit){
                        return;
                    }
                }
            }
        }
        if (closedHand) {
            if (flush) {
                yakus.add(Yaku.FLUSH);
                han += 6;
            } else {
                yakus.add(Yaku.HALF_FLUSH);
                han += 3;
            }
        }
        else {
            if (flush) {
                yakus.add(Yaku.FLUSH_);
                han += 5;
            } else {
                yakus.add(Yaku.HALF_FLUSH_);
                han += 2;
            }
        }
    }

    public void doras(){
        for (Tile dora: m.getDoras()){
            for (Meld m: melds){
//                System.out.println(m + " " + dora + " " + m.indexOf(dora));
                if (m.indexOf(dora) >= 0){
                    if(m.isSequence()){
                        doraCount += 1;
                    }
                    else {
                        doraCount += m.getTiles().length;
                    }
                }
            }
        }
        if (doraCount > 0) {
            yakus.add(Yaku.DORA);
            Yaku.DORA.setHan(doraCount);
            han += doraCount;
        }
    }

    public void redTile(){
        if (redTileCount > 0){
            yakus.add(Yaku.RED_TILE);
            Yaku.RED_TILE.setHan(redTileCount);
            han += redTileCount;
        }
    }

    public void hiddenDoras(){
        for (Tile hiddenDora: m.getHiddenDoras()){
            for (Meld m: melds){
                if (m.indexOf(hiddenDora) >= 0){
                    if (m.isSequence()){
                        hiddenDoraCount += 1;
                    }
                    else {
                        hiddenDoraCount += m.getTiles().length;
                    }
                }
            }
        }
        yakus.add(Yaku.HIDDEN_DORA);
        Yaku.HIDDEN_DORA.setHan(hiddenDoraCount);
        han += hiddenDoraCount;
    }

    public void generateOutput(){
        int lineLimit;
        if (times > 0){
            outputLines = new String[7][3];
            for (int i = 0; i < yakumans.size(); i++){
                outputLines[i % 4][i / 4 + 1] = yakumans.get(i).toString();
            }
        }
        else {

        }
    }

    @Override
    public String toString(){
        String out = "";
        if (times > 0) {
            out = yakumans.toString().replaceAll("\\[|]|(,[ ])", "") + "\n" + (times > 1? times + "倍役满": "役满");
        }
        else {
            out =  yakus.toString().replaceAll("\\[|]|(,[ ])", "") + "\n" + han + "番" + fu + "符" + " " + scoreName;
        }
        out += "\n" + score + "点\n" + "应支付：" + Arrays.toString(othersNeedToPay).replaceAll("\\[|]|,", "");
        return out;
    }

}
