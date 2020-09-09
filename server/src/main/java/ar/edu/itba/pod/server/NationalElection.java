package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Party;

import java.util.*;
import java.util.stream.Collectors;

public class NationalElection {
    private List<Map<Party, Long>> ballots;
    private Map<Party, Long> scoringResults = new HashMap<>();

    NationalElection(List<Map<Party, Long>> ballots) {
        this.ballots = ballots;
    }

    public List<Party> scoringRound() {
        long newScore;
        long maxScore = 0;
        long nextMaxScore = 0;

        for (Map<Party, Long> ballot: this.ballots) {
            for (Map.Entry<Party, Long> entry: ballot.entrySet()) {
                newScore = this.scoringResults.computeIfAbsent(entry.getKey(), k -> 0L) + entry.getValue();

                // Saving the two top scores
                if (newScore > maxScore) {
                    nextMaxScore = maxScore;
                    maxScore = newScore;
                }

                this.scoringResults.put(entry.getKey(), newScore);
            }
        }
        // Retrieve the candidates that have the maximum vote
        List<Party> winningCandidates = this.retrievePartyWithScore(maxScore, 2);
        if (winningCandidates.size() > 1) {
            return winningCandidates;
        }

        // Retrieving the second winner from the list of candidates with nextMaxScore
        winningCandidates.addAll(this.retrievePartyWithScore(nextMaxScore, 1));
        return winningCandidates;
    }

    private List<Party> retrievePartyWithScore(final long score, final int limit) {
        List<Party> matchingCandidates = this.scoringResults.entrySet().stream().filter(e -> e.getValue() == score).map(Map.Entry::getKey).collect(Collectors.toList());

        if (matchingCandidates.size() > 1) {
            matchingCandidates = matchingCandidates.stream().sorted(Comparator.comparing(Party::getDescription)).limit(limit).collect(Collectors.toList());
        }
        return matchingCandidates;
    }
}
