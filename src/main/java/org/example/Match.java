package org.example;

import com.sun.source.tree.Scope;

import java.util.*;

public class Match {
    private int id;
    private Team homeTeam;
    private Team awayTeam;
    private List<Goal> goals;

    public Match( Team homeTeam, Team awayTeam, List<Goal> goals) {
        this.id = 0;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.goals = goals;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public boolean didTeamPlayGame(int id){
        return this.awayTeam.sameID(id) || this.homeTeam.sameID(id);
    }


    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Match " +
                "id = " + this.id +
                ", homeTeam = " + this.homeTeam .getName()+
                ", awayTeam = " + this.awayTeam.getName() +
                ", goals = " + goalsDisplay() ;
    }
    public String goalsDisplay(){
        if (this.goals.isEmpty()){
            return  "no goals got scored this match";
        }else return this.goals.toString();
    }
}
