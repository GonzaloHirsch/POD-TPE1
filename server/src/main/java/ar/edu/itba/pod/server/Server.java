package ar.edu.itba.pod.server;

import ar.edu.itba.pod.AuditService;
import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.QueryService;
import ar.edu.itba.pod.VoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public static void main(final String[] args) throws RemoteException, AlreadyBoundException {
        LOG.info("Server Starting ...");

        // Creating the instance of the servant and exporting it
        final Servant gs = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(gs, 0);

        // Binding the services to the interface names
        final Registry registry = LocateRegistry.getRegistry();
        registry.bind(VoteService.class.getName(), remote);
        registry.bind(ManagementService.class.getName(), remote);
        registry.bind(QueryService.class.getName(), remote);
        registry.bind(AuditService.class.getName(), remote);
        LOG.info("Service bound");
    }
}
