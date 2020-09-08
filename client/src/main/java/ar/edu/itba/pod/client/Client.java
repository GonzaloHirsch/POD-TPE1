package ar.edu.itba.pod.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        LOG.info("Client has started...");
        //final GenericService service = (GenericService) Naming.lookup("//127.0.0.1:1099/service");

        //UserAvailableCallbackHandler handler = new UserAvailableCallbackHandlerImpl();

        //UnicastRemoteObject.exportObject(handler, 0);
    }
}
