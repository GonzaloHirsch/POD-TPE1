package ar.edu.itba.pod;

import ar.edu.itba.pod.server.StateElection;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Party> starVote1 = new ArrayList<>();
        List<Party> starVote2 = new ArrayList<>();

        for(Party p : Party.values()) starVote1.add(p);
        starVote2.add(Party.TURTLE);
        starVote2.add(Party.BUFFALO);

        List<List<Party>> jungleBallots = new ArrayList<>();
        jungleBallots.add(starVote1);
        List<List<Party>> savannahBallots = new ArrayList<>();
        savannahBallots.add(starVote2);

        Map<Province, List<List<Party>>> stateBallots = new HashMap<>();
        stateBallots.put(Province.JUNGLE, jungleBallots);
        stateBallots.put(Province.SAVANNAH, savannahBallots);

        stateElection.emitVotes(stateBallots);

        assertEquals(0, stateElection.getBallotsPerProvince(Province.TUNDRA).size());
        assertEquals(1, stateElection.getBallotsPerProvince(Province.SAVANNAH).size());
        assertEquals(1, stateElection.getBallotsPerProvince(Province.JUNGLE).size());
    }
}
