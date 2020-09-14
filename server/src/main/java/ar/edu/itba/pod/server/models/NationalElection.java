package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.comparators.DoubleComparator;
import ar.edu.itba.pod.comparators.LongComparator;
import ar.edu.itba.pod.models.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class NationalElection implements Serializable {
    private static final long serialVersionUID = 3779368141238758571L;
    /**
     * List to hold all the votes in order to be able to perform the automatic runoff
     */
    private final List<Map<Party, Long>> ballots = new ArrayList<>();

    // Comparators
    private final DoubleComparator doubleComparator = new DoubleComparator();
    private final LongComparator longComparator = new LongComparator();

    // Sorted results
    private TreeSet<MutablePair<Party, Long>> sortedScoringResults = new TreeSet<>(longComparator);
    private TreeSet<MutablePair<Party, Double>> sortedRunoffResults = new TreeSet<>(doubleComparator);
    private Party winner = null;

    public NationalElection() { }

    public void emitVote(Map<Party, Long> vote) {
        synchronized (this.ballots) {
            this.ballots.add(vote);
        }
    }

    /**
     * Computes the NationalElection Results
     * Is called inside the lock when closing the elections
     * Will only be called ONCE and hence do not need to be synchronized
     */
    public void computeNationalElectionResults() {
        if (this.ballots.size() > 0) {
            List<Party> scoringRoundWinners = this.scoringRound();
            winner = this.automaticRunoff(scoringRoundWinners);
        }
    }

    /**
     * Runs the scoring round considering the ballots parameter. Calculates for each party the score
     * by summing up the vote value (0-5) of said party of all ballots.
     * @return List of top two candidates
     */
    private List<Party> scoringRound() {
        final Map<Party, Long> scoringRoundResults = new HashMap<>();
        long newScore;

        for (Map<Party, Long> ballot: this.ballots) {
            for (Map.Entry<Party, Long> entry: ballot.entrySet()) {
                newScore = scoringRoundResults.computeIfAbsent(entry.getKey(), k -> 0L) + entry.getValue();
                // Saving the current score of the party
                scoringRoundResults.put(entry.getKey(), newScore);
            }
        }

        this.sortedScoringResults = new TreeSet<>(longComparator);
        this.sortedScoringResults.addAll(scoringRoundResults.entrySet().stream().map(e -> new MutablePair<>(e.getKey(), e.getValue())).collect(Collectors.toList()));

        // Sorting the candidates and obtaining the two top ones
        return this.sortedScoringResults.stream().limit(2).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Runs the automatic runoff considering the ballots parameter and the winners of the scorring round
     * A party adds points by having the larger vote value on a ballot
     * @return Winning party of automatic runoffs
     */
    private Party automaticRunoff(final List<Party> winners) {
        final Map<Party, Double> automaticRunoffResult = new HashMap<>();

        /*if (winners.size() != 2) {
            System.out.println("Must be two winners from the scoring round");
            return null;
        }*/
        // 1. Filter ballots whose winning candidate score is equal to 0
        List<Map<Party, Long>> filteredBallots = this.ballots.stream()
                .filter(b -> validBallotForRunoff(b, winners))
                .collect(Collectors.toList());

        // 2. For each ballot, check which candidate has the top score
        Map<Party, Long> runoffResults = filteredBallots.stream()
                .map(b -> winnerOfBallot(b, winners))
                .collect(Collectors.groupingBy(p -> p, Collectors.summingLong(p -> 1)));

        // 3. Calculate the percentages for each party
        double totalScore = runoffResults.values().stream().mapToLong(v -> v).sum();
        runoffResults.forEach((party, score) -> {
            automaticRunoffResult.put(party, (double) score / totalScore);
        });

        // 4. Sort the results and get the first one
        this.sortedRunoffResults = new TreeSet<>(doubleComparator);
        this.sortedRunoffResults.addAll(automaticRunoffResult.entrySet().stream().map(e -> new MutablePair<>(e.getKey(), e.getValue())).collect(Collectors.toList()));
        return this.sortedRunoffResults.first().getKey();
    }

    /**
     * Given a ballot and a list of winners of the scoring round, returns whether the ballot is valid
     * to be considered for the automatic runoffs. Invalid ballots have vote = 0 for all the winning candidates.
     * @return boolean
     */
    private boolean validBallotForRunoff(Map<Party, Long> ballot, List<Party> winners) {
        if (winners == null || winners.isEmpty()) return false;

        boolean isValid = false;
        for (Party candidate: winners) {
            isValid = isValid || ballot.computeIfAbsent(candidate, c -> 0L) != 0;
        }
        return isValid;
    }

    /**
     * Given a ballot and a list of winners from the scoring round, returns the party that had the
     * largest vote value in that ballot. If it is a tie, the smallest alphanumeric party is chosen.
     * @return Winning party of ballot
     */
    private Party winnerOfBallot(Map<Party, Long> ballot, List<Party> winners) {
        Party winner = null;
        long maxScore = -1;
        
        for (Party candidate: winners) {
            long score = ballot.computeIfAbsent(candidate, c -> 0L);
            // If both have the same score, winner will be the smallest alphanumeric party
            if (winner != null && score == maxScore) {
                winner = candidate.getDescription().compareTo(winner.getDescription()) < 0 ? candidate : winner;
            }
            else if (score > maxScore) {
                maxScore = score;
                winner = candidate;
            }
        }
        return winner;
    }

    /* Methods from below will only be called once the elections are closed and therefore, the national
     * results already calculated. Since threads will only be reading, there is no need to synchronize
     */

    /**
     * Returns the national election winner.
     * @return The party winner of the elections
     */
    public Party getNationalElectionWinner() {
        return winner;
    }

    /**
     * Once elections has been run, this method orders the results of the scoring round
     * @return List of map entries of the scoring round results
     */
    public TreeSet<MutablePair<Party, Long>> getSortedScoringRoundResults() {
        return this.sortedScoringResults;
    }
    
    /**
     * Once elections has been run, this method orders the results of the automatic runoff
     * @return List of map entries of the runoff results
     */
    public TreeSet<MutablePair<Party, Double>> getSortedAutomaticRunoffResults() {
        return this.sortedRunoffResults;
    }
}
