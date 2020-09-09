package ar.edu.itba.pod.server.models;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Province;

import java.util.*;

public class StateElection {

    // we have a list of all spav tickets, per province
    private Map<Province, List<List<Party>>> approvals = new HashMap<>();
    private Map<Integer, Map<Party, Double>> results;

    public StateElection() {
        Arrays.stream(Province.values()).forEach(p -> approvals.put(p, new ArrayList<>()));
    }

    public void emitVotes(Map<Province, List<List<Party>>> spavVotes) {
        spavVotes.keySet().forEach(p -> approvals.get(p).addAll(spavVotes.get(p)));
    }
}
