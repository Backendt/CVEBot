FROM openjdk:17

RUN ["useradd", "-ms", "/bin/sh", "cvebot"]
USER cvebot

WORKDIR /home/cvebot
COPY ./target/cvebot-1.0.jar .

ENV DISCORD_TOKEN="Put your bot token here !"

CMD ["java", "-jar", "cvebot-1.0.jar"]
