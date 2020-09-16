package ar.edu.itba.pod;


import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.exceptions.NoVotesRegisteredException;
import ar.edu.itba.pod.models.Province;
import ar.edu.itba.pod.models.ElectionResults;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface QueryService extends Remote {
    ElectionResults getNationalResults() throws RemoteException, InvalidElectionStateException, NoVotesRegisteredException;
    ElectionResults getProvinceResults(Province province) throws RemoteException, InvalidElectionStateException, NoVotesRegisteredException;
    ElectionResults getTableResults(Integer tableID) throws RemoteException, InvalidElectionStateException, NoVotesRegisteredException;
}
