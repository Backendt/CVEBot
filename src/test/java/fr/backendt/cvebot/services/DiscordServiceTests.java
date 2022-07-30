package fr.backendt.cvebot.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DiscordServiceTests {

    private static JDA jda;
    private static Guild testGuild;

    private DiscordService service;

    @BeforeAll
    static void setupDiscordApi() throws LoginException, InterruptedException {
        String token = System.getenv("DISCORD_TOKEN");

        jda = JDABuilder.createDefault(token)
                .build()
                .awaitReady();

        Collection<Guild> servers = jda.getGuilds();
        if(servers.size() < 1) {
            throw new IllegalStateException("The bot needs to be in at least 1 server to run theses tests");
        }
        testGuild = servers.stream().findFirst().orElseThrow();
    }

    @AfterAll
    static void shutdownDiscordApi() {
        jda.shutdown();
    }

    @BeforeEach
    void initTest() {
        service = new DiscordService(jda);
    }

    @Test
    void getAllGuildsCveChannelsTest() {
        // GIVEN
        TextChannel channel = service.createServerCveChannel(testGuild).orElseThrow();

        Collection<TextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels();

        // THEN
        assertThat(result).containsExactly(channel);

        channel.delete().complete();
    }

    @Test
    void getAllGuildsNonExistentCveChannelsTest() {
        // GIVEN
        Collection<TextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels();

        // THEN
        assertThat(result).isNotEmpty();

        result.forEach(channel -> channel.delete().complete());
    }

    @Test
    void getGuildCveChannelTest() {
        // GIVEN
        TextChannel channel = service.createServerCveChannel(testGuild).orElseThrow();

        Optional<TextChannel> result;
        // WHEN
        result = service.getServerCveChannel(testGuild);

        // THEN
        assertThat(result)
                .isNotEmpty()
                .contains(channel);

        result.get().delete().complete();
    }

    @Test
    void getGuildNonExistentCveChannelTest() {
        // GIVEN
        Optional<TextChannel> result;
        // WHEN
        result = service.getServerCveChannel(testGuild);

        // THEN
        assertThat(result).isNotEmpty();

        result.get().delete().complete();
    }

    @Test
    void createServerCveChannelTest() {
        // GIVEN
        Optional<TextChannel> result;

        // WHEN
        result = service.createServerCveChannel(testGuild);

        // THEN
        assertThat(result).isNotEmpty();
        assertThat(result.get().canTalk()).isTrue();

        result.get().delete().complete();
    }

}
