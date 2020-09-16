package ar.edu.itba.pod.exceptions;

public class InsufficientWinnersException extends Exception {
    public InsufficientWinnersException(){
        super("Not enough winners for this dimension");
    }
}
