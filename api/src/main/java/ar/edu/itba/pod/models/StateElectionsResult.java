package ar.edu.itba.pod.models;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class StateElectionsResult extends ElectionResults{
    private static final long serialVersionUID = -3568878105168093744L;
    private final Province province;
    private final TreeSet<MutablePair<Party, Double>> firstRound;
    private final TreeSet<MutablePair<Party, Double>> secondRound;
    private final TreeSet<MutablePair<Party, Double>> thirdRound;
    // Winners are in order, index 0 -> first round winner, ...
    private final List<Party> winners;

    public StateElectionsResult(Province province, TreeSet<MutablePair<Party, Double>> firstRound,
                                TreeSet<MutablePair<Party, Double>> secondRound, TreeSet<MutablePair<Party, Double>> thirdRound, List<Party> winners) {
        this.province = province;
        this.firstRound = firstRound;
        this.secondRound = secondRound;
        this.thirdRound = thirdRound;
        this.winners = winners;
        this.votingType = VotingType.STATE;
    }

    public Province getProvince() {
        return province;
    }

    public TreeSet<MutablePair<Party, Double>> getFirstRound() {
        return firstRound;
    }

    public TreeSet<MutablePair<Party, Double>> getSecondRound() {
        return secondRound;
    }

    public TreeSet<MutablePair<Party, Double>> getThirdRound() {
        return thirdRound;
    }

    public List<Party> getWinners() {
        return winners;
    }
}
