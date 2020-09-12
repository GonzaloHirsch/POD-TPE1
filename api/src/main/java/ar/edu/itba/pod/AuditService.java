package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.Party;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuditService extends Remote {
    void registerAuditOfficer(Party party, int table, PartyVoteHandler handler) throws RemoteException, InvalidElectionStateException;
}
