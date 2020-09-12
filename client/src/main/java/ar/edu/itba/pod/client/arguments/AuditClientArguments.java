package ar.edu.itba.pod.client.arguments;

import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;

import java.util.Properties;

public class AuditClientArguments {
    private String serverAddress;
    private int tableID;
    private Party party;

    private static final String PARTY_KEY = "party";
    private static final String TABLE_ID_KEY = "id";
    private static final String SERVER_ADDRESS_KEY = "serverAddress";

    public Party getParty() {
        return party;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getTableID() {
        return tableID;
    }

    /**
     * Parses the arguments passed to the client and stores the values
     * @throws InvalidArgumentsException if an invalid argument is received
     */
    public void parseArguments() throws InvalidArgumentsException {
        Properties props = System.getProperties();

        // Try to obtain the party parameter
        if (!props.containsKey(PARTY_KEY)){
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for party");
        } else {
            this.party = Party.fromValue(props.getProperty(PARTY_KEY));
        }

        // Try to obtain the table id parameter
        if (!props.containsKey(TABLE_ID_KEY)){
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for table id");
        } else {
            this.tableID = Integer.parseInt(props.getProperty(TABLE_ID_KEY));
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
        System.out.println("This program should be run as follows:\n"+
                "$>./run-audit -DserverAddress=xx.xx.xx.xx:yyyy -Did=pollingPlaceNumber -Dparty=partyName\n"+
                "Where: \n"+
                " - DserverAddress is xx.xx.xx.xx:yyyy with xx.xx.xx.xx is the server address and yyyy the port of the server\n"+
                " - Did is the id of the polling station the audit officer will be registered to\n" +
                " - Dparty is the party which the audit officer belongs to");
    }
}
