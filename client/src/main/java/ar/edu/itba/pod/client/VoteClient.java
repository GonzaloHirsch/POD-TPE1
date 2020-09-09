package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.Party;
import ar.edu.itba.pod.Province;
import ar.edu.itba.pod.Vote;
import ar.edu.itba.pod.client.arguments.VotingClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
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

public class VoteClient {
    private static final Logger LOG = LoggerFactory.getLogger(VoteClient.class);

    private static final int TABLE_ID = 0;
    private static final int PROVINCE = 1;
    private static final int STAR_SPAV_VOTE = 2;
    private static final int FPTP_VOTE = 3;
    private static final int STAR_VOTE_PARTY = 0;
    private static final int STAR_VOTE_VALUE = 1;

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
        VotingClientArguments clientArguments = new VotingClientArguments();

        // Parsing the arguments
        try {
            clientArguments.parseArguments();
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
        }

        // Getting the reference to the service
        //final ManagementService service = (ManagementService) Naming.lookup("//" + clientArguments.getServerAddress() + "/managementService");

        // Parsing the file
        try {
            List<Vote> votes = parseInputFile(clientArguments.getVotesPath());

            LOG.info("THE VOTES ARE {}", votes);
        } catch (IOException e) {
            System.out.println("Invalid file given");
        }
    }

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

            // Iterating through the votes strings
            for (String s : starAndSpavVotes){
                starAndSpavVoteValues = s.split("\\|");

                // Splitting the values
                starVoteParty = Party.fromValue(starAndSpavVoteValues[STAR_VOTE_PARTY]);
                starVoteValue = Long.parseLong(starAndSpavVoteValues[STAR_VOTE_VALUE]);

                // Adding to the structures
                starVote.put(starVoteParty, starVoteValue);
                spavVote.add(starVoteParty);
            }

            // Creating vote and adding it to the list
            Vote vote = new Vote(province, tableId, fptpParty, starVote, spavVote);
            votes.add(vote);
        }

        return votes;
    }
}
