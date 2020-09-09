package ar.edu.itba.pod;

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
}
