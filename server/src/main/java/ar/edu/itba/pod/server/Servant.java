package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;

import java.rmi.RemoteException;
import java.util.*;

import ar.edu.itba.pod.server.Callables.FptpBatch;
import ar.edu.itba.pod.server.Callables.SpavBatch;
import ar.edu.itba.pod.server.Callables.StarBatch;
import jdk.internal.util.xml.impl.Pair;

import java.util.concurrent.*;

public class Servant implements AuditService, ManagementService, VoteService, QueryService {
    private final Map<Party, Map<Integer, List<PartyVoteHandler>>> auditHandlers = new HashMap<>();
    private final HashMap<Integer, Table> tables = new HashMap<>();
    private StateElection stateElection = new StateElection();
    private NationalElection nationalElection = new NationalElection();
    private ElectionState electionState = ElectionState.PENDING;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final String STATE_LOCK = "ELECTION_STATE_LOCK";

    @Override
    public void registerAuditOfficer(Party party, int table, PartyVoteHandler handler) throws RemoteException {
        synchronized (this.STATE_LOCK) {
            // If election is still pending, it can be registered
            if (this.electionState == ElectionState.PENDING) {
                // Creates the table if it does not exist yet
                synchronized (tables) {
                    tables.computeIfAbsent(table, t -> new Table(table));
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

    private void notifyPartyVote(Vote vote) throws RemoteException {
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
    
    public int emitVotes(List<Vote> votes) throws RemoteException, ExecutionException, InterruptedException {
        // independently, we process votes on three dimensions needed
        Future<Integer> fptpResult = executor.submit(new FptpBatch(votes, tables));
        Future<Integer> starResult = executor.submit(new StarBatch(votes, nationalElection));
        Future<Integer> spavResult = executor.submit(new SpavBatch(votes, stateElection));

        fptpResult.get();
        // once all fptp results have been processed we can safely notify audit officers at their table
        executor.submit(() -> {for(Vote v: votes) notifyPartyVote(v); return "Finished";});

        spavResult.get();
        starResult.get();

        // finished processing all dimensions
        return votes.size();
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
