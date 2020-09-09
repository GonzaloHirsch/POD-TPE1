package ar.edu.itba.pod.server;

import ar.edu.itba.pod.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class QueryServer {
    private static final Logger LOG = LoggerFactory.getLogger(QueryServer.class);

    public static void main(final String[] args) throws RemoteException {
        LOG.info("QueryServer Starting ...");

        final QueryService qs = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(qs, 0);

        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind("queryService", remote);

        LOG.info("Service bound");
    }
}
