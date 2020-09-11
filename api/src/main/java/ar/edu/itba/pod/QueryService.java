package ar.edu.itba.pod;


import ar.edu.itba.pod.exceptions.ElectionNotStartedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface QueryService extends Remote {
    List<Map.Entry<Party,Long>> getNationalResults() throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party,Long>> getProvinceResults(Province province) throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party, Double>> getTableResults(Integer tableID) throws RemoteException, ElectionNotStartedException;
}
