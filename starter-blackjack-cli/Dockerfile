FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src/ src/
RUN ./mvnw -q package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/blackjack-cli-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
