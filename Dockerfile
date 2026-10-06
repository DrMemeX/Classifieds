FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY classifieds-app/pom.xml classifieds-app/pom.xml
COPY classifieds-audit-app/pom.xml classifieds-audit-app/pom.xml
COPY classifieds-kafka-contract/pom.xml classifieds-kafka-contract/pom.xml

ARG MODULE

RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl ${MODULE} -am dependency:go-offline

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl ${MODULE} -am clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

ARG MODULE

COPY --from=build \
    /app/${MODULE}/target/${MODULE}-1.0.0-SNAPSHOT.jar \
    app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]