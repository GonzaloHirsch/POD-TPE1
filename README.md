# POD-TPE1

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
