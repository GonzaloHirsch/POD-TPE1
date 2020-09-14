package ar.edu.itba.pod.models;

import java.io.Serializable;

public abstract class ElectionResults implements Serializable {
    protected VotingType votingType;
    public VotingType getVotingType() {
        return votingType;
    }
}
