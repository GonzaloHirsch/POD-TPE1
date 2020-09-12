package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.models.Party;

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

    public TreeSet<Map.Entry<Party,Double>> getResultsFromTable(){
        if(votes.size() == 0) return new TreeSet<>();

        // Summing up the total amount of votes
        double totalVotes = (double) this.votes.values().stream().mapToLong(AtomicLong::get).reduce(0, Long::sum);

        // Will compare first with percentage and then the party
        Comparator<Map.Entry<Party, Double>> ftptComparator = (e1, e2) -> {
            int keyComparison = e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
            if (keyComparison == 0) {
                return e1.getValue().compareTo(e2.getValue());
            }
            return keyComparison;
        };

        TreeSet<Map.Entry<Party,Double>> entries = new TreeSet<>(ftptComparator);
        votes.forEach((key, value) -> entries.add(new AbstractMap.SimpleEntry<>(key, (((Long) value.get()).doubleValue()) / totalVotes)));

        return entries;
    }

}
