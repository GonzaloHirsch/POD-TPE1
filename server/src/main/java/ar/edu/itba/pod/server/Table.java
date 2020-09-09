package ar.edu.itba.pod.server;

import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Vote;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
    private final Integer ID;
    private Map<Party, AtomicLong> votes = new HashMap<>();
//    private Map<Party, List<PartyVoteHandler>> auditHandlers = new HashMap<>();


    public Table(Integer ID) {
        this.ID = ID;
        Arrays.stream(Party.values()).forEach(p -> votes.put(p, new AtomicLong(0)));
//        Arrays.stream(Party.values()).forEach(p -> auditHandlers.put(p, new ArrayList<>()));
    }

    public void emitVote(Party party) {
        votes.get(party).getAndIncrement();
//        notifyPartyVote(vote.getFptpVote(), vote);
    }

//    public void registerAuditHandler(Party party, PartyVoteHandler handler) {
//        auditHandlers.get(party).add(handler);
//    }

//    private void notifyPartyVote(Party party, Vote vote) throws RemoteException {
//        for(PartyVoteHandler handler : auditHandlers.get(party))
//            handler.onPartyVote(vote);
//    }
}
