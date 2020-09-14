package ar.edu.itba.pod;

import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import ar.edu.itba.pod.server.models.Table;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class TableTest {

    private static Table table;

    @Before
    public void setUp() {
        table = new Table(0, Province.JUNGLE);
    }

    @Test
    public void testEmitVote() {
        assertEquals(0, table.getVotes(Party.BUFFALO));
        table.emitVote(Party.BUFFALO);
        assertEquals(1, table.getVotes(Party.BUFFALO));
    }
    @Test
    public void testQueryResults(){
        double totalVotes = 0;
        Map<Party, Integer> votesPerParty = new HashMap<>();
        for(Party p : Party.values()){
            int votes = emitRandomVotesForParty(p);
            votesPerParty.put(p, votes);
            totalVotes+=votes;
        }

        TreeSet<MutablePair<Party, Double>> results = table.getResultsFromTable();
        double finalTotalVotes = totalVotes;
        results.forEach(e->{
            assertEquals(new Double(votesPerParty.get(e.getKey())/ finalTotalVotes), e.getValue());
        });
    }

    private int emitRandomVotesForParty(Party party) {
        Random random = new Random();
        int votes = random.nextInt(100);
        for(int i=0; i<votes; i++) {
            table.emitVote(party);
        }
        return votes;
    }
}
