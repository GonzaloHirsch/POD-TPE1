package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Province;

import java.util.*;

public class StateElection {

    // we have a list of all spav tickets, per province
    private Map<Province, List<List<Party>>> ballots = new HashMap<>();
    private Map<Integer, Map<Party, Double>> results;

    public StateElection() {
        Arrays.stream(Province.values()).forEach(p -> ballots.put(p, new ArrayList<>()));
    }

    /**
     * Given a batch of SPAV votes, stores these on approvals
     * @return quantity of processed votes
     */
    public int emitVotes(Map<Province, List<List<Party>>> spavVotes) {
        spavVotes.keySet().forEach(p -> ballots.get(p).addAll(spavVotes.get(p)));
        return spavVotes.size();
    }

    public List<List<Party>> getBallotsPerProvince(Province province) {
        return ballots.get(province);
    }
}
