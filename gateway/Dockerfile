###########################################################
# BUILD
###########################################################
FROM maven:3-openjdk-11 AS build

WORKDIR /app/build

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src/ ./src/

RUN mvn package -Dmaven.test.skip
###########################################################

###########################################################
# RUN
###########################################################
FROM openjdk:11

WORKDIR /app/run

COPY --from=build /app/build/target/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
###########################################################