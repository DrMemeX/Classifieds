FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn -pl classifieds-app -am clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build \
    /app/classifieds-app/target/classifieds-app-1.0.0-SNAPSHOT.jar \
    app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]