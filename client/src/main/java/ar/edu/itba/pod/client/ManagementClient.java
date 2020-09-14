package ar.edu.itba.pod.client;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.client.arguments.ManagementClientArguments;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;
import ar.edu.itba.pod.exceptions.InvalidElectionStateException;
import ar.edu.itba.pod.models.ElectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagementClient {

    public static void main(final String[] args) {
        try {
            ManagementClientArguments clientArguments = new ManagementClientArguments();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
            }

            // Getting the reference to the service
            final ManagementService service = (ManagementService) Naming.lookup("//" + clientArguments.getServerAddress() + "/" + ManagementService.class.getName());

            // Performing actions depending on the given parameter action
            try {
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
                System.out.println("ERROR: Invalid election state");
            }
        } catch (RemoteException re){
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe){
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me){
            System.out.println("ERROR: Malformed URL");
        }
    }
}
