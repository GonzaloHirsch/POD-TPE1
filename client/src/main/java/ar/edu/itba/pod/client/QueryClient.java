package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.ManagementClientArguments;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.ElectionNotStartedException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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


        final QueryService service = (QueryService) Naming.lookup("//" + clientArguments.getServerAddress() + "/queryService");

        if(clientArguments.getProvinceName().equals("") && clientArguments.getTableID() == null){
            try {
                List<Map.Entry<Party, Long>> nationalResults = service.getNationalResults();
            } catch (ElectionNotStartedException e) {
                e.printStackTrace();
            }
        }else if(clientArguments.getTableID() != null){
            try {
                List<Map.Entry<Party, Double>> tableResults = service.getTableResults(clientArguments.getTableID());
            } catch (ElectionNotStartedException e) {
                e.printStackTrace();
            }
        }else{
            try {
                List<Map.Entry<Party, Long>> stateResults = service.getProvinceResults(Province.fromValue(clientArguments.getProvinceName()));
            } catch (ElectionNotStartedException e) {
                e.printStackTrace();
            }
        }
    }
}
