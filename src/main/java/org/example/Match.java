package org.example;

import com.sun.source.tree.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Match {
    private int id;
    private Team homeTeam;
    private Team awayTeam;
    private List<Goal> goals;


    public Long getHomeGoals() {
        return homeGoals;
    }

    public Long getAwayGoals() {
        return awayGoals;
    }

    public Match(int id, Team homeTeam, Team awayTeam, List<Goal> goals) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.goals = goals;
    }

    private Long getTeamGoals (Team team){
        return this.goals.stream()
                .map(Goal::getScorer)
                .filter(player -> team.getPlayerList().contains(player)).count();
    }

    public int getId() {
        return id;
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

//    private int randomMinute () {
//        Random random = new Random();
//        int randomMinute = random.nextInt(1,11);
//        return randomMinute;
//    }
//    private Player getRandomScorer () {
//
//    }
//    public void addGoal() {
//        Goal goal = new Goal(Utils.getNewGoalId(),randomMinute(), )
//        this.goals =
//    }


    public  List<Team> getTeams(){
        return List.of(this.homeTeam , this.awayTeam);
    }


    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                ", goals=" + goals +
                '}';
    }
}
