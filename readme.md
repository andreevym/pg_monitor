[![Build Status](https://travis-ci.org/theborakompanioni/pg_monitor.svg?branch=master)](https://travis-ci.org/theborakompanioni/pg_monitor)


pg_monitor
====
A postgres metrics reporter and monitoring tool

This information is available out of the box:
- Cache hit rates
- System activity
- COMMITs and ROLLBACKs
- Sequentual scans details
- Index scan details
- Lock statistics

TODO:
- Optimizer information
- Open database connections
- Disk I/O
- Checkpointing behavior


### Package
```
> mvn clean package
```

### Run
```
> java -jar target/pgmonitor-${version}.jar
```

#### Docker
```
> docker run -t -i -p 8080:8080 --link my-postgres-container:postgres --link my-influx-container:influxdb tbk/pgmonitor
```
