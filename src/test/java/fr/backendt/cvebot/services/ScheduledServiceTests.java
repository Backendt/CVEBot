package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVEMessage;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import static fr.backendt.cvebot.TestConsts.CVE_DATA_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScheduledServiceTests {

    private CVEService cveService;
    private DiscordService discordService;
    private ScheduledService service;

    @BeforeEach
    void initTest() {
        cveService = Mockito.mock(CVEService.class);
        discordService = Mockito.mock(DiscordService.class);

        service = new ScheduledService(cveService, discordService);
    }

    @Test
    void updateCVEsTest() {
        // GIVEN
        ZonedDateTime time = ZonedDateTime.now();

        TextChannel channel = Mockito.mock(TextChannel.class);
        Collection<TextChannel> channels = List.of(channel);
        Collection<CVEMessage> messages = List.of(new CVEMessage());

        ZonedDateTime result;

        when(cveService.getCVEAfterTime(any(ZonedDateTime.class))).thenReturn(CVE_DATA_TEST);
        when(discordService.getAllServersCveChannels()).thenReturn(channels);
        when(cveService.getPartitionedCVEMessages(anyList())).thenReturn(messages);
        // WHEN
        result = service.updateCVEs(time);

        // THEN
        verify(cveService).getCVEAfterTime(time);
        verify(cveService).getPartitionedCVEMessages(CVE_DATA_TEST.getCveList());
        verify(discordService).sendCVEMessagesToChannels(messages, channels);
        assertThat(result).isEqualTo(CVE_DATA_TEST.getRequestTime());
    }

}
