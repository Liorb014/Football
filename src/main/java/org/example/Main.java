package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LeagueManager leagueManager = new LeagueManager();
        leagueManager.generatePossibleMatches();
        roundOfMatches(leagueManager , Utils.ROUND_START);
    }

    public static void roundOfMatches(LeagueManager leagueManager, int numberOfRounds){
        if (numberOfRounds<Utils.ROUND_END){
            leagueManager.generateMatchRound()
                    .stream()
                    .forEach(match ->{
                        System.out.println(match.getHomeTeam().getName() +"---------VS--------" + match.getAwayTeam().getName());
                        leagueManager.addPointsForTeams(match);
                        leagueManager.createLeagueTable();
                        countDown(Utils.TIME_TO_COUNTDOWN);
                        Utils.sleep(Utils.TIME_TO_COUNTDOWN);
                        System.out.println("\n"+match);
                        System.out.println("\n"+match.getHomeTeam().getName() +"'s goals : " + leagueManager.getTeamGoalWasScoredCount(match.getHomeTeam()) + "   " +
                                match.getAwayTeam().getName() +"'s goals : " + leagueManager.getTeamGoalWasScoredCount(match.getAwayTeam())+ "\n");
                    });
            System.out.println("league table: "+leagueManager.getLeagueTable());
            optionsMenu(leagueManager,numberOfRounds);
        }
    }

    public synchronized static void countDown(int counter){
        new Thread(()->{
            if (counter>Utils.END_OF_TIME){
                System.out.println(counter);
                Utils.sleep(Utils.TIME_TO_COUNT);
                countDown(counter-Utils.TIME_TO_COUNT);
            }
        }).start();
    }

    public static void optionsMenu(LeagueManager leagueManager, int numberOfRounds){;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\npress 1 to find matches by team" +
                "\npress 2 to find top scoring teams" +
                "\npress 3 to find players with the amount of your require goals"+
                "\npress 4 to get team by position"+
                "\npress 5 to get top scorers"+
                "\npress 6 to start the next round"
        );
        try {
            int userChoice = scanner.nextInt();
            switch (userChoice){
                case 1 -> {
                    System.out.println("please enter the team id:  ");
                    System.out.println(leagueManager.findMatchesByTeam(scanner.nextInt()));
                    optionsMenu(leagueManager,numberOfRounds);
                }

                case 2 -> {
                    System.out.println("please enter your desire amount of top teams u want to see: ");
                    System.out.println(leagueManager.findTopScoringTeams(scanner.nextInt()));
                    optionsMenu(leagueManager,numberOfRounds);

                }
                case 3 -> {
                    System.out.println("please enter a number of gaols wnd u will see the amount of players who score that much: ");
                    System.out.println(leagueManager.findPlayersWithAtLeastNGoals(scanner.nextInt()));
                    optionsMenu(leagueManager,numberOfRounds);
                }
                case 4 -> {
                    System.out.println("please enter the number of the position of the team of the score bord(league table): ");
                    System.out.println(leagueManager.getTeamByPosition(scanner.nextInt()-1));
                    optionsMenu(leagueManager,numberOfRounds);
                }
                case 5 -> {
                    System.out.println("please enter a number to get the range of players who's scored the most and what their id's  ");
                    System.out.println(leagueManager.getTopScorers(scanner.nextInt()));
                    optionsMenu(leagueManager,numberOfRounds);
                }
                case 6 -> {
                    roundOfMatches(leagueManager,numberOfRounds+1);
                }
                default -> optionsMenu(leagueManager,numberOfRounds);
            }
        } catch (InputMismatchException | IndexOutOfBoundsException | IllegalArgumentException e  ){
            System.out.println("enter valid number");
            optionsMenu(leagueManager , numberOfRounds);
        }
    }


}