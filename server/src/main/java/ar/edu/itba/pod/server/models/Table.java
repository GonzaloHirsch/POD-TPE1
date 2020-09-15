package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.comparators.DoubleComparator;
import ar.edu.itba.pod.exceptions.NoVotesRegisteredException;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
    private final Integer ID;
    private final Province province;
    private final Map<Party, AtomicLong> votes = new HashMap<>();

    private final DoubleComparator doubleComparator = new DoubleComparator();

    public Table(Integer ID, Province province) {
        this.ID = ID;
        this.province = province;
        Arrays.stream(Party.values()).forEach(p -> votes.put(p, new AtomicLong(0)));
    }

    /**
     * Given some party, increments votes value for that party.
     */
    public void emitVote(Party party) {
        this.votes.get(party).getAndIncrement();
    }

    public Integer getID() {
        return this.ID;
    }

    public Province getProvince() {
        return this.province;
    }

    public Map<Party, AtomicLong> getVotes() {
        return this.votes;
    }

    public long getVotes(Party party) {
        return this.votes.get(party).longValue();
    }

    public long votesQuantity() {
        return this.votes.entrySet().stream().mapToLong(p -> p.getValue().longValue()).sum();
    }

    public TreeSet<MutablePair<Party,Double>> getResultsFromTable() throws NoVotesRegisteredException {
        TreeSet<MutablePair<Party, Double>> entries;

        // Synchronizing in order to stop incoming votes while calculating the result
        synchronized (this.votes) {
            boolean noVotes = this.votes.entrySet().stream().allMatch(e -> e.getValue().get() == 0L);
            if(noVotes) {
                throw new NoVotesRegisteredException();
            }

            // Summing up the total amount of votes
            double totalVotes = (double) this.votes.values().stream().mapToLong(AtomicLong::get).reduce(0, Long::sum);

            entries = new TreeSet<>(this.doubleComparator);
            this.votes.forEach((key, value) -> entries.add(new MutablePair<>(key, ((((Long) value.get()).doubleValue()) / totalVotes) * 100.0 )));
        }
        return entries;
    }
}
