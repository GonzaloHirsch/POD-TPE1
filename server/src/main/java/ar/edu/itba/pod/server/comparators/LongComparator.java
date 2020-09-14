package ar.edu.itba.pod.server.comparators;

import ar.edu.itba.pod.models.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.Comparator;

public class LongComparator implements Comparator<MutablePair<Party, Long>>, Serializable {
    private static final long serialVersionUID = -8622237157406731352L;

    @Override
    public int compare(MutablePair<Party, Long> e1, MutablePair<Party, Long> e2) {
        int valueComparison = e2.getValue().compareTo(e1.getValue());
        if (valueComparison == 0) {
            return e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
        }
        return valueComparison;
    }
}

