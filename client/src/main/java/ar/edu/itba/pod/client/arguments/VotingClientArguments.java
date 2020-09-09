package ar.edu.itba.pod.client.arguments;

import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;

import java.util.Properties;

public class VotingClientArguments {
    private String votesPath;
    private String serverAddress;

    private static final String VOTES_FILENAME_KEY = "votesPath";
    private static final String SERVER_ADDRESS_KEY = "serverAddress";

    public String getVotesPath() {
        return votesPath;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Parses the arguments passed to the client and stores the values
     *
     * @throws InvalidArgumentsException if an invalid argument is received
     */
    public void parseArguments() throws InvalidArgumentsException {
        Properties props = System.getProperties();

        // Try to obtain the actions parameter
        if (!props.containsKey(VOTES_FILENAME_KEY)) {
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for votesPath");
        } else {
            this.votesPath = props.getProperty(VOTES_FILENAME_KEY);
        }

        // Try to obtain the server address
        if (!props.containsKey(SERVER_ADDRESS_KEY)) {
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for serverAddress");
        } else {
            this.serverAddress = props.getProperty(SERVER_ADDRESS_KEY);
        }
    }

    /**
     * Method to print the help for the vote client
     */
    private void printHelp() {
        System.out.println("This program should be run like:\n" +
                "$>./run-vote -DserverAddress=xx.xx.xx.xx:yyyy -DvotesPath=filename\n" +
                "Where: \n" +
                " - DserverAddress is xx.xx.xx.xx:yyyy with xx.xx.xx.xx is the server address and yyyy the port of the server\n" +
                " - DvotesPath is the path to the file");
    }
}
