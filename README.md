# POD-TPE1

## Building
To build the project just run:
```
mvn clean install
```

## Running
At least three(3) terminals are going to be needed. These terminals are noted A, B, C, etc.

**NOTE**: This commands are meant to be run from the root of the project.

From terminal A run:
```
cd server/target/
tar -xzf POD-TPE1-server-1.0-SNAPSHOT-bin.tar.gz
cd POD-TPE1-server-1.0-SNAPSHOT
chmod u+x $(ls | egrep run-)
./run-registry
```

The condensed command is:
```
cd server/target/ && tar -xzf POD-TPE1-server-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-server-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-) && ./run-registry
```

From terminal B run:
```
cd server/target/POD-TPE1-server-1.0-SNAPSHOT
./run-server
```

The condensed command is:
```
cd server/target/POD-TPE1-server-1.0-SNAPSHOT && ./run-server
```

From terminal C run:
```
cd client/target/
tar -xzf POD-TPE1-client-1.0-SNAPSHOT-bin.tar.gz
cd POD-TPE1-client-1.0-SNAPSHOT
chmod u+x $(ls | egrep run-)
```

The condensed command is:
```
cd client/target/ && tar -xzf POD-TPE1-client-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-client-1.0-SNAPSHOT && chmod u+x $(ls | egrep run-)
```

## Command Examples
This examples are meant to be used from the directory:
```
client/target/POD-TPE1-client-1.0-SNAPSHOT
```

So from the root of the project run:
```
cd client/target/POD-TPE1-client-1.0-SNAPSHOT
```

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
./run-query -DserverAddress=127.0.0.1:1099 -DoutPath=../../../examples/national_result.csv
```

To check state results, run:
```
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=SAVANNAH -DoutPath=../../../examples/savannah_result.csv
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=TUNDRA -DoutPath=../../../examples/tundra_result.csv
./run-query -DserverAddress=127.0.0.1:1099 -Dstate=JUNGLE -DoutPath=../../../examples/jungle_result.csv
```

To check table results, run:
```
./run-query -DserverAddress=127.0.0.1:1099 -Did=1000 -DoutPath=../../../examples/table1000_result.csv
./run-query -DserverAddress=127.0.0.1:1099 -Did=1001 -DoutPath=../../../examples/table1001_result.csv
```

### Audit Client
```
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1000 -Dparty=TIGER
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1001 -Dparty=LYNX
./run-fiscal -DserverAddress=127.0.0.1:1099 -Did=1002 -Dparty=BUFFALO
```

## Authors

Second Semester of 2020 - ITBA

**Florencia Petrikovich** - fpetrikovich@itba.edu.ar

**Gonzalo Hirsch** - ghirsch@itba.edu.ar

**Marina Fuster** - mfuster@itba.edu.ar

**Gastón Lifschitz** - glifschitz@itba.edu.ar