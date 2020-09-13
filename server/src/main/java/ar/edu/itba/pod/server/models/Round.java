package ar.edu.itba.pod.server.models;

public enum Round {
    FIRST(0),
    SECOND(1),
    THIRD(2);

    private int value;

    private Round(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
