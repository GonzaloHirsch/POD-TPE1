package ar.edu.itba.pod;

import ar.edu.itba.pod.models.Party;
import ar.edu.itba.pod.models.Province;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class VoteHelper {
    private static final String prefix = "votes";
    private static final String extension = ".csv";
    private static final String outputPath = "examples/";

    private static final char delimiter = ';';
    private static final char pipe = '|';
    private static final char comma = ',';
    private static final char newLine = '\n';

    private static final List<Integer> points = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});

    private static final Random random = new Random();

    private static Map<Province, List<Integer>> tables = new HashMap<>();

    private static final int votesQuantity = 30;
    private static final int votesFileQuantity = 5;

    /**
     * Generates votesFileQuantity files with votesQuantity votes in examples/
     */
    public static void generateRandomVoteFiles() {
        generateTablesPerProvince();
        for(int i=0; i<votesFileQuantity; i++) {
            try {
                FileWriter f = new FileWriter(outputPath + prefix + i + extension, false);
                for(int j=0; j<votesQuantity; j++){
                    f.write(getRandomVoteLine());
                    if(j < votesQuantity-1) f.write(newLine);
                }
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * assigns tables to Provinces
     */
    private static void generateTablesPerProvince() {
        // MUST be 3x Province.values().length
        int tableIDs[] = {1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008};
        int i=0;
        for(Province p : Province.values()) {
            List<Integer> provinceTables = new ArrayList<>();
            for(int j=0; j<3; j++) {
                provinceTables.add(tableIDs[i++]);
            }
            tables.put(p, provinceTables);
        }
    }

    /**
     * @return String with a new line such as '1000;JUNGLE;TIGER|3,LEOPARD|2,LYNX|1;TIGER'
     */
    private static String getRandomVoteLine() {
        StringBuilder str = new StringBuilder();
        Province province = getRandomProvince();
        str.append(getRandomTable(province));
        str.append(delimiter);
        str.append(province.getDescription());
        str.append(delimiter);
        str.append(getSpavStarVote());
        str.append(delimiter);
        str.append(getRandomParty().getDescription());
        return str.toString();
    }

    /**
     * @return random province
     */
    private static Province getRandomProvince() {
        return Province.values()[random.nextInt(Province.values().length)];
    }

    /**
     * @param province from where the table is needed
     * @return random table for province
     */
    private static int getRandomTable(Province province) {
        return tables.get(province).get(random.nextInt(tables.get(province).size()));
    }

    /**
     * @return String representing SpavStarVote on csv file
     */
    private static String getSpavStarVote() {
        StringBuilder ballot = new StringBuilder();

        // How many votes are going to be for star and spav
        int ballotVotes = random.nextInt(4);

        // we want to shuffle points for these votes
        Collections.shuffle(points);

        // we get as many random parties as we need
        List<Party> parties = getRandomParties(ballotVotes);

        // for every vote...
        for(int i=0; i<ballotVotes; i++) {
            ballot.append(parties.get(i).getDescription());
            ballot.append(pipe);
            ballot.append(points.get(i));
            if(i < ballotVotes-1) ballot.append(comma);
        }
        return ballot.toString();
    }

    /**
     * @param n how many parties are needed
     * @return List with n random unique parties
     */
    private static List<Party> getRandomParties(int n) {
        List<Party> parties = new ArrayList<>();
        while(parties.size() < n) {
            Party p = Party.values()[random.nextInt(Party.values().length)];
            if(!parties.contains(p)) {
                parties.add(p);
            }
        }
        return parties;
    }

    /**
     * @return random party
     */
    private static Party getRandomParty(){
        return Party.values()[random.nextInt(Party.values().length)];
    }
}
