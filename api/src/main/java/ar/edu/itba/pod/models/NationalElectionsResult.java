package ar.edu.itba.pod.models;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Map;
import java.util.TreeSet;

public class NationalElectionsResult extends ElectionResults {
    private static final long serialVersionUID = 3439625826120089417L;
    private TreeSet<MutablePair<Party, Long>> scoringRoundResults;
    private TreeSet<MutablePair<Party, Double>> automaticRunoffResults;
    private Party winner;

    public NationalElectionsResult(TreeSet<MutablePair<Party, Long>> scoring, TreeSet<MutablePair<Party, Double>> runoff, Party winner) {
        this.scoringRoundResults = scoring;
        this.automaticRunoffResults = runoff;
        this.winner = winner;
        this.votingType = VotingType.NATIONAL;
    }

    public TreeSet<MutablePair<Party, Long>> getScoringRoundResults() {
        return scoringRoundResults;
    }

    public TreeSet<MutablePair<Party, Double>> getAutomaticRunoffResults() {
        return automaticRunoffResults;
    }

    public Party getWinner() {
        return winner;
    }

}
