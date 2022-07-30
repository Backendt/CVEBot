package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
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
import java.util.List;
import java.util.Optional;

import static fr.backendt.cvebot.TestConsts.CVE_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

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
        if(servers.size() > 1) {
            throw new IllegalStateException("WARNING ! Multiple servers detected ! Check the discord token");
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
    void postNewCVEsTest() {
        // GIVEN
        List<CVE> cveList = List.of(CVE_TEST);

        // WHEN
        assertThatNoException().isThrownBy(
                () -> service.postNewCVEs(cveList)
        );

        testGuild.getTextChannelsByName("cve-news", true).forEach(
                channel -> channel.delete().complete()
        );
    }

    @Test
    void getAllGuildsCveChannelsTest() {
        // GIVEN
        TextChannel channel = service.createServerCveChannel(testGuild).orElseThrow();

        List<Guild> servers = List.of(testGuild);

        Collection<TextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels(servers);

        // THEN
        assertThat(result).containsExactly(channel);

        channel.delete().complete();
    }

    @Test
    void getAllGuildsNonExistentCveChannelsTest() {
        // GIVEN
        List<Guild> servers = List.of(testGuild);

        int expectedSize = 1;
        Collection<TextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels(servers);

        // THEN
        assertThat(result).hasSize(expectedSize);

        result.stream().findFirst().orElseThrow().delete().complete();
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
    void createGuildCveChannelTest() {
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
