# Build stage
FROM maven:3.8.1-openjdk-17 AS builder

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application without running tests
RUN mvn clean package -DskipTests

# Testing stage
FROM maven:3.8.1-openjdk-17 AS tester

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the Maven project files and the built application from the build stage
COPY pom.xml .
COPY src ./src
COPY --from=builder /usr/src/app/target /usr/src/app/target

# Run all tests (unit and integration)
RUN mvn test

# Runtime stage
FROM eclipse-temurin:17-jre AS runtime

# Set the working directory inside the container
WORKDIR /usr/src/app

# Expose the application port
EXPOSE 8080

# Copy the JAR from the build stage
COPY --from=builder /usr/src/app/target/*.jar /usr/src/app/app.jar

# Entrypoint and command to run the application
ENTRYPOINT ["java", "-jar", "/usr/src/app/app.jar"]
