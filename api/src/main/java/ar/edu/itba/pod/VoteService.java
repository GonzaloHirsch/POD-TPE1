package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface VoteService extends Remote {
    // returns how many votes were emitted
    int emitVotes(List<Vote> votes) throws RemoteException, ExecutionException, InterruptedException;
}
