package ar.edu.itba.pod.comparators;

import ar.edu.itba.pod.models.Party;
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.Comparator;

public class DoubleComparator implements Comparator<MutablePair<Party, Double>>, Serializable {
    private static final long serialVersionUID = -8622237157406731352L;

    @Override
    public int compare(MutablePair<Party, Double> e1, MutablePair<Party, Double> e2) {
        int valueComparison = e2.getValue().compareTo(e1.getValue());
        if (valueComparison == 0) {
            return e1.getKey().getDescription().compareTo(e2.getKey().getDescription());
        }
        return valueComparison;
    }
}
