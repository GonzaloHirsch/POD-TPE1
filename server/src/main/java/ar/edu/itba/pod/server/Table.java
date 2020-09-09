package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Party;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
    private final Integer ID;
    private Map<Party, AtomicLong> votes = new HashMap<>();

    public Table(Integer ID) {
        this.ID = ID;
        Arrays.stream(Party.values()).forEach(p -> votes.put(p, new AtomicLong(0)));
    }

    /**
     * Given some party, increments votes value for that party.
     */
    public void emitVote(Party party) {
        votes.get(party).getAndIncrement();
    }

    public Integer getID() {
        return ID;
    }

    public long getVotes(Party party) {
        return votes.get(party).longValue();
    }
}
