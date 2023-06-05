package org.example;

public class Goal {
    private int id;
    private int minute;
    private Player scorer;
    private final int MAX_SECONDS = 90;
    private final int MIN_SECONDS = 1;

    public Goal( int minute, Player scorer) {
        this.id = 0;
        if (minute < MAX_SECONDS && minute > MIN_SECONDS) {
            this.minute = minute;
        }
        this.scorer = scorer;
    }
    public Player getScorer() {
        return scorer;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "\n Goal id = " + this.id +", minute  = " + minute + " scorer = " + this.scorer ;
    }
}
