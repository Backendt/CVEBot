package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVEMessage;
import fr.backendt.cvebot.models.Severity;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class DiscordService {

    private static final String CHANNEL_NAME = "cve-news";

    private final JDA jda;

    public DiscordService(JDA jda) {
        this.jda = jda;
    }


    public void sendCVEMessagesToChannels(Collection<CVEMessage> messages, Collection<TextChannel> channels) {
        for(TextChannel channel : channels) {
            for(CVEMessage message : messages) {
                sendCVEMessageToChannel(message, channel);
            }
        }
    }

    public void sendCVEMessageToChannel(CVEMessage message, TextChannel channel) {
        List<MessageEmbed> embeds = message.getEmbeds();
        Set<Severity> severities = message.getSeverities();

        MessageAction action = channel.sendMessageEmbeds(embeds);
        for(Severity severity : severities) {
            action = action.addFile(
                    severity.getImageFile(),
                    severity.getImageName()
            );
        }

        action.queue(null, error -> log.error("Could not send embed to a channel", error));
    }

    /**
     * Get cve-news channel of all servers.
     * Create the text channel if it doesn't exist.
     * Servers in which channels couldn't be fetched nor created will not have its text channel in the collection
     * @return The cve-news channels
     */
    public Collection<TextChannel> getAllServersCveChannels() {
        List<Guild> servers = jda.getGuilds();

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
                .filter(TextChannel::canTalk)
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
            log.info("Could not create cve-news channel in server : {}", server.getName());
            return Optional.empty();
        }
    }

}
