package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeSet;

public class ElectionResults implements Serializable {
    private VotingType votingType;
    private NationalElectionsResult nationalElectionsResult = null;
    private StateElectionsResult stateElectionsResult = null;
    private TreeSet<Map.Entry<Party,Double>> fptpResult = null;

    public ElectionResults(NationalElectionsResult nationalResult) {
        this.nationalElectionsResult = nationalResult;
        votingType = VotingType.NATIONAL;
    }

    public ElectionResults(StateElectionsResult stateElectionsResult) {
        this.stateElectionsResult = stateElectionsResult;
        votingType = VotingType.STATE;
    }

    public ElectionResults(TreeSet<Map.Entry<Party,Double>> result) {
        this.fptpResult = result;
        votingType = VotingType.FPTP;
    }

    public VotingType getVotingType() {
        return votingType;
    }

    public NationalElectionsResult getNationalElectionsResult() {
        return nationalElectionsResult;
    }

    public StateElectionsResult getStateElectionsResult() {
        return stateElectionsResult;
    }

    public TreeSet<Map.Entry<Party,Double>> getFptpResult() {
        return fptpResult;
    }
}
