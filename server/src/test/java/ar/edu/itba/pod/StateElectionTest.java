package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.StateElection;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class StateElectionTest {

    private static StateElection stateElection;

    @Before
    public void setUp() {
        stateElection = new StateElection();
    }

    @Test
    public void testProvincesWithNoVotes() {
        for(Province p : Province.values())
            assertEquals(0, stateElection.getBallotsPerProvince(p).size());
    }

    @Test
    public void testEmitVotes() {
        List<Party> spavVote2 = new ArrayList<>();
        List<Party> spavVote1 = new ArrayList<>(Arrays.asList(Party.values()));
        spavVote2.add(Party.TURTLE);
        spavVote2.add(Party.BUFFALO);

        stateElection.emitVote(Province.JUNGLE, spavVote1);

        stateElection.emitVote(Province.SAVANNAH, spavVote2);

        assertEquals(0, stateElection.getBallotsPerProvince(Province.TUNDRA).size());
        assertEquals(1, stateElection.getBallotsPerProvince(Province.SAVANNAH).size());
        assertEquals(1, stateElection.getBallotsPerProvince(Province.JUNGLE).size());
    }
}
