# POD-TPE1

## Fast Running Commands

From terminal A run:
```
cd server/target/ && tar -xzf POD-TPE1-server-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-server-1.0-SNAPSHOT && chmod u+x $(ls | egrep .sh)
```

In terminal B run:
```
cd client/target/ && tar -xzf POD-TPE1-client-1.0-SNAPSHOT-bin.tar.gz && cd POD-TPE1-client-1.0-SNAPSHOT && chmod u+x $(ls | egrep .sh)
```

Quickly move to directories
```
cd server/target/POD-TPE1-server-1.0-SNAPSHOT
cd client/target/POD-TPE1-client-1.0-SNAPSHOT
cd ../../../
```
