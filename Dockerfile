# Build stage
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
EXPOSE 9999
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=default
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]