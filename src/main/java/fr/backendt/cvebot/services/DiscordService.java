package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEMessage;
import fr.backendt.cvebot.models.Severity;
import fr.backendt.cvebot.utils.EmbedUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DiscordService {

    private static final Logger LOGGER = LogManager.getLogger(DiscordService.class);
    private static final String CHANNEL_NAME = "cve-news";

    private final JDA jda;

    public DiscordService(JDA jda) {
        this.jda = jda;
    }

    /**
     * Send CVEs to all discord servers
     * @param cveList The CVEs to send
     */
    @Async
    public void postNewCVEs(List<CVE> cveList) {
        Collection<Guild> servers = jda.getGuilds();
        LOGGER.info("Sending {} CVE(s) to {} server(s)", cveList.size(), servers.size());

        // Get all CVE news text channels
        Collection<TextChannel> channels = getAllServersCveChannels(servers);
        if(channels.isEmpty()) {
            return;
        }

        // Create Embed messages from CVEs
        List<CVEMessage> cveAsEmbeds = cveList.stream()
                .map(EmbedUtils::createCveEmbed)
                .toList();

        List<List<CVEMessage>> partitionedEmbeds = ListUtils.partition(cveAsEmbeds, 10); // Send in groups of 10 embeds

        // Send embeds in all channels
        for(TextChannel channel : channels) {
            for(List<CVEMessage> messages : partitionedEmbeds) {

                List<MessageEmbed> embeds = new ArrayList<>();
                Set<Severity> severities = new HashSet<>();
                for(CVEMessage cve : messages) {
                    embeds.add(cve.getEmbed());
                    severities.add(cve.getSeverity());
                }

                MessageAction message = channel.sendMessageEmbeds(embeds);
                for(Severity severity : severities) {
                    message = message.addFile(severity.getImageFile(), severity.getImageName());
                }

                message.queue(null, error -> LOGGER.error("Could not send embed to a channel", error));
            }
        }
    }

    /**
     * Get cve-news channel of all given servers.
     * Create the text channel if it doesn't exist.
     * Servers in which channels couldn't be fetched nor created will not have its text channel in the collection
     * @param servers Servers to get the channels from
     * @return The cve-news channels
     */
    public Collection<TextChannel> getAllServersCveChannels(Collection<Guild> servers) {
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
    public Optional<TextChannel> getServerCveChannel(Guild server) {
        return server.getTextChannelsByName(CHANNEL_NAME, true).stream()
                .findFirst()
                .or(() -> this.createServerCveChannel(server));
    }

    /**
     * Create a cve-news text channel in the given server.
     * Will create the channel even if one already exist.
     * @param server The server in which the channel needs to be created
     * @return The created channel. Empty if it couldn't be created
     */
    public Optional<TextChannel> createServerCveChannel(Guild server) {
        try {
            TextChannel channel = server.createTextChannel(CHANNEL_NAME).complete();
            return Optional.of(channel);
        } catch(RuntimeException exception) {
            LOGGER.info("Could not create cve-news channel in server : {}", server.getName());
            return Optional.empty();
        }
    }

}
