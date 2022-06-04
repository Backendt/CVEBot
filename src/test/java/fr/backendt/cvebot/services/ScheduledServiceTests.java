package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.repositories.NVDRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

import static fr.backendt.cvebot.TestConsts.CVE_DATA_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ScheduledServiceTests {

    private NVDRepository repository;
    private DiscordService discordService;
    private ScheduledService service;

    @BeforeEach
    void initTest() {
        repository = Mockito.mock(NVDRepository.class);
        discordService = Mockito.mock(DiscordService.class);

        service = new ScheduledService(repository, discordService);
    }

    @Test
    void updateCVEsTest() {
        // GIVEN
        ZonedDateTime time = service.getLastCheck();

        Mono<CVEData> data = Mono.fromCallable(() -> CVE_DATA_TEST);

        ZonedDateTime result;

        when(repository.fetchCVEsAfterTime(time)).thenReturn(data);
        // WHEN
        result = service.updateCVEs();

        // THEN
        verify(repository, times(1)).fetchCVEsAfterTime(time);
        verify(discordService, times(1)).postNewCVEs(CVE_DATA_TEST.getCveList());

        assertThat(result).isEqualTo(CVE_DATA_TEST.getRequestTime());
    }

    @Test
    void fetchCVEsTest() {
        // GIVEN
        ZonedDateTime time = service.getLastCheck();

        Mono<CVEData> data = Mono.fromCallable(() -> CVE_DATA_TEST);

        CVEData result;

        when(repository.fetchCVEsAfterTime(time)).thenReturn(data);
        // WHEN
        result = service.fetchCVEs();

        // THEN
        verify(repository, times(1)).fetchCVEsAfterTime(time);
        assertThat(result).isEqualTo(CVE_DATA_TEST);
    }

    @Test
    void fetchEmptyCVEsTest() {
        // GIVEN
        ZonedDateTime time = service.getLastCheck();

        Mono<CVEData> data = Mono.empty();

        CVEData expected = new CVEData(List.of(), time);
        CVEData result;

        when(repository.fetchCVEsAfterTime(time)).thenReturn(data);
        // WHEN
        result = service.fetchCVEs();

        // THEN
        verify(repository, times(1)).fetchCVEsAfterTime(time);
        assertThat(result).isEqualTo(expected);
    }

}
