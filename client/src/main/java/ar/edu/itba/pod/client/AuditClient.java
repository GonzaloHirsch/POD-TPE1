package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuditClient {
    private static final Logger LOG = LoggerFactory.getLogger(AuditClient.class);

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        LOG.info("AuditClient has started...");
        final AuditService service = (AuditService) Naming.lookup("//127.0.0.1:1099/auditService");

        PartyVoteHandler handler = new PartyVoteHandlerImpl();

        UnicastRemoteObject.exportObject(handler, 0);

        String officer = "Madre Teresa";
        Party party = Party.BUFFALO;
        Integer table = 1004;
        registerAuditOfficerThread(service, officer, party, table, handler);
    }

    public static void registerAuditOfficerThread(AuditService service, String officer, Party party, Integer table, PartyVoteHandler handler){
        Runnable r = () -> {
            try {
                service.registerAuditOfficer(officer, party, table, handler);
            } catch (RemoteException e) {
                LOG.error("Error registering the audit officer");
            } catch (ElectionsInProgressException e) {
                LOG.error("Elections in progress. Can no longer register audit officers");
            }
            LOG.info("Fiscal of {} registered on polling place {}", party.toString(), table); // TODO LOG OR PRINTF?

            try {
                service.notifyPartyVote(new Vote(party, table));
            } catch (RemoteException e) {
                LOG.error("Error notifying new vote");
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
}

