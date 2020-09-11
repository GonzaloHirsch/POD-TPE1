package ar.edu.itba.pod.server.models;

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

    public List<Map.Entry<Party,Double>> getResultsFromTable(){
        if(votes.size() == 0) return null;


        List<Map.Entry<Party,Double>> entries = new ArrayList<>();
        votes.forEach((a,b) -> entries.add(new AbstractMap.SimpleEntry<>(a, ((Long)b.get()).doubleValue())));
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        
        long totalVotes = 0;
        for(Map.Entry<Party,Double> entry : entries){
            totalVotes += entry.getValue();
        }

        final long finalTotalVotes = totalVotes;
        entries.forEach((e) -> e.setValue(e.getValue()/finalTotalVotes));

        return entries;
    }

}
