package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.comparators.DoubleComparator;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.stream.Collectors;

public class StateElection {
    /**
     * Each province holds a list of the list of candidate parties
     */
    private final Map<Province, List<List<Party>>> ballots = new HashMap<>();
    private Map<Province, List<Map<Party, Double>>> results = new HashMap<>();
    // TODO: cambiar luego de preguntar que pasa si no alcanzan ganadores por provincia
    private Map<Province, List<Party>> winnersPerProvince = new HashMap<>();
    private List<Party> winners = new ArrayList<>();

    // Comparator
    private static final DoubleComparator doubleComparator = new DoubleComparator();

    public StateElection() {
        Arrays.stream(Province.values()).forEach(p -> ballots.put(p, new ArrayList<>()));
    }

    /**
     * Given a SPAV vote in the format of List of parties, and the corresponding province, store the vote
     * @param province Province for the vote
     * @param vote List of chosen candidate parties
     */
    public void emitVote(Province province, List<Party> vote) {
        synchronized (this.ballots) {
            this.ballots.get(province).add(vote);
        }
    }

    /**
     * Once elections are closed, computes all results
     */
    public void computeStateElectionResults() {
        for(Province p : Province.values()) computeResultsForProvince(p);
    }

    /**
     * Given a province, computes three rounds.
     * @param province Province for results
     */
    private void computeResultsForProvince(Province province){
        // Three rounds will be stored here
        List<Map<Party, Double>> rounds = new ArrayList<>();
        // There are no winners at this point
        winners.clear();
        for(int i=0; i<3; i++) {
            // 1. Computes round results
            rounds.add(computeRound(province));
            // 2. Computes winner for the round
            computeWinner(rounds.get(i));
        }
        // 3. Adds winners per province
        winnersPerProvince.put(province, new ArrayList<>(winners));
        // Final results
        results.put(province, rounds);
    }

    /**
     * Given a province, computes a specific round.
     * @param province Province for round
     */
    private Map<Party, Double> computeRound(Province province) {
        // 1. First we get ballots for the intended province
        List<List<Party>> provinceBallots = ballots.get(province);
        Map<Party, Double> round = new HashMap<>();
        // 2. For every party, we search for approved ballots, then we get real approval value, and sum all together
        for(Party p : Party.values()){
            // we don't compute results for winners from previous rounds!
            if(!winners.contains(p)) {
                round.put(p, provinceBallots.stream()
                        .filter(b -> b.contains(p))
                        .mapToDouble(b -> 1/(1+winnersInBallot(b)))
                        .sum());
            }
        }
        // 3. New round is completed
        return round;
    }

    /**
     * Given a round, computes the winner of that round.
     * @param round to compute a winner
     */
    private void computeWinner(Map<Party, Double> round) {
        round.entrySet().stream().map(e -> new MutablePair<>(e.getKey(), e.getValue()))
                // 1. Filters winners from previous rounds
                .filter(e -> !winners.contains(e.getKey()))
                // 2. From remaining, finds the winner
                .min(doubleComparator)
                // 3. Adds new winner for a specific round
                .map(Map.Entry::getKey).ifPresent(p -> winners.add(p));
    }

    /**
     * Given a ballot, returns how many winners are on that ballot.
     * @param ballot
     * @return How many winners are on that ballot
     */
    private double winnersInBallot(List<Party> ballot) {
        return ballot.stream().filter(p -> winners.contains(p)).count();
    }

    /* Methods from below will only be called once the elections are closed and therefore, state results
     * already calculated. Since threads will only be reading, there is no need to synchronize.
     */

    /**
     * Returns how many votes were processed
     * @return long representing votes quantity
     */
    public long getVotesQuantity() {
        return ballots.values().stream().mapToLong(v -> v.size()).sum();
    }

    /**
     * Given province and round, returns the party that won that round
     * @param province Province to find winner
     * @param round where winner was elected
     * @return Winner Party for the round in that province
     */
    public Party getWinner(Province province, Round round) {
        return winnersPerProvince.get(province).get(round.getValue());
    }

    /**
     * Given province and round, returns results
     * @param province Province to get results
     * @param round for those results
     * @return Map containing results per Party
     * ! If Double is null, it means it was an elected Party in the previous round. Not the same as having 0.0 approvals
     */
    public Map<Party, Double> getResultsRound(Province province, Round round) {
        return results.get(province).get(round.getValue());
    }

    /**
     * Given province, returns all winners for state election
     * @param province Province to get winners
     * @return List containing three winners, one per round
     */
    public Party[] getWinners(Province province) {
        return winnersPerProvince.get(province).toArray(new Party[]{});
    }

    /**
     * Given province and round, returns sorted treeSet with results
     * @param province Province for results
     * @param round Round for results
     * @return TreeSet containing Map entries with party and approval rate
     */
    private TreeSet<MutablePair<Party, Double>> getNthRound(Province province, Round round) {
        TreeSet<MutablePair<Party, Double>> orderedSet = new TreeSet<>(doubleComparator);
        if(!results.get(province).get(round.getValue()).isEmpty()) {
            orderedSet.addAll(results.get(province).get(round.getValue()).entrySet().stream().map(e -> new MutablePair<>(e.getKey(), e.getValue())).collect(Collectors.toList()));
        }
        return orderedSet;
    }

    public TreeSet<MutablePair<Party, Double>> getFirstRound(Province province) {
        return getNthRound(province, Round.FIRST);
    }

    public TreeSet<MutablePair<Party, Double>> getSecondRound(Province province) {
        return getNthRound(province, Round.SECOND);
    }

    public TreeSet<MutablePair<Party, Double>> getThirdRound(Province province) {
        return getNthRound(province, Round.THIRD);
    }
}
