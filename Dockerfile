FROM mediasol/openjdk17-slim-jprofiler

ARG JAR_FILE=target/test-case-backend-0.0.1-SNAPSHOT.jar

# Copy the Spring Boot JAR file into the container
COPY ${JAR_FILE} app.jar

# Install MongoDB client tools if necessary
#RUN apk add --no-cache mongodb-tools

EXPOSE 8090
ENTRYPOINT ["java","-jar","app.jar"]