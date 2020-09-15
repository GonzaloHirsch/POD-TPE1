package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.AuditClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.PartyVoteHandler;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuditClient {
    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException, InvalidElectionStateException {
        try {
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

            registerAuditOfficerThread(service, clientArguments.getParty(), clientArguments.getTableID(), handler);
        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me) {
            System.out.println("ERROR: Malformed URL");
        }
    }

    private static void registerAuditOfficerThread(AuditService service, Party party, int table, PartyVoteHandler handler) {
        try {
            service.registerAuditOfficer(party, table, handler);
            System.out.format("Audit officer of %s registered on polling place %s\n", party.toString(), table);
        } catch (RemoteException e) {
            System.out.println("ERROR: Remote Exception. Error registering the audit officer");
        } catch (InvalidElectionStateException e) {
            System.out.println("Elections are OPEN. Can no longer register an audit officer");
        }
    }
}

