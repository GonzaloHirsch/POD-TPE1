package ar.edu.itba.pod;

import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.TreeSet;

public interface QueryService extends Remote {
    List<TreeSet<Pair>> getNationalResults() throws RemoteException;
    List<TreeSet<Pair>> getProvinceResults(Province province) throws RemoteException;
    List<TreeSet<Pair>> getTableResults(Integer tableID) throws RemoteException;
}
