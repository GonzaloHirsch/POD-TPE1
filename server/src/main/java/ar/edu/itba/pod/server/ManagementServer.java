package ar.edu.itba.pod.server;

import ar.edu.itba.pod.AuditService;
import ar.edu.itba.pod.ManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ManagementServer {
    private static final Logger LOG = LoggerFactory.getLogger(ManagementServer.class);

    public static void main(final String[] args) throws RemoteException {
        LOG.info("AuditServer Starting ...");

        final ManagementService gs = new Servant();
        final Remote remote = UnicastRemoteObject.exportObject(gs, 0);

        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind("managementService", remote);

        LOG.info("Service bound");
    }
}
