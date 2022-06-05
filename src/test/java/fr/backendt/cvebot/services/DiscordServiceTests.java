package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static fr.backendt.cvebot.TestConsts.CVE_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class DiscordServiceTests {

    private static DiscordApi api;
    private static Server testServer;

    private DiscordService service;

    @BeforeAll
    static void setupDiscordApi() {
        String token = System.getenv("DISCORD_TOKEN");

        api = new DiscordApiBuilder()
                .setToken(token)
                .login().join();

        Collection<Server> servers = api.getServers();
        if(servers.size() > 1) {
            throw new IllegalStateException("WARNING ! Multiple servers detected ! Check the discord token");
        }
        testServer = servers.stream().findFirst().orElseThrow();
    }

    @AfterAll
    static void shutdownDiscordApi() {
        api.disconnect();
    }

    @BeforeEach
    void initTest() {
        service = new DiscordService(api);
    }

    @Test
    void postNewCVEsTest() {
        // GIVEN
        List<CVE> cveList = List.of(CVE_TEST);

        // WHEN
        assertThatNoException().isThrownBy(
                () -> service.postNewCVEs(cveList)
        );

        testServer.getTextChannelsByName("cve-news").forEach(
                channel -> channel.delete().join()
        );
    }

    @Test
    void getAllServersCveChannelsTest() {
        // GIVEN
        ServerTextChannel channel = service.createServerCveChannel(testServer).orElseThrow();

        List<Server> servers = List.of(testServer);

        Collection<ServerTextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels(servers);

        // THEN
        assertThat(result).containsExactly(channel);

        channel.delete().join();
    }

    @Test
    void getAllServersNonExistentCveChannelsTest() {
        // GIVEN
        List<Server> servers = List.of(testServer);

        int expectedSize = 1;
        Collection<ServerTextChannel> result;

        // WHEN
        result = service.getAllServersCveChannels(servers);

        // THEN
        assertThat(result).hasSize(expectedSize);

        result.stream().findFirst().orElseThrow().delete().join();
    }

    @Test
    void getServerCveChannelTest() {
        // GIVEN
        ServerTextChannel channel = service.createServerCveChannel(testServer).orElseThrow();

        Optional<ServerTextChannel> result;
        // WHEN
        result = service.getServerCveChannel(testServer);

        // THEN
        assertThat(result)
                .isNotEmpty()
                .contains(channel);

        result.get().delete().join();
    }

    @Test
    void getServerNonExistentCveChannelTest() {
        // GIVEN
        Optional<ServerTextChannel> result;
        // WHEN
        result = service.getServerCveChannel(testServer);

        // THEN
        assertThat(result).isNotEmpty();

        result.get().delete().join();
    }

    @Test
    void createServerCveChannelTest() {
        // GIVEN
        Optional<ServerTextChannel> result;

        // WHEN
        result = service.createServerCveChannel(testServer);

        // THEN
        assertThat(result).isNotEmpty();
        assertThat(result.get().canYouWrite()).isTrue();

        result.get().delete().join();
    }

}
