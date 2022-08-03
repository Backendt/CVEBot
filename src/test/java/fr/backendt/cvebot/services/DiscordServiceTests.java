package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVEMessage;
import fr.backendt.cvebot.models.Severity;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DiscordServiceTests {

    private JDA jda;
    private DiscordService service;

    private Guild server;
    private TextChannel channel;
    private ChannelAction<TextChannel> action;

    @BeforeEach
    void initTest() {
        jda = Mockito.mock(JDA.class);
        service = new DiscordService(jda);

        server = Mockito.mock(Guild.class);
        channel = Mockito.mock(TextChannel.class);
        action = Mockito.mock(ChannelAction.class);
    }

    @Test
    void sendCVEMessagesToChannelsTest() {
        // GIVEN
        MessageEmbed embed = Mockito.mock(MessageEmbed.class);
        List<MessageEmbed> embeds = List.of(embed);
        Set<Severity> severities = Set.of(Severity.UNKNOWN);

        CVEMessage message = new CVEMessage(embeds, severities);
        MessageAction messageAction = Mockito.mock(MessageAction.class);

        List<CVEMessage> messages = List.of(message);
        List<TextChannel> channels = List.of(channel);

        when(channel.sendMessageEmbeds(anyCollection())).thenReturn(messageAction);
        when(messageAction.addFile(any(InputStream.class), anyString())).thenReturn(messageAction);
        // WHEN
        service.sendCVEMessagesToChannels(messages, channels);

        // THEN
        verify(channel).sendMessageEmbeds(embeds);
        verify(messageAction).addFile(any(InputStream.class), anyString());
        verify(messageAction).queue(any(), any());
    }

    @Test
    void sendEmptyCVEMessagesToChannelsTest() {
        // GIVEN
        List<CVEMessage> messages = List.of(new CVEMessage());
        List<TextChannel> channels = List.of(channel);

        // WHEN
        service.sendCVEMessagesToChannels(messages, channels);

        // THEN
        verify(channel, never()).sendMessageEmbeds(anyCollection());
    }

    @Test
    void sendCVEMessageToChannelTest() {
        // GIVEN
        MessageEmbed embed = Mockito.mock(MessageEmbed.class);
        List<MessageEmbed> embeds = List.of(embed);
        Set<Severity> severities = Set.of(Severity.UNKNOWN, Severity.CRITICAL);

        CVEMessage message = new CVEMessage(embeds, severities);
        MessageAction messageAction = Mockito.mock(MessageAction.class);

        when(channel.sendMessageEmbeds(anyCollection())).thenReturn(messageAction);
        when(messageAction.addFile(any(InputStream.class), anyString())).thenReturn(messageAction);
        // WHEN
        service.sendCVEMessageToChannel(message, channel);

        // THEN
        verify(channel).sendMessageEmbeds(embeds);
        verify(messageAction, times(2)).addFile(any(InputStream.class), anyString());
        verify(messageAction).queue(any(), any());
    }

    @Test
    void getAllServersCveChannelsTest() {
        // GIVEN
        List<Guild> guilds = List.of(server);

        List<TextChannel> channels = List.of(channel);
        Collection<TextChannel> result;

        when(jda.getGuilds()).thenReturn(guilds);
        when(server.getTextChannelsByName(anyString(), anyBoolean())).thenReturn(channels);
        when(channel.canTalk()).thenReturn(true);
        // WHEN
        result = service.getAllServersCveChannels();

        // THEN
        assertThat(result).contains(channel);
    }

    @Test
    void getNoServersCveChannelsTest() {
        // GIVEN
        Collection<TextChannel> result;

        when(jda.getGuilds()).thenReturn(List.of());
        // WHEN
        result = service.getAllServersCveChannels();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void getServerExistentCveChannelTest() {
        // GIVEN
        List<TextChannel> channels = List.of(channel);

        Optional<TextChannel> result;

        when(channel.canTalk()).thenReturn(true);
        when(server.getTextChannelsByName(anyString(), anyBoolean())).thenReturn(channels);
        // WHEN
        result = service.getServerCveChannel(server);

        // THEN
        assertThat(result).contains(channel);
        verify(server, never()).createTextChannel(anyString());
    }

    @Test
    void getServerNonAuthorizedCveChannelTest() {
        // GIVEN
        TextChannel nonAuthorizedChannel = Mockito.mock(TextChannel.class);
        List<TextChannel> channels = List.of(nonAuthorizedChannel);

        Optional<TextChannel> result;

        when(nonAuthorizedChannel.canTalk()).thenReturn(false);
        when(server.getTextChannelsByName(anyString(), anyBoolean())).thenReturn(channels);
        when(server.createTextChannel(anyString())).thenReturn(action);
        when(action.complete()).thenReturn(channel);
        // WHEN
        result = service.getServerCveChannel(server);

        // THEN
        assertThat(result).contains(channel);
        verify(server).createTextChannel(anyString());
    }

    @Test
    void getServerNonExistentCveChannelTest() {
        // GIVEN
        Optional<TextChannel> result;

        when(server.getTextChannelsByName(anyString(), anyBoolean())).thenReturn(List.of());
        when(server.createTextChannel(anyString())).thenReturn(action);
        when(action.complete()).thenReturn(channel);
        // WHEN
        result = service.getServerCveChannel(server);

        // THEN
        assertThat(result).contains(channel);
        verify(server).createTextChannel(anyString());
    }

    @Test
    void createServerCveChannelTest() {
        // GIVEN
        Optional<TextChannel> result;

        when(server.createTextChannel(anyString())).thenReturn(action);
        when(action.complete()).thenReturn(channel);
        // WHEN
        result = service.createServerCveChannel(server);

        // THEN
        assertThat(result).contains(channel);

        verify(action).complete();
    }

    @Test
    void createServerCveChannelWithoutPermissionTest() {
        // GIVEN
        Optional<TextChannel> result;

        when(server.createTextChannel(anyString())).thenThrow(new RuntimeException("Error !"));
        // WHEN
        result = service.createServerCveChannel(server);

        // THEN
        assertThat(result).isEmpty();
    }

}
