package ar.edu.itba.pod.models;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.TreeSet;

public class FPTPResult extends ElectionResults {
    private static final long serialVersionUID = -2379609868942776490L;

    private TreeSet<MutablePair<Party, Double>> fptpResults;
    private ElectionState electionState;

    public FPTPResult (TreeSet<MutablePair<Party, Double>> fptpResults, ElectionState electionState) {
        this.fptpResults = fptpResults;
        this.votingType = VotingType.FPTP;
        this.electionState = electionState;
    }

    public TreeSet<MutablePair<Party, Double>> getFptpResults() {
        return fptpResults;
    }

    public Party getWinner() {
        return fptpResults.first().getKey();
    }

    public ElectionState getElectionState() {
        return electionState;
    }
}
