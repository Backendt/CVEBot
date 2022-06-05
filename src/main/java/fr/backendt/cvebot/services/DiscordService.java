package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.utils.EmbedUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class DiscordService {

    private static final Logger LOGGER = LogManager.getLogger(DiscordService.class);
    private static final String CHANNEL_NAME = "cve-news";

    private final DiscordApi api;

    public DiscordService(DiscordApi api) {
        this.api = api;
    }

    /**
     * Send CVEs to all discord servers
     * @param cveList The CVEs to send
     */
    @Async
    public void postNewCVEs(List<CVE> cveList) {
        Collection<Server> servers = api.getServers();
        LOGGER.info("Sending {} CVE(s) to {} server(s)", cveList.size(), servers.size());

        // Get all CVE news text channels
        Collection<ServerTextChannel> channels = getAllServersCveChannels(servers);

        if(channels.isEmpty()) {
            return;
        }

        // Create Embed messages from CVEs
        List<EmbedBuilder> cveAsEmbeds = cveList.stream()
                .map(EmbedUtils::createCveEmbed)
                .toList();

        // Send embeds in all channels
        channels.forEach(channel ->
                channel.sendMessage(cveAsEmbeds)
                        .exceptionally(throwable -> {
                            LOGGER.error("Could not send embed to a channel", throwable);
                            return null;
                        }));
    }

    /**
     * Get cve-news channel of all given servers.
     * Create the text channel if it doesn't exist.
     * Servers in which channels couldn't be fetched nor created will not have its text channel in the collection
     * @param servers Servers to get the channels from
     * @return The cve-news channels
     */
    public Collection<ServerTextChannel> getAllServersCveChannels(Collection<Server> servers) {
        return servers.stream()
                .map(this::getServerCveChannel)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Get cve-news channel of the given server.
     * Create the text channel if it doesn't exist
     * @param server The server to get the channel from
     * @return The cve-news channel. Empty if it couldn't be fetched or created
     */
    public Optional<ServerTextChannel> getServerCveChannel(Server server) {
        return server.getTextChannelsByNameIgnoreCase(CHANNEL_NAME).stream()
                .findFirst()
                .or(() -> this.createServerCveChannel(server));
    }

    /**
     * Create a cve-news text channel in the given server.
     * Will create the channel even if one already exist.
     * @param server The server in which the channel needs to be created
     * @return The created channel. Empty if it couldn't be created
     */
    public Optional<ServerTextChannel> createServerCveChannel(Server server) {
        if(!server.canYouCreateChannels()) {
            LOGGER.warn("Could not create cve-news channel in server : {}", server.getName());
            return Optional.empty();
        }

        ServerTextChannel channel = server.createTextChannelBuilder()
                .setName(CHANNEL_NAME)
                .create()
                .join();

        return Optional.of(channel);
    }

}
