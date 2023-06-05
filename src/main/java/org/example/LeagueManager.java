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

    private final int TEAM_WIN_SCORE = 3;
    private final int TEAM_DRAW_SCORE = 1;
    private final int ROUND = 5;
    private final int MAX_POSSIBLE_MATCHES = 90;
    public static final int MAX_PEOPLE_AT_TEAM = 15;
    private final int FROM_ROUND_FOUR = 25;
    private final int FROM_ROUND_FIVE = 20;
    private final int FROM_ROUND_SEVEN = 10;
    private final int FROM_ROUND_EIGHT = 5;
    private final int SKIP_TWO_TEAMS = 2;
    private final int SKIP_ONE_TEAM = 1;
    private final int DONT_SKIP_ON_TEAM = 0;

    public LeagueManager() {
        List<String> data = FileHandler.readFile();
        teamList = data
                .stream()
                .map(Team::new)
                .collect(toList());
    }

    public List<Match> findMatchesByTeam(int teamId) {
        System.out.println("matches that have been played: ");
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(teamId))
                .collect(toList());
    }

    public List<Team> findTopScoringTeams(int n) {
        List<Team> temp = this.teamList.
                stream()
                .sorted(comparing(team -> getTeamGoalWasScoredCount((Team) team)).reversed())
                .limit(n)
                .collect(toList());
        this.teamList.stream().forEach(team ->{if (!temp.contains(team)){temp.add(team);};});
        return temp;
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
        System.out.println("{id=how much scored,...}");
        return temp
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .sorted(Map.Entry.comparingByValue())
                .collect(HashMap::new, (m, entry) -> m.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    public void createLeagueTable() {
        this.leagueTable = this.teamList.stream()
                .sorted(Comparator.comparing(Team::getPoints).reversed().
                        thenComparing(Comparator.comparing(team -> getTeamGoalWasScoredCount((Team) team) - getTeamGoalWasTakenCount((Team) team))
                                .thenComparing(team -> team.toString())))
                .collect(toList());
    }

    public void addPointsForTeams(Match currentMatch) {
        Team homeTeam = currentMatch.getHomeTeam();
        Team awayTeam = currentMatch.getAwayTeam();

        if (getTeamGoalWasScoredCount(homeTeam) > getTeamGoalWasScoredCount(awayTeam)) {
            homeTeam.addPoints(TEAM_WIN_SCORE);
        } else if (getTeamGoalWasScoredCount(homeTeam) == getTeamGoalWasScoredCount(awayTeam)) {
            homeTeam.addPoints(TEAM_DRAW_SCORE);
            awayTeam.addPoints(TEAM_DRAW_SCORE);
        } else {
            awayTeam.addPoints(TEAM_WIN_SCORE);
        }
    }

    public static List<Player> createPlayerList() {
        List<Player> temp;
        temp = Stream.generate(Player::new).limit(MAX_PEOPLE_AT_TEAM).collect(toList());
        return temp;
    }

    public long getTeamGoalWasScoredCount(Team team) {
        return this.matches
                .stream()
                .filter(match -> match.didTeamPlayGame(team.getId()))
                .map(Match::getGoals)
                .flatMap(List::stream)
                .filter(goal -> team.getPlayerList().contains(goal.getScorer()))
                .count();
    }

    public long getTeamGoalWasTakenCount(Team team) {
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

    public List<Team> getLeagueTable() {
        return leagueTable;
    }

    public List<Goal> generateGoalList(Team team1, Team team2) {
        List<Player> playerList = new ArrayList<>(team1.getPlayerList());
        playerList.addAll(team2.getPlayerList());

        Random random = new Random();
        return Stream
                .generate(() -> new Goal( random.nextInt(MAX_POSSIBLE_MATCHES+1), playerList.get(random.nextInt(playerList.size()))
                )).limit(random.nextInt(ROUND))
                .collect(toList());
    }

    public List<Match> generateMatchRound() {
        Integer size = SKIP_TWO_TEAMS;
        List<Match> output = new ArrayList<>();
        if (possibleMatches.size() == FROM_ROUND_FOUR) {
            size = DONT_SKIP_ON_TEAM;
        } else if (possibleMatches.size() == FROM_ROUND_FIVE) {
            size = SKIP_TWO_TEAMS;
        } else if (possibleMatches.size() == FROM_ROUND_SEVEN) {
            size = SKIP_ONE_TEAM;
        } else if (possibleMatches.size() == FROM_ROUND_EIGHT) {
            size = DONT_SKIP_ON_TEAM;
        } else if (possibleMatches.size() == DONT_SKIP_ON_TEAM) {
            size= null;
        }
        while (output.size() != ROUND) {
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
                    .limit(ROUND)
                    .collect(toList());
            if (output.size() != ROUND) {
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
}
