package ar.edu.itba.pod;

import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface QueryService extends Remote {
    List<Map.Entry<Party,Long>> getNationalResults() throws RemoteException;
    List<Map.Entry<Party,Long>> getProvinceResults(Province province) throws RemoteException;
    List<Map.Entry<Party,Long>> getTableResults(Integer tableID) throws RemoteException;
}
