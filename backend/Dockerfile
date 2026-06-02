# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy everything from the repository root into the container
COPY . .

# Move into the backend directory where pom.xml actually lives, then build
WORKDIR /app/backend
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built .jar file from the backend target folder
COPY --from=build /app/backend/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]