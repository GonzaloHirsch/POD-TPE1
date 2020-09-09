package ar.edu.itba.pod.server.Callables;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Province;
import ar.edu.itba.pod.Vote;
import ar.edu.itba.pod.server.StateElection;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Callable;

public class SpavBatch implements Callable<Integer> {
    private final List<Vote> votes;
    private final StateElection stateElection;

    public SpavBatch(List<Vote> votes, StateElection stateElection) {
        this.votes = votes;
        this.stateElection = stateElection;
    }

    @Override
    public Integer call() throws RemoteException {
        Map<Province, List<List<Party>>> newBallots = new HashMap<>();
        Arrays.stream(Province.values()).forEach(p -> newBallots.put(p, new ArrayList<>()));
        votes.stream().forEach(v -> newBallots.get(v.getProvince()).add(v.getSpavVote()));
        stateElection.emitVotes(newBallots);
        return votes.size();
    }
}