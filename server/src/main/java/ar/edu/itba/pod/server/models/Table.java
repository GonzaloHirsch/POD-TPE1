package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.models.Party;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
    private final Integer ID;
    private Map<Party, AtomicLong> votes = new HashMap<>();

    private static final Comparator<Map.Entry<Party, Double>> fptpComparator = (e1, e2) -> {
        int valueComparison = e2.getValue().compareTo(e1.getValue());
        if (valueComparison == 0) {
            return e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
        }
        return valueComparison;
    };

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

    public Map<Party, AtomicLong> getVotes() {
        return this.votes;
    }

    public long getVotes(Party party) {
        return votes.get(party).longValue();
    }

    public TreeSet<Map.Entry<Party,Double>> getResultsFromTable(){
        TreeSet<Map.Entry<Party, Double>> entries;

        // Syncronizing in order to stop incoming votes while calculating the result
        synchronized (this.votes) {
            if (votes.size() == 0) return new TreeSet<>();

            // Summing up the total amount of votes
            double totalVotes = (double) this.votes.values().stream().mapToLong(AtomicLong::get).reduce(0, Long::sum);

            entries = new TreeSet<>(fptpComparator);
            votes.forEach((key, value) -> entries.add(new AbstractMap.SimpleEntry<>(key, (((Long) value.get()).doubleValue()) / totalVotes)));
        }
        return entries;
    }

}
