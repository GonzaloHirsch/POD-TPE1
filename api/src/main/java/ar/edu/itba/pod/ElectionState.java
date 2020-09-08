package ar.edu.itba.pod;

public enum ElectionState {
    OPEN("Elections are in progress"), CLOSED("Elections are finished"), PENDING("Elections have not started");

    private String description;

    private ElectionState(String s){
        this.description = s;
    }

    public String getDescription() {
        return description;
    }
}
