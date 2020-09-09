package ar.edu.itba.pod;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Vote implements Serializable {
    private final Province province;
    private final Integer table; // FIXME puede ser int -> como prefieran
    private final Party fptpVote;
    private final Map<Party, Long> starVote;
    private final List<Party> spavVote;

    public Vote(Province province, Integer table, Party fptpVote, Map<Party, Long> starVote, List<Party> spavVote) {
        this.table = table;
        this.province = province;
        this.fptpVote = fptpVote;
        this.starVote = starVote;
        this.spavVote = spavVote;
    }

    public Integer getTable() {
        return table;
    }

    public Province getProvince() {
        return province;
    }

    public Party getFptpVote() {
        return fptpVote;
    }

    public Map<Party, Long> getStarVote() {
        return starVote;
    }

    public List<Party> getSpavVote() {
        return spavVote;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "province=" + province +
                ", table=" + table +
                ", fptpVote=" + fptpVote +
                ", starVote=" + starVote +
                ", spavVote=" + spavVote +
                '}';
    }
}
