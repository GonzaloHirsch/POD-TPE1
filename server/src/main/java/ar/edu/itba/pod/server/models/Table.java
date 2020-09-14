package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.comparators.DoubleComparator;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
    private final Integer ID;
    private final Province province;
    private final Map<Party, AtomicLong> votes = new HashMap<>();

    private static final DoubleComparator doubleComparator = new DoubleComparator();

    public Table(Integer ID, Province province) {
        this.ID = ID;
        this.province = province;
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

    public Province getProvince() {
        return province;
    }

    public Map<Party, AtomicLong> getVotes() {
        return this.votes;
    }

    public long getVotes(Party party) {
        return votes.get(party).longValue();
    }

    public TreeSet<MutablePair<Party,Double>> getResultsFromTable(){
        TreeSet<MutablePair<Party, Double>> entries;

        // Synchronizing in order to stop incoming votes while calculating the result
        synchronized (this.votes) {
            if (votes.size() == 0) return new TreeSet<>();

            // Summing up the total amount of votes
            double totalVotes = (double) this.votes.values().stream().mapToLong(AtomicLong::get).reduce(0, Long::sum);

            entries = new TreeSet<>(doubleComparator);
            votes.forEach((key, value) -> entries.add(new MutablePair<>(key, (((Long) value.get()).doubleValue()) / totalVotes)));
        }
        return entries;
    }
}
