package ar.edu.itba.pod;

import ar.edu.itba.pod.server.Table;
import org.junit.Before;
import org.junit.Test;

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
}
