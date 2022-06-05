FROM openjdk:17-alpine as builder

WORKDIR cvebot
COPY . .

RUN ./mvnw install -DskipTests


FROM openjdk:17-alpine

RUN ["adduser", "--disabled-password", "cvebot"]
USER cvebot
WORKDIR cvebot

COPY --from=builder /cvebot/target/cvebot-1.0.jar .

CMD ["java", "-jar", "/cvebot/cvebot-1.0.jar"]
