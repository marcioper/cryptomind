# Use a slim Java 21 runtime as base
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR into the container
COPY build/libs/user-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the service port (default 8080 for Spring Boot)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","app.jar"]
