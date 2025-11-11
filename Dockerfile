# Start from a lightweight Java 21 base image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file from target folder
COPY target/*.jar app.jar

# Expose ports
EXPOSE 8888
EXPOSE 9999

# Environment variables
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=default

# Run the Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]