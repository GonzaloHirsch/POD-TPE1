package ar.edu.itba.pod;


import ar.edu.itba.pod.exceptions.ElectionNotStartedException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface QueryService extends Remote {
    List<Map.Entry<Party,Long>> getNationalResults() throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party,Long>> getProvinceResults(Province province) throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party,Long>> getTableResults(Integer tableID) throws RemoteException, ElectionNotStartedException;
}
