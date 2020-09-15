package ar.edu.itba.pod;

import ar.edu.itba.pod.client.QueryClient;
import ar.edu.itba.pod.client.VoteClient;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.arguments.VotingClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.exceptions.NoVotesRegisteredException;
import ar.edu.itba.pod.models.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoteClientTest {
    private VoteClient voteClient = new VoteClient();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    VoteService service;

    @Before
    public void setUp(){
        try {
            // Getting the reference to the service
            service = (VoteService) Naming.lookup("//127.0.0.1:1099/" + VoteService.class.getName());


        } catch (RemoteException re){
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe){
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me){
            System.out.println("ERROR: Malformed URL");
        }
    }

    @Test
    public void testVoteClient() throws NoVotesRegisteredException {
        try {
            // Parsing the file
            //            List<Vote> votes = parseInputFile("/home/fpetrikovich/Programacion/POD/POD-TPE1/examples/votes.csv");
            List<Vote> votes = parseInputFile("/Users/gastonlifschitz/ITBA/POD/POD-TPE1/examples/votes.csv");

            final ManagementService managementService = (ManagementService) Naming.lookup("//127.0.0.1:1099/" + ManagementService.class.getName());

            managementService.openElection();

            // Emitting votes
            //emitAllVotes(service, votes);

            votes.forEach(System.out::println);

            managementService.closeElection();

            final QueryService service = (QueryService) Naming.lookup("//127.0.0.1:1099/" + QueryService.class.getName());

            ElectionResults electionResults = service.getNationalResults();
            nationalQuery(electionResults);

            stateQuery(service.getProvinceResults(Province.JUNGLE));
            stateQuery(service.getProvinceResults(Province.SAVANNAH));
            stateQuery(service.getProvinceResults(Province.TUNDRA));

        } catch (IOException | NotBoundException | InvalidElectionStateException | NoVotesRegisteredException e) {
            System.out.println("ERROR: Invalid file given " + e.getMessage());
        }
    }

    private static String getStringFromDoubleTreeSet(TreeSet<MutablePair<Party, Double>> set, boolean includePercent){
        StringBuilder sb = new StringBuilder();
        String percent = includePercent ? "%" : "";
        for(Map.Entry<Party, Double> pair : set){
            if(pair.getValue()!=null){
                sb.append("\n");
                sb.append(String.format("%.2f", pair.getValue())).append(percent).append(";").append(pair.getKey());
            }
        }
        return sb.toString();
    }

    private static void nationalQuery(ElectionResults results) {
        NationalElectionsResult nationalResults = (NationalElectionsResult) results;
        TreeSet<MutablePair<Party, Long>> scoringRound = nationalResults.getScoringRoundResults();
        TreeSet<MutablePair<Party, Double>> automaticRunoff = nationalResults.getAutomaticRunoffResults();
        Party winner = nationalResults.getWinner();

        StringBuilder outputString = new StringBuilder();

        // Scoring results
        outputString.append("Score;Party");
        for(MutablePair<Party, Long> pair : scoringRound){
            outputString.append("\n");
            outputString.append(pair.getValue()).append(";").append(pair.getKey());
        }

        // Automatic runoff results
        outputString.append("\nPercentage;Party");
        outputString.append(getStringFromDoubleTreeSet(automaticRunoff, true));
        // National election winner
        outputString.append("\nWinner\n").append(winner);
        System.out.println(outputString);
    }
    private static String getStringFromDoubleTreeSet(TreeSet<MutablePair<Party, Double>> set){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Party, Double> pair : set){
            if(pair.getValue()!=null){
                sb.append("\n");
                sb.append(pair.getValue()).append(";").append(pair.getKey());
            }
        }
        return sb.toString();
    }
    /**
     * Parses the given file and generates the votes
     * @param path Path to the file
     * @return List with all the votes
     * @throws IOException if the file path is not valid
     */
    private static List<Vote> parseInputFile(String path) throws IOException {
        // Reading all the file lines
        System.out.print(new File(path).toPath());
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
            System.out.println("\nline:" + line);

            Arrays.stream(voteParts).forEach(System.out::println);
            tableId = Integer.parseInt(voteParts[0]);
            province = Province.fromValue(voteParts[1]);
            fptpParty = Party.fromValue(voteParts[3]);
            starAndSpavVotes = voteParts[2].split(",");

            // Structures for the more complex votes
            Map<Party, Long> starVote = new HashMap<>();
            List<Party> spavVote = new ArrayList<>();

            // Iterating through the votes strings
            for (String s : starAndSpavVotes){
                starAndSpavVoteValues = s.split("\\|");

                // Splitting the values
                starVoteParty = Party.fromValue(starAndSpavVoteValues[0]);
                starVoteValue = Long.parseLong(starAndSpavVoteValues[1]);

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

    private static void stateQuery(ElectionResults stateResults) {
        StateElectionsResult stateElectionsResult = (StateElectionsResult) stateResults;
        TreeSet<MutablePair<Party, Double>> firstRound = stateElectionsResult.getFirstRound();
        TreeSet<MutablePair<Party, Double>> secondRound = stateElectionsResult.getSecondRound();
        TreeSet<MutablePair<Party, Double>> thirdRound = stateElectionsResult.getThirdRound();
        Party[] winners = stateElectionsResult.getWinners();

        String outputString = "Round 1\nApproval;Party" +
                getStringFromDoubleTreeSet(firstRound) +
                "\nWinners\n" + winners[0] + "\nRound 2\nApproval;Party" +
                getStringFromDoubleTreeSet(secondRound) +
                "\nWinners\n" + winners[0] + ", " + winners[1] + "\nRound 3\nApproval;Party" +
                getStringFromDoubleTreeSet(thirdRound) +
                "\nWinners\n" + winners[0] + ", " + winners[1] + ", " + winners[2];

        System.out.println(outputString);
    }

    private static void emitAllVotes(VoteService service, List<Vote> votes){
        votes.forEach(v -> {
            try {
                service.emitVote(v);
            } catch (RemoteException | ExecutionException | InterruptedException | InvalidElectionStateException e) {
                System.out.println("ERROR: Server error processing vote");
            }
        });
    }
    private static void emitAllVotesAsync(VoteService service, List<Vote> votes){
        votes.stream().parallel().forEach(v -> {
            Runnable r = () -> {
                try {
                    service.emitVote(v);
                } catch (RemoteException | ExecutionException | InterruptedException | InvalidElectionStateException e) {
                    System.out.println("ERROR: Server error processing vote");
                }
            };

            // Submitting the task
            executor.submit(r);
        });
    }
}
