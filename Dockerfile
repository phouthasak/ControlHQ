FROM maven:3.8.5-openjdk-17 as build

WORKDIR /app

COPY pom.xml
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]