# Start with a base image containing Java and Maven
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY api/pom.xml .

# Download dependencies without running the tests (for faster build)
RUN mvn dependency:go-offline -B

# Copy the source code to the container
COPY api/src ./src

# Package the application to create the JAR file
RUN mvn clean package -DskipTests

# Start a new stage with only the JAR and JDK for a smaller image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage to the final image
COPY --from=build /app/target/api-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
