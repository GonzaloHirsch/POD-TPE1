package ar.edu.itba.pod.server.Callables;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Vote;
import ar.edu.itba.pod.server.NationalElection;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class StarBatch implements Callable<Integer> {
    private final List<Vote> votes;
    private final NationalElection nationalElection;

    public StarBatch(List<Vote> votes, NationalElection nationalElection) {
        this.votes = votes;
        this.nationalElection = nationalElection;
    }

    @Override
    public Integer call() throws RemoteException {
        List<Map<Party, Long>> newBallots = new ArrayList<>();
        votes.stream().forEach(v -> newBallots.add(v.getStarVote()));
        // votes are processed in national election
        return nationalElection.addSparBallots(newBallots);
    }
}