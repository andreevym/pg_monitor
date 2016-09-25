Postrics
====
A postgres metrics reporter and monitoring tool

### Package
```
> mvn clean package
```

### Run
```
> java -jar target/vertx-rx-springboot-${version}.jar
```

#### Docker
```
> docker run -t -i -p 8080:8080 --link my-postgres-container:postgres --link my-influx-container:influxdb tbk/postgres-metrics
```