package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.InvalidElectionStateException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public interface VoteService extends Remote {
    /**
     * Exposed service method to emit a single vote
     * @param vote Vote to be emitted
     * @return
     * @throws RemoteException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    void emitVote(Vote vote) throws RemoteException, ExecutionException, InterruptedException, InvalidElectionStateException;
}
