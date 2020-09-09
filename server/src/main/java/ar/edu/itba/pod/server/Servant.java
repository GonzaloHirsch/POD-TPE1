package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Servant implements AuditService, ManagementService, QueryService {
    private final Map<Party, Map<Integer, List<String>>> auditOfficers = new HashMap<>();
    private final Map<Party, Map<Integer, List<PartyVoteHandler>>> auditHandlers = new HashMap<>();
    private ElectionState electionState = ElectionState.PENDING;

    private final String STATE_LOCK = "ELECTION_STATE_LOCK";
    private NationalElection nationalElection = new NationalElection();

    @Override
    public void registerAuditOfficer(String officer, Party party, Integer table, PartyVoteHandler handler) throws RemoteException {
        synchronized (this.STATE_LOCK) {
            // If election is still pending, it can be registered
            if (this.electionState == ElectionState.PENDING) {
                // Registering the audit officer
                synchronized (auditOfficers) {
                    auditOfficers.computeIfAbsent(party, p -> new HashMap<>())
                            .computeIfAbsent(table, t -> new ArrayList<>())
                            .add(officer);
                }

                // Saving the vote handler to notify when new votes on a table for a certain party happen
                synchronized (auditHandlers) {
                    auditHandlers.computeIfAbsent(party, p -> new HashMap<>())
                            .computeIfAbsent(table, t-> new ArrayList<>())
                            .add(handler);
                }
            } else {
                throw new ElectionsInProgressException();
            }
        }
    }


    @Override
    public void notifyPartyVote(Vote vote) throws RemoteException {
        Party party = vote.getFptpVote();
        Integer table = vote.getTable();

        synchronized (auditHandlers) {
            if (auditHandlers.containsKey(party) && auditHandlers.get(party).containsKey(table)) {
                for (PartyVoteHandler handler: auditHandlers.get(party).get(table)) {
                    handler.onPartyVote(vote);
                }
            }
        }

    }

    @Override
    public void openElection() throws RemoteException, InvalidElectionStateException {
        synchronized (this.STATE_LOCK){
            if (this.electionState != ElectionState.PENDING){
                throw new InvalidElectionStateException("Elections have already started/finished");
            }
            this.electionState = ElectionState.OPEN;
        }
    }

    @Override
    public void closeElection() throws RemoteException, InvalidElectionStateException {
        synchronized (this.STATE_LOCK){
            if (this.electionState != ElectionState.OPEN){
                throw new InvalidElectionStateException("Elections haven't started or have already finished");
            }
            this.electionState = ElectionState.CLOSED;
        }
    }

    @Override
    public ElectionState getElectionState() throws RemoteException {
        synchronized (this.STATE_LOCK){
            return this.electionState;
        }
    }

    @Override
    public List<Map.Entry<Party,Long>> getNationalResults() throws RemoteException {
        synchronized (this.STATE_LOCK) {
            if(electionState==ElectionState.OPEN){

            }else if(electionState==ElectionState.CLOSED){

            }
        }
        return null;
    }

    @Override
    public List<Map.Entry<Party,Long>> getProvinceResults(Province province) throws RemoteException {
        synchronized (this.STATE_LOCK) {
            if(electionState==ElectionState.OPEN){

            }else if(electionState==ElectionState.CLOSED){
                nationalElection.getNationalElectionWinner();
                return nationalElection.getOrderedScoringRoundResults();
            }
        }
        return null;
    }

    @Override
    public List<Map.Entry<Party,Long>> getTableResults(Integer tableID) throws RemoteException {
        synchronized (this.STATE_LOCK) {
            if(electionState == ElectionState.OPEN || electionState == ElectionState.CLOSED){

            }
        }
        return null;
    }
}
