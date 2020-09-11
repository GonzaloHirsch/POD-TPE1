package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.Table;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class TableTest {

    private static Table table;

    @Before
    public void setUp() {
        table = new Table(0);
    }

    @Test
    public void testEmitVote() {
        assertEquals(0, table.getVotes(Party.BUFFALO));
        table.emitVote(Party.BUFFALO);
        assertEquals(1, table.getVotes(Party.BUFFALO));
    }
    @Test
    public void testQuery(){
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.BUFFALO);
        table.emitVote(Party.JACKALOPE);
        table.emitVote(Party.LEOPARD);
        table.emitVote(Party.LYNX);
        table.emitVote(Party.OWL);
        table.emitVote(Party.TIGER);
        table.emitVote(Party.TURTLE);

        TreeSet<Map.Entry<Party, Double>> results =table.getResultsFromTable();
        results.forEach(e->{
            System.out.println("PARTY " + e.getKey() + " - Voting: " + e.getValue());
        });
    }
}
