package ar.edu.itba.pod.client.arguments;

import ar.edu.itba.pod.client.exceptions.InvalidArgumentsException;

import java.util.Optional;
import java.util.Properties;

public class QueryClientArguments {
    private String serverAddress;
    private String provinceName;
    private String outputPath;
    private Integer tableID = null;

    private static final String STATE_KEY = "state";
    private static final String ID_KEY = "id";
    private static final String OUT_PATH = "outPath";
    private static final String SERVER_ADDRESS_KEY = "serverAddress";

    public String getServerAddress() {
        return serverAddress;
    }
    public String getProvinceName() {
        return provinceName;
    }
    public String getOutputPath() {
        return outputPath;
    }

    public Integer getTableID() {
        return tableID;
    }

    /**
     * Parses the arguments passed to the client and stores the values
     * @throws InvalidArgumentsException if an invalid argument is received
     */
    public void parseArguments() throws InvalidArgumentsException {
        Properties props = System.getProperties();

        if (props.containsKey(STATE_KEY) && props.containsKey(ID_KEY)){
            this.printHelp();
            throw new InvalidArgumentsException("Cannot specify both table id and state");
        }

        // Try to obtain the state parameter
        if (props.containsKey(STATE_KEY)){
            this.provinceName = props.getProperty(STATE_KEY);
        }

        // Try to obtain the id parameter
        if (props.containsKey(ID_KEY)){
            this.tableID = Integer.parseInt(props.getProperty(ID_KEY));
        }

        // Try to obtain the out path parameter
        if (!props.containsKey(OUT_PATH)){
            this.printHelp();
            throw new InvalidArgumentsException("Invalid argument for outPath");
        } else {
            this.outputPath = props.getProperty(OUT_PATH);
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
                "$>./run-query -DserverAddress=xx.xx.xx.xx:yyyy [ -Dstate=stateName |\n" +
                "-Did=pollingPlaceNumber ] -DoutPath=fileName\n"+
                "Where: \n"+
                " - DserverAddress is xx.xx.xx.xx:yyyy with xx.xx.xx.xx is the server address and yyyy the port of the server\n"+
                " - Dstate is the name of the province\n"+
                " - Did is the voting table id\n"+
                " - DoutPath is the path where the results file will be stored\n");
    }
}
