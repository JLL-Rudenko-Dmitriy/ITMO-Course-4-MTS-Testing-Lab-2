FROM maven:3.9-eclipse-temurin-21
LABEL authors="dmrudenkovl"

WORKDIR /app
VOLUME /app/target

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

CMD ["mvn", "test"]