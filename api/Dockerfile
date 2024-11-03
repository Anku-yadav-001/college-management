# Stage 1: Build the JAR file
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY api/pom.xml .

# Download dependencies to save time during builds
RUN mvn dependency:go-offline -B

# Copy the source code and build the JAR file
COPY api/src ./src

# Compile the code and create the JAR file, skipping tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Create a lean production image with only the JDK and JAR file
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the previous stage
COPY --from=build /app/target/api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your app will run on (change this if needed)
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
