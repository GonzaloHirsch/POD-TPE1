package ar.edu.itba.pod.exceptions;

/**
 * Exception thrown when an invalid action + state combination is encountered
 */
public class InvalidElectionStateException extends Exception {
    public InvalidElectionStateException(String s){
        super(s);
    }
}
