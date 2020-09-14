package ar.edu.itba.pod.models;

import java.util.Map;
import java.util.TreeSet;

public class FPTPResult extends ElectionResults {
    private static final long serialVersionUID = -2379609868942776490L;

    private TreeSet<Map.Entry<Party, Double>> fptpResults;

    public FPTPResult (TreeSet<Map.Entry<Party, Double>> fptpResults) {
        this.fptpResults = fptpResults;
        this.votingType = VotingType.FPTP;
    }

    public TreeSet<Map.Entry<Party, Double>> getFptpResults() {
        return fptpResults;
    }

    public Party getWinner() {
        return fptpResults.first().getKey();
    }

    @Override
    public String toString() {
        return this.fptpResults.toString();
    }
}
