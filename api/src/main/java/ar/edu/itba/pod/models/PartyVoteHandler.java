package ar.edu.itba.pod.models;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PartyVoteHandler extends Remote {
    void onPartyVote(Vote vote) throws RemoteException;
}
