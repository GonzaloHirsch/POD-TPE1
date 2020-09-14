package ar.edu.itba.pod.models;

import java.io.Serializable;

public abstract class ElectionResults implements Serializable {
    private static final long serialVersionUID = 1920965438822291259L;

    protected VotingType votingType;
    public VotingType getVotingType() {
        return votingType;
    }
}
