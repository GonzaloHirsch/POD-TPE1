package ar.edu.itba.pod.exceptions;

public class NoVotesRegisteredException extends Exception {
    public NoVotesRegisteredException(){
        super("No Votes");
    }
}
