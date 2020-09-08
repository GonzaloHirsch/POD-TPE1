package ar.edu.itba.pod.server;

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
        //final GenericService gs = new GenericServiceImpl();
        //final Remote remote = UnicastRemoteObject.exportObject(gs, 0);

        final Registry registry = LocateRegistry.getRegistry();
        //registry.bind("service", remote);
        LOG.info("Service bound");
    }
}
