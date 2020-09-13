package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;

public class NationalElectionsResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private TreeSet<Map.Entry<Party, Long>> scoringRoundResults;
    private TreeSet<Map.Entry<Party, Double>> automaticRunoffResults;
    private Party winner;

    public NationalElectionsResult(TreeSet<Map.Entry<Party, Long>> scoring, TreeSet<Map.Entry<Party, Double>> runoff, Party winner) {
        this.scoringRoundResults = scoring;
        this.automaticRunoffResults = runoff;
        this.winner = winner;
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
}
