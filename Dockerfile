FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw -q -v

COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar chrona-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/chrona-backend.jar"]
