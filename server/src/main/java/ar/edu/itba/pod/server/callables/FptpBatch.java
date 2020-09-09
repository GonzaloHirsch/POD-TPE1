package ar.edu.itba.pod.server.callables;

import ar.edu.itba.pod.Vote;
import ar.edu.itba.pod.server.models.Table;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class FptpBatch implements Callable<Integer> {
    private final List<Vote> votes;
    private final HashMap<Integer, Table> tables;

    public FptpBatch(List<Vote> votes, HashMap<Integer, Table> tables) {
        this.votes = votes;
        this.tables = tables;
    }

    @Override
    public Integer call() throws RemoteException {
        votes.stream().forEach(v -> tables.get(v.getTable()).emitVote(v.getFptpVote()));
        return votes.size();
    }
}
