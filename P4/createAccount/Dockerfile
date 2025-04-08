FROM openjdk:17

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests

EXPOSE 8085

CMD ["java", "-jar", "target/createaccount-0.0.1-SNAPSHOT.jar", "--server.port=8085"]
