package fr.backendt.cvebot.configuration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;

@Configuration
public class DiscordApiConfiguration {

    @Value("${cvebot.discord.discord_token}")
    private String token;

    @Bean
    public JDA getJDA() throws LoginException {
        return JDABuilder.createDefault(token)
                .build();
    }

}
