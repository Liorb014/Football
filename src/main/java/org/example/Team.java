package org.example;

import java.util.List;

public class Team {

    private int id;
    private String name;
    private List<Player> playerList;
    private int points;
    private Integer position;
    public static final int POINTS_START = 0;
    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;

    public Team(String line) {
        String[] temp =line.split(",");
        this.id = Integer.parseInt(temp[ID_INDEX]);
        this.name = temp[NAME_INDEX];
        this.playerList=LeagueManager.createPlayerList();
        this.points = POINTS_START;
        this.position = null;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public boolean sameID(int id){
        return this.id == id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "\n Team name : " +getName() +" Team ID: " +getId() +" ,points "+ points + " ";
    }



    public List<Player> getPlayerList() {
        return playerList;
    }


}
