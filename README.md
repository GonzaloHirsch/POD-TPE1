# POD-TPE1

## Set Up
After you cloned the project, go to project directory:
```
mvn clean package
```
From terminal A run:
```
cd server/target/ && tar -xzf POD-TPE1-server-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-server-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-)
```
Start Registry:
```
./run-registry
```
From terminal B run:
```
cd server/target/POD-TPE1-server-1.0-SNAPSHOT/ && ./run-server
```
From terminal C run:
```
cd client/target/ && tar -xzf POD-TPE1-client-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-client-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-)
```

## Command Examples
### Vote Client
To run this, a _vote.csv_ file is used from the _/examples_ directory:
```
./run-vote -DserverAddress=127.0.0.1:1099 -DvotesPath=../../../examples/votes.csv
```

### Management Client
To OPEN the elections, run:
```
./run-management -DserverAddress=127.0.0.1:1099 -Daction=open
```

To CLOSE the elections, run:
```
./run-management -DserverAddress=127.0.0.1:1099 -Daction=close
```

To check STATUS of the elections, run:
```
./run-management -DserverAddress=127.0.0.1:1099 -Daction=state
```

### Query Client

To check national results, run:
```
./run-query -DserverAddress=127.0.0.1:1099 -DoutPath=national.csv
```

To check state results, run:
```
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=SAVANNAH -DoutPath=savannah.csv
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=TUNDRA -DoutPath=tundra.csv
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=JUNGLE -DoutPath=jungle.csv
```

To check table results, run:
```
./run-query -DserverAddress=127.0.0.1:1099 -Did=1000 -DoutPath=table1000.csv
./run-query -DserverAddress=127.0.0.1:1099 -Did=1001 -DoutPath=table1001.csv
```

### Audit Client
```
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1000 -Dparty=TIGER
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1001 -Dparty=LYNX
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1002 -Dparty=BUFFALO
```

## Fast Running Commands

From terminal A run:
```
cd server/target/ && tar -xzf POD-TPE1-server-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-server-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-)
```

In terminal B run:
```
cd client/target/ && tar -xzf POD-TPE1-client-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-client-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-)
```

Quickly move to directories
```
cd server/target/POD-TPE1-server-1.0-SNAPSHOT
cd client/target/POD-TPE1-client-1.0-SNAPSHOT
cd ../../../
```
