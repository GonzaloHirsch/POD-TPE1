package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.ManagementClientArguments;
import ar.edu.itba.pod.client.arguments.QueryClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class QueryClient {
    private static final Logger LOG = LoggerFactory.getLogger(QueryClient.class);

    public static void main(final String[] args) throws RemoteException, NotBoundException, MalformedURLException, InterruptedException {
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
        service.getNationalResults();

        /*try {
            switch (clientArguments.getAction()) {
                case OPEN:
                    service.openElection();
                    System.out.println("Election Started");
                    break;
                case CLOSE:
                    service.closeElection();
                    System.out.println("Election Finished");
                    break;
                case STATE:
                    ElectionState state = service.getElectionState();
                    System.out.println(state.getDescription());
                    break;
            }
        } catch (InvalidElectionStateException e) {
            System.out.println("ERROR: Invalid state");
        }*/

    }
}
