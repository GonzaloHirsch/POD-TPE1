package ar.edu.itba.pod.models;

import java.util.Optional;

public enum ElectionState {
    OPEN("Elections are in progress"), CLOSED("Elections are finished"), PENDING("Elections have not started");

    private String description;

    private ElectionState(String s){
        this.description = s;
    }

    public String getDescription() {
        return description;
    }

    public static ElectionState fromValue(String s) throws RuntimeException {
        String value = Optional.ofNullable(s).orElseThrow(RuntimeException::new).toUpperCase();
        for (ElectionState state : ElectionState.values()){
            if (value.equals(state.description)){
                return state;
            }
        }
        throw new RuntimeException();
    }
}
