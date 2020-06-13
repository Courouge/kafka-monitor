FROM maven:alpine as mavenBuild
COPY pom.xml pom.xml
COPY src src
RUN mvn clean install

FROM adoptopenjdk/openjdk14:alpine-jre
COPY --from=mavenBuild ./target/App-1.0.0.jar ./
CMD ["java", "-jar", "/App-1.0.0.jar"]
