package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servant implements AuditService {
    private final Map<Party, Map<Integer, List<String>>> auditOfficers = new HashMap<>();
    private final Map<Party, Map<Integer, PartyVoteHandler>> auditHandlers = new HashMap<>();

    @Override
    public void registerAuditOfficer(String officer, Party party, Integer table, PartyVoteHandler handler) throws RemoteException {
        // TODO -> use administrationService to check if elections have begun
        if (true) {
            // Registering the audit officer
            synchronized (auditOfficers) {
                auditOfficers.computeIfAbsent(party, p -> new HashMap<>())
                        .computeIfAbsent(table, t -> new ArrayList<>())
                        .add(officer);
            }

            // Saving the vote handler to notify when new votes on a table for a certain party happen
            synchronized (auditHandlers) {
                auditHandlers.computeIfAbsent(party, p -> new HashMap<>()).put(table, handler);
            }
        } else {
            throw new ElectionsInProgressException();
        }
    }

    @Override
    public void notifyPartyVote(Vote vote) throws RemoteException {
        Party party = vote.getParty();
        Integer table = vote.getTable();

        synchronized (auditHandlers) {
            if (auditHandlers.containsKey(party) && auditHandlers.get(party).containsKey(table)) {
                auditHandlers.get(party).get(table).onPartyVote(vote);
            }
        }

    }
}
