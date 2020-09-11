package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.Party;

import java.util.*;
import java.util.stream.Collectors;

public class NationalElection {
    /**
     * List to hold all the votes in order to be able to perform the automatic runoff
     */
    private final List<Map<Party, Long>> ballots = new ArrayList<>();
    private final Map<Party, Long> scoringRoundResults = new HashMap<>();
    private final Map<Party, Double> automaticRunoffResult = new HashMap<>();

    public NationalElection() { }

    public void emitVote(Map<Party, Long> vote) {
        synchronized (this.ballots) {
            this.ballots.add(vote);
        }
    }

    /**
     * Runs the national elections to calculate results.
     * @return The party winner of the elections
     */
    public Party getNationalElectionWinner() {
        List<Party> scoringRoundWinners = this.scoringRound();
        return this.automaticRunoff(scoringRoundWinners);
    }

    /**
     * Once elections has been run, this method orders the results of the scoring round
     * @return List of map entries of the scoring round results
     */
    public TreeSet<Map.Entry<Party, Long>> getOrderedScoringRoundResults() {
        if (this.scoringRoundResults.isEmpty()) return new TreeSet<>();

        Comparator<Map.Entry<Party, Long>> scoringComparator = (e1, e2) -> {
            int keyComparison = e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
            if (keyComparison == 0) {
                return e1.getValue().compareTo(e2.getValue());
            }
            return keyComparison;
        };

        TreeSet<Map.Entry<Party, Long>> orderedSet = new TreeSet<>(scoringComparator);
        orderedSet.addAll(this.scoringRoundResults.entrySet());
        return orderedSet;
    }
    /**
     * Once elections has been run, this method orders the results of the automatic runoff
     * @return List of map entries of the runoff results
     */
    public TreeSet<Map.Entry<Party, Double>> getOrderedAutomaticRunoffResults() {
        if (this.automaticRunoffResult.isEmpty()) return new TreeSet<>();

        Comparator<Map.Entry<Party, Double>> runoffComparator = (e1, e2) -> {
            int keyComparison = e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
            if (keyComparison == 0) {
                return e1.getValue().compareTo(e2.getValue());
            }
            return keyComparison;
        };

        TreeSet<Map.Entry<Party, Double>> orderedSet = new TreeSet<>(runoffComparator);
        orderedSet.addAll(this.automaticRunoffResult.entrySet());
        return orderedSet;
    }

    /**
     * Runs the scoring round considering the ballots parameter. Calculates for each party the score
     * by summing up the vote value (0-5) of said party of all ballots.
     * @return List of top two candidates
     */
    public List<Party> scoringRound() {
        long newScore;
        long maxScore = 0;
        long nextMaxScore = 0;

        for (Map<Party, Long> ballot: this.ballots) {
            for (Map.Entry<Party, Long> entry: ballot.entrySet()) {
                newScore = this.scoringRoundResults.computeIfAbsent(entry.getKey(), k -> 0L) + entry.getValue();

                // Saving the two top scores
                if (newScore > maxScore) {
                    nextMaxScore = maxScore;
                    maxScore = newScore;
                }

                // Saving the current score of the party
                this.scoringRoundResults.put(entry.getKey(), newScore);
            }
        }
        // Retrieve the candidates that have the maximum vote
        List<Party> winningCandidates = this.retrievePartyWithScore(this.scoringRoundResults, maxScore, 2);
        if (winningCandidates.size() > 1) {
            return winningCandidates;
        }

        // Retrieving the second winner from the list of candidates with nextMaxScore
        winningCandidates.addAll(this.retrievePartyWithScore(this.scoringRoundResults, nextMaxScore, 1));
        return winningCandidates;
    }

    /**
     * Runs the automatic runoff considering the ballots parameter and the winners of the scorring round
     * A party adds points by having the larger vote value on a ballot
     * @return Winning party of automatic runoffs
     */
    public Party automaticRunoff(final List<Party> winners) {

        if (winners.size() != 2) {
            System.out.println("Must be two winners from the scoring round");
            return null;
        }
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
            this.automaticRunoffResult.put(party, (double) score / totalScore);
        });

        // 4. If candidates have the same score, the smallest alphanumeric one wins
        long maxScore = Collections.max(runoffResults.entrySet(), Comparator.comparingLong(Map.Entry::getValue)).getValue();
        return retrievePartyWithScore(runoffResults, maxScore, 1).get(0);
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

    /**
     * Given the scores of each party, returns the limit amount of candidates that have the specified score
     * @return Winning party
     */
    private List<Party> retrievePartyWithScore(Map<Party, Long> partyScore, final long score, final int limit) {
        List<Party> matchingCandidates = partyScore.entrySet().stream().filter(e -> e.getValue() == score).map(Map.Entry::getKey).collect(Collectors.toList());

        if (matchingCandidates.size() > 1) {
            matchingCandidates = matchingCandidates.stream().sorted(Comparator.comparing(Party::getDescription)).limit(limit).collect(Collectors.toList());
        }
        return matchingCandidates;
    }
}
