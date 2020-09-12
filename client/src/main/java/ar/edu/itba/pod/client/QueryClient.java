package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.Province;
import ar.edu.itba.pod.models.ElectionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
                ElectionResults nationalResults = service.getNationalResults();
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }else if(clientArguments.getTableID() != null){
            try {
                ElectionResults tableResults = service.getTableResults(clientArguments.getTableID());
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ElectionResults stateResults = service.getProvinceResults(Province.fromValue(clientArguments.getProvinceName()));
            } catch (InvalidElectionStateException e) {
                e.printStackTrace();
            }
        }
    }
}
