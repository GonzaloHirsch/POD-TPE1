package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuditService extends Remote {
    void registerAuditOfficer(Party party, int table, PartyVoteHandler handler) throws RemoteException;
}
