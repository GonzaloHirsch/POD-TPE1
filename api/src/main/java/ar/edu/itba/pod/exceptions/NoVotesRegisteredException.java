package ar.edu.itba.pod.exceptions;

public class NoVotesRegisteredException extends RuntimeException {
    public NoVotesRegisteredException(){
        super("No votes registered");
    }
}
