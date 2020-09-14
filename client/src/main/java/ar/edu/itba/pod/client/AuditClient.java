package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.AuditClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.PartyVoteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuditClient {
    private static final Logger LOG = LoggerFactory.getLogger(AuditClient.class);

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException, InvalidElectionStateException {
        AuditClientArguments clientArguments = new AuditClientArguments();

        // Parsing the arguments
        try {
            clientArguments.parseArguments();
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
        }

        final AuditService service = (AuditService) Naming.lookup("//" + clientArguments.getServerAddress() + "/" + AuditService.class.getName());

        PartyVoteHandler handler = new PartyVoteHandlerImpl();

        UnicastRemoteObject.exportObject(handler, 0);

        registerAuditOfficerThread(service,  clientArguments.getParty(), clientArguments.getTableID(), handler);

    }

    private static void registerAuditOfficerThread(AuditService service, Party party, int table, PartyVoteHandler handler){
        Runnable r = () -> {
            try {
                service.registerAuditOfficer(party, table, handler);
            } catch (RemoteException e) {
                LOG.error("Error registering the audit officer");
            } catch (InvalidElectionStateException e) {
                LOG.error(e.getMessage());
            }
            System.out.format("Audit officer of %s registered on polling place %s\n", party.toString(), table);
        };
        Thread t = new Thread(r);
        t.start();
    }
}

