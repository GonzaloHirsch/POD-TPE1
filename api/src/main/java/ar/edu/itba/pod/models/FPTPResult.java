package ar.edu.itba.pod.models;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Map;
import java.util.TreeSet;

public class FPTPResult extends ElectionResults {
    private static final long serialVersionUID = -2379609868942776490L;

    private TreeSet<MutablePair<Party, Double>> fptpResults;

    public FPTPResult (TreeSet<MutablePair<Party, Double>> fptpResults) {
        this.fptpResults = fptpResults;
        this.votingType = VotingType.FPTP;
    }

    public TreeSet<MutablePair<Party, Double>> getFptpResults() {
        return fptpResults;
    }

    public Party getWinner() {
        return fptpResults.first().getKey();
    }
}
