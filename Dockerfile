FROM openjdk:11.0.12-jre
COPY target/match-service-0.0.1-SNAPSHOT.jar /opt/match-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java",  "-XX:+UseParallelGC", "-Xms2048m", "-Xmx4096m", "-XX:MetaspaceSize=2048m", "-XX:+UnlockExperimentalVMOptions",  "-Djava.net.preferIPv4Stack=true", "-jar","/opt/match-service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080