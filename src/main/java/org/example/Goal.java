package org.example;

public class Goal {
    private int id;
    private int minute;
    private Player scorer;

    private final int MAX_SECONDS = 90;
    private final int MIN_SECONDS = 1;
    public Goal(int id, int minute, Player scorer) {
        this.id = id;
        if(minute < MAX_SECONDS && minute > MIN_SECONDS){
            this.minute = minute;
        }
        this.scorer = scorer;
    }

    public int getId() {
        return id;
    }

    public int getMinute() {
        return minute;
    }

    public Player getScorer() {
        return scorer;
    }

    @Override
    public String toString() {
        return "Goal{" + "id=" + id +
                ", scorer=" + scorer +
                '}';
    }
}
