package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.*;
import ar.edu.itba.pod.server.models.NationalElection;
import ar.edu.itba.pod.server.models.StateElection;
import ar.edu.itba.pod.server.models.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Servant implements AuditService, ManagementService, VoteService, QueryService {
    private static final Logger LOG = LoggerFactory.getLogger(Servant.class);

    private final Map<Party, Map<Integer, List<PartyVoteHandler>>> auditHandlers = new HashMap<>();
    private final HashMap<Integer, Table> tables = new HashMap<>();
    private final StateElection stateElection = new StateElection();
    private final NationalElection nationalElection = new NationalElection();

    /**
     * Variable to hold the state of the election
     */
    private ElectionState electionState = ElectionState.PENDING;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final String STATE_LOCK = "ELECTION_STATE_LOCK";

    // Will compare first with percentage and then the party
    private final static Comparator<Map.Entry<Party, Double>> fptpComparator = (e1, e2) -> {
        int valueComparison = e2.getValue().compareTo(e1.getValue());
        if (valueComparison == 0) {
            return e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
        }
        return valueComparison;
    };

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                      AUDIT METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void registerAuditOfficer(Party party, int table, PartyVoteHandler handler) throws RemoteException, InvalidElectionStateException {
        synchronized (this.STATE_LOCK) {
            // If election is still pending, it can be registered
            if (this.electionState == ElectionState.PENDING) {
                // Saving the vote handler to notify when new votes on a table for a certain party happen
                synchronized (auditHandlers) {
                    auditHandlers.computeIfAbsent(party, p -> new HashMap<>())
                            .computeIfAbsent(table, t-> new ArrayList<>())
                            .add(handler);
                }
            } else {
                throw new InvalidElectionStateException("Elections in progress. Can no longer register an audit officer");
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

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                  MANAGEMENT METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

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
            this.nationalElection.computeNationalElectionResults();
            this.stateElection.computeStateElectionResults();
            this.electionState = ElectionState.CLOSED;
        }
    }

    @Override
    public ElectionState getElectionState() throws RemoteException {
        synchronized (this.STATE_LOCK){
            return this.electionState;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                      VOTE METHODS
    //////////////////////////////////////////////////////////////////////////////////////////
    
    public void emitVote(Vote vote) throws RemoteException, ExecutionException, InterruptedException, InvalidElectionStateException {
        // Synchronize the access to the election state
        System.out.println("emitVote executed");
        synchronized (this.STATE_LOCK){
            if (this.electionState != ElectionState.OPEN){
                throw new InvalidElectionStateException("Elections haven't started or have already finished");
            }
        }
        System.out.println("1");

        // Synchronize access to see if the key exists, perform the emission out of synchronized block
        synchronized (this.tables){
            if (!this.tables.containsKey(vote.getTable())){
                this.tables.put(vote.getTable(), new Table(vote.getTable(),vote.getProvince()));
            }
        }
        System.out.println("2");

        // Emit the vote for the table
        this.tables.get(vote.getTable()).emitVote(vote.getFptpVote());

        // Processing the SPAV vote for the state election
        this.stateElection.emitVote(vote.getProvince(), vote.getSpavVote());

        // Processing the STAR vote for the national election
        this.nationalElection.emitVote(vote.getStarVote());

        // Notify the vote
        this.notifyPartyVote(vote);
        System.out.println("3");
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                      QUERY METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ElectionResults getNationalResults() throws RemoteException, InvalidElectionStateException {
        ElectionState electionState;
        // In order to avoid locking the whole if blocks, I pass the value of the election to a local variable
        synchronized (this.STATE_LOCK) {
            electionState = this.electionState;
        }

        if(electionState == ElectionState.OPEN) {
            return this.getAllTableResults();

        } else if(electionState == ElectionState.CLOSED){
            System.out.println("Closed elections");
            Party winner = nationalElection.getNationalElectionWinner();
            System.out.println(winner.getDescription());
            NationalElectionsResult nationalResults = new NationalElectionsResult(
                    nationalElection.getOrderedScoringRoundResults(),
                    nationalElection.getOrderedAutomaticRunoffResults(),
                    winner
            );
            System.out.println("creating election results");
            ElectionResults electionResults = new ElectionResults(nationalResults);
            System.out.println("Returning election results");
            return electionResults;
        }

        // Elections have not began
        throw new InvalidElectionStateException("Elections PENDING. Can not request national results");
    }

    @Override
    public ElectionResults getProvinceResults(Province province) throws RemoteException, InvalidElectionStateException {
        ElectionState electionState;

        // In order to avoid locking the whole if blocks, I pass the value of the election to a local variable
        synchronized (this.STATE_LOCK) {
            electionState = ElectionState.fromValue(this.electionState.getDescription());
        }

        if(electionState == ElectionState.OPEN) {
            return this.getProvinceTableResults(province);
        }
        else if(electionState == ElectionState.CLOSED){
            StateElectionsResult stateResults = new StateElectionsResult(province,
                    stateElection.getFirstRound(province),
                    stateElection.getSecondRound(province),
                    stateElection.getThirdRound(province),
                    stateElection.getWinners(province));
            return new ElectionResults(stateResults);
        };

        throw new InvalidElectionStateException("Elections PENDING. Can not request state results");
    }

    @Override
    public ElectionResults getTableResults(Integer tableID) throws RemoteException, InvalidElectionStateException {
        ElectionState electionState;

        // In order to avoid locking the whole if blocks, I pass the value of the election to a local variable
        synchronized (this.STATE_LOCK) {
            electionState = ElectionState.fromValue(this.electionState.getDescription());
        }

        if(electionState != ElectionState.PENDING){
            return new ElectionResults(tables.get(tableID).getResultsFromTable());
        }
        throw new InvalidElectionStateException("Elections PENDING. Can not request FPTP results");
    }

    // Will only be called when getNationalResults is called and elections are still open
    @Override
    public ElectionResults getAllTableResults() throws RemoteException {
        Map<Party, Long> fptpVotes;
        synchronized (this.tables) {
            fptpVotes = this.tables.values().stream()
                    .flatMap(t -> t.getVotes().entrySet().stream())
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(e -> e.getValue().get())));
        }
        return newElectionResults(fptpVotes);
    }

    // Will only be called when getProvinceResults is called and elections are still open
    @Override
    public ElectionResults getProvinceTableResults(Province province) throws RemoteException {
        Map<Party, Long> fptpVotes;
        synchronized (this.tables) {
            fptpVotes = this.tables.values().stream()
                    .filter(t -> t.getProvince().equals(province))
                    .flatMap(t -> t.getVotes().entrySet().stream())
                    .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(e -> e.getValue().get())));
        }
        return newElectionResults(fptpVotes);
    }

    private ElectionResults newElectionResults(Map<Party, Long> fptpVotes) {
        double totalVotes = (double) fptpVotes.values().stream().reduce(0L, Long::sum);

        TreeSet<Map.Entry<Party, Double>> fptpResult = new TreeSet<>(fptpComparator);
        fptpVotes.forEach((key, value) -> fptpResult.add(new AbstractMap.SimpleEntry<>(key, (double) value / totalVotes)));

        return new ElectionResults(fptpResult);
    }
}
