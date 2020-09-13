package ar.edu.itba.pod;

import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import ar.edu.itba.pod.server.models.Round;
import ar.edu.itba.pod.server.models.StateElection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class StateElectionTest {

    private static StateElection stateElection;
    private static Province province = Province.JUNGLE;
    private static List<List<Party>> ballots = new ArrayList<>();
    private static int votes;

    @BeforeClass
    public static void setUp() {
        stateElection = new StateElection();
        createBallots(ballots);
        for(List<Party> v : ballots) stateElection.emitVote(province, v);
        stateElection.computeStateElectionResults();
    }

    @Test
    public void testEmitVotes() {
        assertEquals(votes, stateElection.getVotesQuantity());
    }

    @Test
    public void testWinnersPerRound() {
        assertEquals(Party.TIGER, stateElection.getWinner(province, Round.FIRST));
        assertEquals(Party.JACKALOPE, stateElection.getWinner(province, Round.SECOND));
        assertEquals(Party.LEOPARD, stateElection.getWinner(province, Round.THIRD));
    }

    @Test
    public void testWinners() {
        List<Party> winners = stateElection.getWinners(province);
        assertEquals(Round.values().length, winners.size());
        assertEquals(Party.TIGER, winners.get(Round.FIRST.getValue()));
        assertEquals(Party.JACKALOPE, winners.get(Round.SECOND.getValue()));
        assertEquals(Party.LEOPARD, winners.get(Round.THIRD.getValue()));
    }

    @Test
    public void testFirstRoundScores() {
        Map<Party, Double> firstRound = stateElection.getResultsRound(province, Round.FIRST);
        assertEquals(new Double(2.0), firstRound.get(Party.JACKALOPE));
        assertEquals(new Double(2.0), firstRound.get(Party.OWL));
        assertEquals(new Double(3.0), firstRound.get(Party.TIGER));
        assertEquals(new Double(2.0), firstRound.get(Party.LEOPARD));
        assertEquals(new Double(2.0), firstRound.get(Party.TURTLE));
        assertEquals(new Double(1.0), firstRound.get(Party.LYNX));
        assertEquals(new Double(0.0), firstRound.get(Party.BUFFALO));
    }

    @Test
    public void testSecondRoundScores() {
        Map<Party, Double> firstRound = stateElection.getResultsRound(province, Round.SECOND);
        assertEquals(new Double(1.5), firstRound.get(Party.JACKALOPE));
        assertEquals(new Double(1.0), firstRound.get(Party.OWL));
        assertNull(firstRound.get(Party.TIGER));
        assertEquals(new Double(1.5), firstRound.get(Party.LEOPARD));
        assertEquals(new Double(1.5), firstRound.get(Party.TURTLE));
        assertEquals(new Double(0.5), firstRound.get(Party.LYNX));
        assertEquals(new Double(0.0), firstRound.get(Party.BUFFALO));
    }

    @Test
    public void testThirdRoundScores() {
        Map<Party, Double> firstRound = stateElection.getResultsRound(province, Round.THIRD);
        assertNull(firstRound.get(Party.JACKALOPE));
        assertEquals(new Double((1/(double)3)+(1/(double)2)), firstRound.get(Party.OWL));
        assertNull(firstRound.get(Party.TIGER));
        assertEquals(new Double(1.0), firstRound.get(Party.LEOPARD));
        assertEquals(new Double(1.0), firstRound.get(Party.TURTLE));
        assertEquals(new Double(0.5), firstRound.get(Party.LYNX));
        assertEquals(new Double(0.0), firstRound.get(Party.BUFFALO));
    }

    private static void createBallots(List<List<Party>> ballots) {

        List<Party> ballot1 = new ArrayList<>();
        ballot1.add(Party.JACKALOPE);
        ballot1.add(Party.OWL);
        ballot1.add(Party.TIGER);
        votes++;

        List<Party> ballot2 = new ArrayList<>();
        ballot2.add(Party.JACKALOPE);
        ballot2.add(Party.LEOPARD);
        ballot2.add(Party.TURTLE);
        votes++;

        List<Party> ballot3 = new ArrayList<>();
        ballot3.add(Party.TURTLE);
        ballot3.add(Party.OWL);
        ballot3.add(Party.TIGER);
        votes++;

        List<Party> ballot4 = new ArrayList<>();
        ballot4.add(Party.LEOPARD);
        ballot4.add(Party.LYNX);
        ballot4.add(Party.TIGER);
        votes++;

        ballots.add(ballot1);
        ballots.add(ballot2);
        ballots.add(ballot3);
        ballots.add(ballot4);
    }
}
