package org.example;

import java.util.Random;

public class Player {
    private int id;
    private String firstName;
    private String lastName;

    public Player() {
        this.id = Utils.getNewPlayerId();
        this.firstName = Utils.FIRST_NAMES.get(new Random().nextInt(Utils.FIRST_NAMES.size()));
        this.lastName = Utils.LAST_NAMES.get(new Random().nextInt(Utils.LAST_NAMES.size()));
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return id == player.id;
    }


    @Override
    public String toString() {
        return "Player id = " + this.id +
                ", name = " + this.firstName +
                " " + this.lastName + "\n";
    }

}
