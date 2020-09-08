package ar.edu.itba.pod;

public class Vote {
    private Party party;
    private Integer table; // FIXME puede ser int -> como prefieran

    public Vote(Party party, Integer table) {
        this.party = party;
        this.table = table;
    }

    public Party getParty() {
        return party;
    }

    public Integer getTable() {
        return table;
    }
}
