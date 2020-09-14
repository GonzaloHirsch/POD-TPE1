package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.models.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeSet;

public class QueryClient {
    private static final Logger LOG = LoggerFactory.getLogger(QueryClient.class);

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        LOG.info("QueryClient has started...");

        QueryClientArguments clientArguments = new QueryClientArguments();

        // Parsing the arguments
        try {
            clientArguments.parseArguments();
        } catch (InvalidArgumentsException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Getting the reference to the server
        final QueryService service = (QueryService) Naming.lookup("//" + clientArguments.getServerAddress() + "/" + QueryService.class.getName());

        try {
            // This is the NATIONAL election
            if (clientArguments.getProvinceName() == null && clientArguments.getTableID() == null) {
                ElectionResults results = service.getNationalResults();
                if (results.getVotingType() == VotingType.NATIONAL) {
                    nationalQuery(results, clientArguments.getOutputPath());
                } else {
                    ftptQuery(results, clientArguments.getOutputPath(), false);
                }
            }
            // This is the TABLE election
            else if (clientArguments.getTableID() != null) {
                ElectionResults tableResults = service.getTableResults(clientArguments.getTableID());
                ftptQuery(tableResults, clientArguments.getOutputPath(), true);
            }
            // This is the STATE election
            else {
                ElectionResults stateResults = service.getProvinceResults(Province.fromValue(clientArguments.getProvinceName()));
                if (stateResults.getVotingType() == VotingType.STATE) {
                    stateQuery(stateResults, clientArguments.getOutputPath());
                } else {
                    ftptQuery(stateResults, clientArguments.getOutputPath(), false);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void stateQuery(ElectionResults stateResults, String filename) {
        StateElectionsResult stateElectionsResult = (StateElectionsResult) stateResults;
        TreeSet<MutablePair<Party, Double>> firstRound = stateElectionsResult.getFirstRound();
        TreeSet<MutablePair<Party, Double>> secondRound = stateElectionsResult.getSecondRound();
        TreeSet<MutablePair<Party, Double>> thirdRound = stateElectionsResult.getThirdRound();
        Party[] winners = stateElectionsResult.getWinners();

        String outputString = "Round 1\nApproval;Party" +
                getStringFromDoubleTreeSet(firstRound, false) +
                "\nWinners\n" + winners[0] + "\nRound 2\nApproval;Party" +
                getStringFromDoubleTreeSet(secondRound, false) +
                "\nWinners\n" + winners[0] + ", " + winners[1] + "\nRound 3\nApproval;Party" +
                getStringFromDoubleTreeSet(thirdRound, false) +
                "\nWinners\n" + winners[0] + ", " + winners[1] + ", " + winners[2];
        write(filename, outputString);
    }
    private static String getStringFromDoubleTreeSet(TreeSet<MutablePair<Party, Double>> set, boolean includePercent){
        StringBuilder sb = new StringBuilder();
        String percent = includePercent ? "%" : "";

        set.forEach(pair -> {
            if(pair.getRight() != null && pair.getRight() != 0.0)
                sb.append("\n").append(String.format("%.2f", pair.getValue())).append(percent).append(";").append(pair.getKey());
        });
        return sb.toString();
    }

    private static void ftptQuery(ElectionResults results, String filename, boolean showWinner) {
        FPTPResult fptpResult = (FPTPResult) results;

        StringBuilder outputString = new StringBuilder();
        outputString.append("Percentage;Party");
        outputString.append(getStringFromDoubleTreeSet(fptpResult.getFptpResults(), true));

        if(showWinner){
            outputString.append("\nWinner\n").append(fptpResult.getWinner());
        }
        write(filename, outputString.toString());
    }

    public static void nationalQuery(ElectionResults results, String filename) {
        NationalElectionsResult nationalResults = (NationalElectionsResult) results;
        TreeSet<MutablePair<Party, Long>> scoringRound = nationalResults.getScoringRoundResults();
        TreeSet<MutablePair<Party, Double>> automaticRunoff = nationalResults.getAutomaticRunoffResults();
        Party winner = nationalResults.getWinner();

        StringBuilder outputString = new StringBuilder();

        // Scoring results
        outputString.append("Score;Party");
        scoringRound.forEach(pair -> {
            if(pair.getRight()!=0)
                outputString.append("\n").append(pair.getValue()).append(";").append(pair.getKey());
        });
        // Automatic runoff results
        outputString.append("\nPercentage;Party");
        outputString.append(getStringFromDoubleTreeSet(automaticRunoff, true));

        // National election winner
        outputString.append("\nWinner\n").append(winner);

        write(filename, outputString.toString());
    }

    private static void write (String filename , String value) {
        try {
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(value);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing the election results to " + filename);
        }
    }
}
