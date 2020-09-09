package ar.edu.itba.pod;


import ar.edu.itba.pod.exceptions.ElectionNotStartedException;
import jdk.internal.util.xml.impl.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface QueryService extends Remote {
    List<Map.Entry<Party,Long>> getNationalResults() throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party,Long>> getProvinceResults(Province province) throws RemoteException, ElectionNotStartedException;
    List<Map.Entry<Party,Long>> getTableResults(Integer tableID) throws RemoteException, ElectionNotStartedException;
}
