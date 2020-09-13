package ar.edu.itba.pod.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class StateElectionsResult implements Serializable {
    private final Province province;
    private final TreeSet<Map.Entry<Party, Double>> firstRound;
    private final TreeSet<Map.Entry<Party, Double>> secondRound;
    private final TreeSet<Map.Entry<Party, Double>> thirdRound;
    // Winners are in order, index 0 -> first round winner, ...
    private final List<Party> winners;

    public StateElectionsResult(Province province, TreeSet<Map.Entry<Party, Double>> firstRound,
                                TreeSet<Map.Entry<Party, Double>> secondRound, TreeSet<Map.Entry<Party, Double>> thirdRound, List<Party> winners) {
        this.province = province;
        this.firstRound = firstRound;
        this.secondRound = secondRound;
        this.thirdRound = thirdRound;
        this.winners = winners;
    }

    public Province getProvince() {
        return province;
    }

    public TreeSet<Map.Entry<Party, Double>> getFirstRound() {
        return firstRound;
    }

    public TreeSet<Map.Entry<Party, Double>> getSecondRound() {
        return secondRound;
    }

    public TreeSet<Map.Entry<Party, Double>> getThirdRound() {
        return thirdRound;
    }

    public List<Party> getWinners() {
        return winners;
    }
}
