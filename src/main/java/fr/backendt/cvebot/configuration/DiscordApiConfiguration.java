package fr.backendt.cvebot.configuration;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordApiConfiguration {

    @Value("${cvebot.discord.discord_token}")
    private String token;

    @Bean
    public DiscordApi discordApi() {
        return new DiscordApiBuilder()
                .setToken(token)
                .login().join();
    }

}
