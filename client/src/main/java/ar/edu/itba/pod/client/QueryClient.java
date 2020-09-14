package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
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
                    nationalQuery(results, clientArguments);
                } else {
                    ftptQuery(results, clientArguments, false);
                }
            }
            // This is the TABLE election
            else if (clientArguments.getTableID() != null) {
                ElectionResults tableResults = service.getTableResults(clientArguments.getTableID());
                ftptQuery(tableResults, clientArguments, true);
            }
            // This is the STATE election
            else {
                ElectionResults stateResults = service.getProvinceResults(Province.fromValue(clientArguments.getProvinceName()));
                if (stateResults.getVotingType() == VotingType.STATE) {
                    stateQuery(stateResults, clientArguments);
                } else {
                    ftptQuery(stateResults, clientArguments, false);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void stateQuery(ElectionResults stateResults, QueryClientArguments clientArguments) {
        StateElectionsResult stateElectionsResult = (StateElectionsResult) stateResults;
        TreeSet<Map.Entry<Party,Double>> firstRound = stateElectionsResult.getFirstRound();
        TreeSet<Map.Entry<Party,Double>> secondRound = stateElectionsResult.getSecondRound();
        TreeSet<Map.Entry<Party,Double>> thirdRound = stateElectionsResult.getThirdRound();
        List<Party> winners = stateElectionsResult.getWinners();

        String outputString = "Round 1\nApproval;Party" +
                getStringFromDoubleTreeSet(firstRound) +
                "\nWinners\n" + winners.stream().findFirst() + "\nRound 2\nApproval;Party" +
                getStringFromDoubleTreeSet(secondRound) +
                "\nWinners\n" + winners.stream().findFirst() + ", " + winners.get(1) + "\nRound 3\nApproval;Party" +
                getStringFromDoubleTreeSet(thirdRound) +
                "\nWinners\n" + winners.stream().findFirst() + ", " + winners.get(1) + ", " + winners.get(2);
        write(clientArguments.getOutPutPath(), outputString);
    }
    private static String getStringFromDoubleTreeSet(TreeSet<Map.Entry<Party,Double>> set){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Party, Double> pair : set){
            if(pair.getValue()!=null){
                sb.append("\n");
                sb.append(pair.getValue()).append(";").append(pair.getKey());
            }
        }
        return sb.toString();
    }

    private static void ftptQuery(ElectionResults results, QueryClientArguments clientArguments, boolean showWinner) {
        FPTPResult fptpResult = (FPTPResult) results;

        StringBuilder outputString = new StringBuilder();
        outputString.append("Percentage;Party");
        outputString.append(getStringFromDoubleTreeSet(fptpResult.getFptpResults()));

        if(showWinner){
            outputString.append("\nWinner\n").append(fptpResult.getWinner());
        }
        write(clientArguments.getOutPutPath(), outputString.toString());
    }

    public static void nationalQuery(ElectionResults results, QueryClientArguments clientArguments) {
        NationalElectionsResult nationalResults = (NationalElectionsResult) results;
        TreeSet<Map.Entry<Party,Long>> scoringRound         = nationalResults.getScoringRoundResults();
        TreeSet<Map.Entry<Party,Double>> automaticRunoff    = nationalResults.getAutomaticRunoffResults();
        Party winner                                        = nationalResults.getWinner();

        StringBuilder outputString = new StringBuilder();

        // Scoring results
        outputString.append("Score;Party");
        for(Map.Entry<Party, Long> pair : scoringRound){
            outputString.append("\n");
            outputString.append(pair.getValue()).append(";").append(pair.getKey());
        }

        // Automatic runoff results
        outputString.append("\nPercentage;Party");
        outputString.append(getStringFromDoubleTreeSet(automaticRunoff));

        // National election winner
        outputString.append("\nWinner\n").append(winner);

        write(clientArguments.getOutPutPath(), outputString.toString());
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
