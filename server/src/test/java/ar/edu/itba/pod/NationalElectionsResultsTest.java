package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.NationalElection;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class NationalElectionsResultsTest {
    private static NationalElection nationalElection;
    private List<Map<Party, Long>> ballots;

    @Before
    public void setUp() {
        nationalElection = new NationalElection();
        ballots = new ArrayList<>(this.createBallots());
    }


    @Test
    public void testEmitVotes() {
        this.ballots.forEach(v -> nationalElection.emitVote(v));

        assertEquals(Party.JACKALOPE, nationalElection.getNationalElectionWinner());
        System.out.println(nationalElection.getOrderedScoringRoundResults());
        System.out.println(nationalElection.getOrderedAutomaticRunoffResults());
    }
    
    private List<Map<Party, Long>> createBallots() {
        Map<Party, Long> ballot1 = new HashMap<>();
        ballot1.put(Party.TIGER, 2L);
        ballot1.put(Party.LEOPARD, 1L);
        ballot1.put(Party.LYNX, 5L);
        ballot1.put(Party.TURTLE, 3L);
        ballot1.put(Party.BUFFALO, 4L);

        Map<Party, Long> ballot2 = new HashMap<>();
        ballot2.put(Party.TIGER, 2L);
        ballot2.put(Party.JACKALOPE, 4L);
        ballot2.put(Party.LYNX, 1L);
        ballot2.put(Party.OWL, 3L);
        ballot2.put(Party.BUFFALO, 4L);

        Map<Party, Long> ballot3 = new HashMap<>();
        ballot3.put(Party.TIGER, 5L);
        ballot3.put(Party.JACKALOPE, 5L);
        ballot3.put(Party.OWL, 2L);
        ballot3.put(Party.TURTLE, 4L);
        ballot3.put(Party.LEOPARD, 1L);

        return Arrays.asList(ballot1, ballot2, ballot3);
    }
}
