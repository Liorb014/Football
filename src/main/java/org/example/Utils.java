package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static final int SECOND = 1000;
    private static int playerID = 1;
    private static int goalID = 1;
    private static int matchID = 1;
    public static final List<String> FIRST_NAMES = List.of("gil","or", "david", "ben", "dov", "dan", "ron", "pelg", "ram", "rom", "don" , "ori" , "omer", "yuda", "dvir,","daniel","yogev","lior","ari","yosef","amir","oleg","eden","alex","matan","avi","ravid","ofek","sun","dror");
    public static final List<String> LAST_NAMES = List.of("hatar","hamar","hadad","swisa","benzakai","edri","levi","buzaglo","dadon","hatav","goren","bashtaker","bechor","biran","alon","mizrahi","arel","gilboha","avidan","nezer","kadosh","malihac","ovad","cohen","bentov","haim","zion","dagan","golan","vaizman");
    public static final int ROUND_START = 1;
    public static final int ROUND_END = 9;
    public static final int TIME_TO_COUNTDOWN = 10;
    public static final int END_OF_TIME = 0;
    public static final int TIME_TO_COUNT = 1;
    public static void sleep(int sec){
            try {
                Thread.sleep(sec * SECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }
    private static final String PATH_TO_DATA_FILE = "src/main/java/org/example/Assets/Teams.csv";
    public static List<String> readFile() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(PATH_TO_DATA_FILE));
            return bufferedReader
                    .lines()
                    .skip(1)
                    .collect(Collectors.toList());
        } catch (FileNotFoundException e) {
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
