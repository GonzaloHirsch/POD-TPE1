package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.VotingClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;
import ar.edu.itba.pod.models.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoteClient {
    private static final Logger LOG = LoggerFactory.getLogger(VoteClient.class);

    // Executor service for all threads
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final int TABLE_ID = 0;
    private static final int PROVINCE = 1;
    private static final int STAR_SPAV_VOTE = 2;
    private static final int FPTP_VOTE = 3;
    private static final int STAR_VOTE_PARTY = 0;
    private static final int STAR_VOTE_VALUE = 1;

    public static void main(final String[] args){
        try {
            VotingClientArguments clientArguments = new VotingClientArguments();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
            }

            // Getting the reference to the service
            final VoteService service = (VoteService) Naming.lookup("//" + clientArguments.getServerAddress() + "/" + VoteService.class.getName());

            try {
                // Parsing the file
                List<Vote> votes = parseInputFile(clientArguments.getVotesPath());

                // Emitting votes
                emitAllVotes(service, votes);

                System.out.printf("%d votes registered\n", votes.size());

                // Shutdown and wait
                executor.shutdown();
            } catch (IOException e) {
                System.out.println("ERROR: Invalid file given");
            }
        } catch (RemoteException re){
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe){
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me){
            System.out.println("ERROR: Malformed URL");
        }
    }

    /**
     * Emits all the given votes to the service
     * @param service Service to be used to emit the votes
     * @param votes List of parsed votes
     */
    private static void emitAllVotes(VoteService service, List<Vote> votes){
        // Emit all votes, use a parallel stream
        // TODO: CHECK PERFORMANCE
        votes.stream().parallel().forEach(v -> emitVote(service, v));
    }

    /**
     * Emit a single vote using a runnable to a executor service
     * @param service Service to be used to emit the votes
     * @param vote Vote to be emitted
     */
    private static void emitVote(VoteService service, Vote vote){
        // Creating the runnable task
        Runnable r = () -> {
            try {
                service.emitVote(vote);
            } catch (RemoteException | ExecutionException | InterruptedException | InvalidElectionStateException e) {
                System.out.println("ERROR: Server error processing vote");
                System.out.println(e.getMessage());
            }
        };

        // Submitting the task
        executor.submit(r);
    }

    /**
     * Parses the given file and generates the votes
     * @param path Path to the file
     * @return List with all the votes
     * @throws IOException if the file path is not valid
     */
    private static List<Vote> parseInputFile(String path) throws IOException {
        // Reading all the file lines
        List<String> lines = Files.readAllLines(new File(path).toPath());

        String[] voteParts;

        // Variables to be extracted
        int tableId;
        Province province;
        Party fptpParty;
        String[] starAndSpavVotes;
        String[] starAndSpavVoteValues;
        Party starVoteParty;
        long starVoteValue;

        // List to hold all the votes
        List<Vote> votes = new ArrayList<>();

        // Iterating and splitting in the ; as indicated
        for (String line : lines)
        {
            System.out.println(line);
            /*
            * The parts of the string are:
            *  - 0 -> table id
            *  - 1 -> province
            *  - 2 -> STAR & SPAV vote
            *  - 3 -> FPTP vote
            * */
            voteParts = line.trim().split(";");

            // Extracting the values
            tableId = Integer.parseInt(voteParts[TABLE_ID]);
            province = Province.fromValue(voteParts[PROVINCE]);
            fptpParty = Party.fromValue(voteParts[FPTP_VOTE]);
            starAndSpavVotes = voteParts[STAR_SPAV_VOTE].split(",");

            // Structures for the more complex votes
            Map<Party, Long> starVote = new HashMap<>();
            List<Party> spavVote = new ArrayList<>();

            if (starAndSpavVotes.length >= 1 && !starAndSpavVotes[0].isEmpty()){
                // Iterating through the votes strings
                for (String s : starAndSpavVotes){
                    LOG.debug("{} THE VOTE", s);
                    starAndSpavVoteValues = s.split("\\|");

                    // Splitting the values
                    starVoteParty = Party.fromValue(starAndSpavVoteValues[STAR_VOTE_PARTY]);
                    starVoteValue = Long.parseLong(starAndSpavVoteValues[STAR_VOTE_VALUE]);

                    // Adding to the structures
                    starVote.put(starVoteParty, starVoteValue);
                    spavVote.add(starVoteParty);
                }
            }

            // Creating vote and adding it to the list
            Vote vote = new Vote(province, tableId, fptpParty, starVote, spavVote);
            votes.add(vote);
        }

        return votes;
    }
}
