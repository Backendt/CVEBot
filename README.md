# CVE Bot

CVE Bot is a Discord bot written in Java, using Spring Boot and Javacord.

It uses the [National Vulnerability Database](https://nvd.nist.gov/) to get the latest CVE informations.

Every 2 hours, the bot will post the latest CVEs in a `#cve-news` channel.


## Running the bot

First, you need to [create the discord bot](https://ptb.discord.com/developers/docs/getting-started#creating-an-app). You will need its secret and id.

You can invite the bot before or after the installation with this link :
```bash
https://discord.com/oauth2/authorize?client_id="Your bot id"&permissions=3088&scope=bot 
```
(Replace "Your bot id" by your actual bot id, and remove the quotes)

### Docker
The simplest way to run the bot is [by using Docker](https://docs.docker.com/get-docker/).

You just have to run this command in the project folder:
```bash
docker build -t cvebot .
```

Then you can run it with this command:
```bash
docker run -e DISCORD_TOKEN="Your bot secret" -itd cvebot
```
(Replace "Your bot secret" by your actual secret)

### Manually

First, make sure you have Java 17 (or above) installed.

Now, you need to compile the project by running this command in the project folder:
```bash
./mvnw install -DskipTests
```

Then your can run it with this command:
```bash
DISCORD_TOKEN="Your bot secret" java -jar ./target/cve-bot-1.0.jar
```

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)