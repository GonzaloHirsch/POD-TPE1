package ar.edu.itba.pod;

public enum Province {
    JUNGLE("JUNGLE"), SAVANNAH("SAVANNAH"), TUNDRA("TUNDRA");

    private String description;

    private Province(String s){
        this.description = s;
    }

    public String getDescription() {
        return description;
    }
}
