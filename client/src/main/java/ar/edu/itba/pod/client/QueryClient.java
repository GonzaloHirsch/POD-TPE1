package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
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


        final QueryService service = (QueryService) Naming.lookup("//" + clientArguments.getServerAddress() + "/" + QueryService.class.getName());

        if(clientArguments.getProvinceName().equals("") && clientArguments.getTableID() == null){
            try {
                ElectionResults results = service.getNationalResults();
                if(results.getVotingType()== VotingType.NATIONAL)
                    nationalQuery(results,clientArguments);
                else
                    ftptQuery(results, clientArguments, false);
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }else if(clientArguments.getTableID() != null){
            try {
                ElectionResults tableResults = service.getTableResults(clientArguments.getTableID());
                ftptQuery(tableResults,clientArguments,true);
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ElectionResults stateResults = service.getProvinceResults(Province.fromValue(clientArguments.getProvinceName()));
                stateQuery(stateResults,clientArguments);
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }
    }

    private static void stateQuery(ElectionResults stateResults, QueryClientArguments clientArguments) {
        StateElectionsResult stateElectionsResult = stateResults.getStateElectionsResult();
        TreeSet<Map.Entry<Party,Double>> firstRound = stateElectionsResult.getFirstRound();
        TreeSet<Map.Entry<Party,Double>> secondRound = stateElectionsResult.getSecondRound();
        TreeSet<Map.Entry<Party,Double>> thirdRound = stateElectionsResult.getThirdRound();
        List<Party> winners = stateElectionsResult.getWinners();

        String outputString = "Round 1\nApproval;Party" +
                getStringFromTreeSet(firstRound) +
                "\nWinners\n" + winners.stream().findFirst() + "\nRound 2\nApproval;Party" +
                getStringFromTreeSet(secondRound) +
                "\nWinners\n" + winners.stream().findFirst() + ", " + winners.get(1) + "\nRound 3\nApproval;Party" +
                getStringFromTreeSet(thirdRound) +
                "\nWinners\n" + winners.stream().findFirst() + ", " + winners.get(1) + ", " + winners.get(2);
        write(clientArguments.getOutPutPath(), outputString);
    }
    private static String getStringFromTreeSet(TreeSet<Map.Entry<Party,Double>> round){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Party, Double> pair : round){
            if(pair.getValue()!=null){
                sb.append("\n");
                sb.append(pair.getValue()).append(";").append(pair.getKey());
            }
        }
        return sb.toString();
    }

    private static void ftptQuery(ElectionResults results, QueryClientArguments clientArguments, boolean showWinner) {
        TreeSet<Map.Entry<Party,Double>> ftptResults = results.getFptpResult();

        StringBuilder outputString = new StringBuilder();
        outputString.append("Percentage;Party");
        for(Map.Entry<Party, Double> pair : ftptResults){
            outputString.append("\n");
            outputString.append(pair.getValue()).append(";").append(pair.getKey());
        }

        if(showWinner){
            outputString.append("\nWinner\n").append(results.getFptpResult().first().getKey());
        }
        write(clientArguments.getOutPutPath(), outputString.toString());
    }

    public static void nationalQuery(ElectionResults nationalResults, QueryClientArguments clientArguments) {
        TreeSet<Map.Entry<Party,Long>> scoringRound         = nationalResults.getNationalElectionsResult().getScoringRoundResults();
        TreeSet<Map.Entry<Party,Double>> automaticRunoff    = nationalResults.getNationalElectionsResult().getAutomaticRunoffResults();
        Party winner                                        = nationalResults.getNationalElectionsResult().getWinner();

        StringBuilder outputString = new StringBuilder();
        outputString.append("Score;Party");
        for(Map.Entry<Party, Long> pair : scoringRound){
            outputString.append("\n");
            outputString.append(pair.getValue()).append(";").append(pair.getKey());
        }

        outputString.append("\nPercentage;Party");
        for(Map.Entry<Party, Double> pair : automaticRunoff){
            outputString.append("\n");
            outputString.append(pair.getValue()).append(";").append(pair.getKey());
        }
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
            e.printStackTrace();
        }
    }
}
