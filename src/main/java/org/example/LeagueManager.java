package org.example;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public class LeagueManager {
    public List<Match> matches = new ArrayList<>();
    public List<Team> teamList;
    public List<Match> possibleMatches = new ArrayList<>();
    public List<Team> leagueTable;

    public List<Team> getLeagueTable() {
        return leagueTable;
    }

    public LeagueManager() {
        List<String> data = FileHandler.readFile();
        teamList = data
                .stream()
                .map(Team::new)
                .collect(toList());
    }

    public void createLeagueTable() {
        this.leagueTable = this.teamList.stream()
                .sorted(Comparator.comparing(Team::getPoints).reversed().
                        thenComparing(Comparator.comparing(team -> getTeamGoalCount((Team) team) - getTeamGoalTakenCount((Team) team))
                                .thenComparing(team -> team.toString())))
                .collect(toList());
    }

    public List<Match> findMatchesByTeam(int teamId) {
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(teamId))
                .collect(toList());
        // working
    }

    public List<Team> findTopScoringTeams(int n) {
        return this.teamList.
                stream()
                .sorted(comparing(team -> getTeamGoalCount((Team) team)).reversed())
                .limit(n)
                .collect(toList());
    }

    public List<Player> findPlayersWithAtLeastNGoals(int n) {
        Map<Player, Long> temp = this.matches
                .stream()
                .map(Match::getGoals)
                .flatMap(List::stream)
                .map(Goal::getScorer)
                .collect(groupingBy(player -> player, counting()));

        return temp
                .keySet()
                .stream()
                .filter(key -> temp.get(key) >= n)
                .collect(toList());
        //working
    }

    public Team getTeamByPosition(int position) {
        return leagueTable.get(position);
    }

    public Map<Integer, Integer> getTopScorers(int n) {
        Map<Integer, Integer> temp = this.matches
                .stream()
                .map(Match::getGoals)
                .flatMap(List::stream)
                .map(Goal::getScorer)
                .collect(groupingBy(Player::getId, summingInt(player -> 1)));
        return temp
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .sorted(Map.Entry.comparingByValue())
                .collect(HashMap::new, (m, entry) -> m.put(entry.getKey(), entry.getValue()), HashMap::putAll);
        //working
    }

    public void addPointsForTeams(Match currentMatch) {
        Team homeTeam = currentMatch.getHomeTeam();
        Team awayTeam = currentMatch.getAwayTeam();

        if (getTeamGoalCount(homeTeam) > getTeamGoalCount(awayTeam)) {
            homeTeam.addPoints(TEAM_WIN_SCORE);
        } else if (getTeamGoalCount(homeTeam) == getTeamGoalCount(awayTeam)) {
            homeTeam.addPoints(TEAM_DRAW_SCORE);
            awayTeam.addPoints(TEAM_DRAW_SCORE);
        } else {
            awayTeam.addPoints(TEAM_WIN_SCORE);
        }
    }

  /*  public Team findPlayerTeam(Player player) {
        return this.teamList
                .stream()
                .filter(team -> team.getPlayerList().contains(player)).findFirst().get();
    }*/

    public static List<Player> createPlayerList() {
        List<Player> temp;
        temp = Stream.generate(Player::new).limit(MAX_PEOPLE_AT_TEAM).collect(toList());
        return temp;
    }

    public long getTeamGoalCount(Team team) {
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(team.getId()))
                .map(Match::getGoals)
                .flatMap(List::stream)
                .filter(goal -> team.getPlayerList().contains(goal.getScorer()))
                .count();
    }

    public long getTeamGoalTakenCount(Team team) {
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(team.getId()))
                .map(Match::getGoals)
                .flatMap(List::stream)
                .filter(goal -> !team.getPlayerList().contains(goal.getScorer()))
                .count();
    }

    public List<Match> generatePossibleMatches() {
        possibleMatches = teamList.stream()
                .flatMap(team1 -> teamList
                        .stream()
                        .filter(team2 -> team1 != team2 && team2.getId() > team1.getId())
                        .map(team2 -> new Match(team1, team2, generateGoalList(team1, team2))))
                .toList();
        return possibleMatches;
    }


    public List<Match> generateMatchRound() {
        int size = 2;
        List<Match> output = new ArrayList<>();
        if (possibleMatches.size() == 25) {
            size = 0;
        } else if (possibleMatches.size() == 20) {
            size = 2;
        } else if (possibleMatches.size() == 10) {
            size = 1;
        } else if (possibleMatches.size() == 5) {
            size = 0;
        } else if (possibleMatches.size() == 0) {
            return null;
        }
        while (output.size() != 5) {
            output.clear();
            List<Team> playedTeams = new ArrayList<>();
            output = possibleMatches
                    .stream()
                    .skip(size)
                    .filter(match -> {
                        if (!playedTeams.contains(match.getHomeTeam()) && !playedTeams.contains(match.getAwayTeam())) {
                            playedTeams.add(match.getHomeTeam());
                            playedTeams.add(match.getAwayTeam());
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .limit(5)
                    .collect(toList());
            if (output.size() != 5) {
                List<Match> newLIst = new ArrayList<>(possibleMatches);
                Collections.reverse(newLIst);
                possibleMatches = List.copyOf(newLIst);
            }
        }

        output.
                stream()
                .forEach(match -> match.setId(Utils.getNewMatchId()));

        output
                .stream()
                .forEach(match -> match.getGoals()
                        .stream()
                        .forEach(goal -> goal.setId(Utils.getNewGoalId())));

        possibleMatches = possibleMatches
                .stream()
                .filter(Predicate.not(output::contains))
                .collect(toList());

        matches.addAll(output);
        return output;
    }

    public List<Team> getLeagueTable() {
        return leagueTable;
    }

    public List<Goal> generateGoalList(Team team1, Team team2) {
        List<Player> playerList = new ArrayList<>(team1.getPlayerList());
        playerList.addAll(team2.getPlayerList());

        Random random = new Random();
        return Stream
                .generate(() -> new Goal(Utils.getNewGoalId(), random.nextInt(91), playerList.get(random.nextInt(playerList.size()))
                )).limit(random.nextInt(5))
                .collect(toList());
    }
}
