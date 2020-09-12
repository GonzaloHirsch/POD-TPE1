package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.ElectionState;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ManagementService extends Remote {
    void openElection() throws RemoteException, InvalidElectionStateException;
    void closeElection() throws RemoteException, InvalidElectionStateException;
    ElectionState getElectionState() throws RemoteException;
}
