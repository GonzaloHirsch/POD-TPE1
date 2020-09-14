package ar.edu.itba.pod.models;

import java.util.Map;
import java.util.TreeSet;

public class NationalElectionsResult extends ElectionResults {
    private static final long serialVersionUID = 3439625826120089417L;

    private TreeSet<Map.Entry<Party, Long>> scoringRoundResults;
    private TreeSet<Map.Entry<Party, Double>> automaticRunoffResults;
    private Party winner;

    public NationalElectionsResult(TreeSet<Map.Entry<Party, Long>> scoring, TreeSet<Map.Entry<Party, Double>> runoff, Party winner) {
        this.scoringRoundResults = scoring;
        this.automaticRunoffResults = runoff;
        this.winner = winner;
        this.votingType = VotingType.NATIONAL;
    }

    public TreeSet<Map.Entry<Party, Long>> getScoringRoundResults() {
        return scoringRoundResults;
    }

    public TreeSet<Map.Entry<Party, Double>> getAutomaticRunoffResults() {
        return automaticRunoffResults;
    }

    public Party getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return this.scoringRoundResults.toString() + "\n" + this.automaticRunoffResults.toString();
    }
}
