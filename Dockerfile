FROM openjdk:17
EXPOSE 8082
ADD target/kaddem-0.0.1-SNAPSHOT.jar Kaddem.jar
ENTRYPOINT ["java","-jar","Kaddem.jar"]