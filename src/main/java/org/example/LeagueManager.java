package org.example;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class LeagueManager {
    public List<Match> matches = new ArrayList<>();
    public List<Team> teamList;
    public List<Match> possibleMatches = new ArrayList<>();
    public List<Team> leagueTable;

    public LeagueManager() {
        List<String> data = FileHandler.readFile();
        teamList = data
                .stream()
                .map(Team::new)
                .collect(toList());

    }

    public void extracted() {
        leagueTable = teamList.stream()
                .sorted(Comparator.comparing(Team::getPoints).reversed())
            //    .sorted(Comparator.comparing().reversed())
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
        Map<Team, Long> temp = this.matches
                .stream()
                .map(match -> match.getGoals())
                .flatMap(List::stream)
                .map(Goal::getScorer)
                .map(player -> findPlayerTeam(player))
                .collect(groupingBy(Function.identity(), counting()));

        return temp.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
        //working ( if team did not score it will now count )
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

    public Map<Integer, Integer> getTopScorers(int n){
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
    public void addPointsForTeams (Match currentMatch) {
        Team homeTeam = currentMatch.getHomeTeam();
        Team awayTeam = currentMatch.getAwayTeam();
        Long goalsForHomeTeam = currentMatch.getGoals().stream()
                .map(Goal::getScorer)
                .filter(player -> findPlayerTeam(player) == homeTeam)
                .count();
        Long goalsForAwayTeam = currentMatch.getGoals().stream()
                .map(Goal::getScorer)
                .filter(player -> findPlayerTeam(player) == awayTeam)
                .count();
        if (goalsForHomeTeam > goalsForAwayTeam) {
            homeTeam.addPoints(3);
        } else if (goalsForHomeTeam == goalsForAwayTeam) {
            homeTeam.addPoints(1);
            awayTeam.addPoints(1);
        } else {
            awayTeam.addPoints(3);
        }
    }

    public Team findPlayerTeam(Player player) {
        Team team1 = this.teamList
                .stream()
                .filter(team -> team.getPlayerList().contains(player)).findFirst().get();
        return team1;
    }

    public static List<Player> createPlayerList() {
        List<Player>  temp = new ArrayList<>();
        temp = Stream.generate(Player::new).limit(15).collect(toList());
        return temp;
    }

    public long getTeamGoalCount(Team team){
      return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(team.getId()))
                .map(Match::getGoals)
                .flatMap(List::stream)
              .filter(goal -> team.getPlayerList().contains(goal.getScorer()))
                .collect(counting());
    }

    public long getTeamGoalNotCount(Team team){
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(team.getId()))
                .map(Match::getGoals)
                .flatMap(List::stream)
                .filter(goal -> !team.getPlayerList().contains(goal.getScorer()))
                .collect(counting());
    }

    public List<Match> generatePossibleMatches() {
        possibleMatches = teamList.stream()
                .flatMap(team1 -> teamList
                        .stream()
                        .filter(team2 -> team1 != team2 && team2.getId() > team1.getId())
                        .map(team2 -> new Match(Utils.getNewMatchId(), team1, team2, generateGoalList(team1, team2))))
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
                        // Check if the home team and away team have already played.
                        if (!playedTeams.contains(match.getHomeTeam()) && !playedTeams.contains(match.getAwayTeam())) {

                            // Add the home team and away team to the played teams set.
                            playedTeams.add(match.getHomeTeam());
                            playedTeams.add(match.getAwayTeam());

                            // Return true to indicate that the match should be included in the output.
                            return true;
                        } else {
                            // Return false to indicate that the match should not be included in the output.
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
        possibleMatches = possibleMatches
                .stream()
                .filter(Predicate.not(output::contains))
                .collect(toList());
        matches.addAll(output);
        return output;
    }

    public List<Goal> generateGoalList (Team team1, Team team2) {
        List<Player> playerList = new ArrayList<>(team1.getPlayerList());
        playerList.addAll(team2.getPlayerList());

        Random random = new Random();
        return Stream
                .generate(() -> new Goal(Utils.getNewGoalId(), random.nextInt(91), playerList.get(random.nextInt(playerList.size()))
                )).limit(random.nextInt(5))
                .collect(toList());
    }
}
