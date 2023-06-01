package org.example;

import java.util.List;

public class Utils {
    private static final int SECOND = 1000;
    private static int playerID = 1;
    private static int goalID = 1;
    private static int matchID = 1;
    public static final List<String> FIRST_NAMES = List.of("gil","or", "david", "ben", "dov", "dan", "ron", "pelg", "ram", "rom", "don" , "ori" , "omer", "yuda", "dvir,","daniel","yogev","lior","ari","yosef","amir","oleg","eden","alex","matan","avi","ravid","ofek","sun","dror");
    public static final List<String> LAST_NAMES = List.of("hatar","hamar","hadad","swisa","benzakai","edri","levi","buzaglo","dadon","hatav","goren","bashtaker","bechor","biran","alon","mizrahi","arel","gilboha","avidan","nezer","kadosh","malihac","ovad","cohen","bentov","haim","zion","dagan","golan","vaizman");

    public static void sleep(int sec){
            try {
                Thread.sleep(sec * SECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }
    public static int getNewPlayerId(){
        return playerID++;
    }

    public static int getNewGoalId(){
        return goalID++;
    }

    public static int getNewMatchId(){
        return matchID++;
    }
}
