package ar.edu.itba.pod.models;

import java.util.Optional;

public enum Party {
    TIGER("TIGER"), LEOPARD("LEOPARD"), LYNX("LYNX"), TURTLE("TURTLE"),
    OWL("OWL"), JACKALOPE("JACKALOPE"), BUFFALO("BUFFALO");

    private String description;

    private Party(String s){
        this.description = s;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Method to obtain an instance of the enum object given the string value
     * @param s value of the enum
     * @return Enum value
     * @throws RuntimeException if the value is invalid
     */
    public static Party fromValue(String s) throws RuntimeException {
        String value = Optional.ofNullable(s).orElseThrow(RuntimeException::new).toUpperCase();
        for (Party party : Party.values()){
            if (value.equals(party.description)){
                return party;
            }
        }
        throw new RuntimeException();
    }
}
