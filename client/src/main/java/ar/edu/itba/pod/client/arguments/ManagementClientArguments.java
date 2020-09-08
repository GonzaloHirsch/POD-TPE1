package ar.edu.itba.pod.client.arguments;

import ar.edu.itba.pod.client.ManagementClient;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;

import java.util.Optional;
import java.util.Properties;

public class ManagementClientArguments {
    private ManagementClientActions action;
    private String serverAddress;

    private static final String ACTIONS_KEY = "action";
    private static final String SERVER_ADDRESS_KEY = "serverAddress";

    public ManagementClientActions getAction() {
        return action;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Parses the arguments passed to the client and stores the values
     * @throws InvalidArgumentsException if an invalid argument is received
     */
    public void parseArguments() throws InvalidArgumentsException {
        Properties props = System.getProperties();

        // Try to obtain the actions parameter
        if (!props.containsKey(ACTIONS_KEY)){
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for action");
        } else {
            this.action = ManagementClientActions.fromValue(props.getProperty(ACTIONS_KEY));
        }

        // Try to obtain the server address
        if (!props.containsKey(SERVER_ADDRESS_KEY)){
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for serverAddress");
        } else {
            this.serverAddress = props.getProperty(SERVER_ADDRESS_KEY);
        }
    }

    /**
     * Method to print the help for the management client
     */
    private void printHelp(){
        System.out.println("This program should be run like:\n"+
                "$>./run-ManagementClient -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName\n"+
                "Where: \n"+
                " - DserverAddress is xx.xx.xx.xx:yyyy with xx.xx.xx.xx is the server address and yyyy the port of the server\n"+
                " - Daction is open, closed or state");
    }

    /**
     * Enum for the actions available
     */
    public enum ManagementClientActions {
        OPEN("open"), CLOSE("close"), STATE("state");

        private String actionString;

        private ManagementClientActions(String s){
            this.actionString = s;
        }

        /**
         * Static method to obtain the enum value for the given string
         * @param s value for an enum value
         * @return ManagementClientActions represented by the value string
         * @throws RuntimeException if invalid string is given
         */
        public static ManagementClientActions fromValue(String s) throws RuntimeException {
            String value = Optional.ofNullable(s).orElseThrow(RuntimeException::new).toLowerCase();
            for (ManagementClientActions action : ManagementClientActions.values()){
                if (value.equals(action.actionString)){
                    return action;
                }
            }
            throw new RuntimeException();
        }
    }
}
