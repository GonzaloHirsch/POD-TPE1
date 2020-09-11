package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Province;

import java.util.*;

public class StateElection {
    /**
     * Each province holds a list of the list of candidate parties
     */
    private final Map<Province, List<List<Party>>> ballots = new HashMap<>();
    private Map<Integer, Map<Party, Double>> results;

    public StateElection() {
        // Initialize the map with the values for the provinces
        Arrays.stream(Province.values()).forEach(p -> ballots.put(p, new ArrayList<>()));
    }

    /**
     * Given a SPAV vote in the format of List of parties, and the corresponding province, store the vote
     * @param province Province for the vote
     * @param vote List of chosen candidate parties
     */
    public void emitVote(Province province, List<Party> vote) {
        synchronized (this.ballots) {
            this.ballots.get(province).add(vote);
        }
    }

    public List<List<Party>> getBallotsPerProvince(Province province) {
        return ballots.get(province);
    }
}
