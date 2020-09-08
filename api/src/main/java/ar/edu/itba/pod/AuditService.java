package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuditService extends Remote {
    void registerAuditOfficer(String officer, Party party, Integer table, PartyVoteHandler handler) throws RemoteException;
    void notifyPartyVote(Vote vote) throws RemoteException;
}
