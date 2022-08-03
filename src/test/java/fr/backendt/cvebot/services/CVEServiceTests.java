package fr.backendt.cvebot.services;

import fr.backendt.cvebot.exceptions.CVEException;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.models.CVEMessage;
import fr.backendt.cvebot.models.Severity;
import fr.backendt.cvebot.repositories.NVDRepository;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static fr.backendt.cvebot.TestConsts.CVE_EMBED_TEST;
import static fr.backendt.cvebot.TestConsts.CVE_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CVEServiceTests {

    private NVDRepository repository;
    private CVEService service;

    @BeforeEach
    void initTest() {
        repository = Mockito.mock(NVDRepository.class);
        service = new CVEService(repository);
    }

    @Test
    void getCVEAfterTimeTest() {
        // GIVEN
        ZonedDateTime time = ZonedDateTime.now().minusHours(1);
        CVEData data = new CVEData();
        Mono<CVEData> mono = Mono.just(data);

        CVEData result;

        when(repository.fetchCVEsAfterTime(any())).thenReturn(mono);
        // WHEN
        result = service.getCVEAfterTime(time);

        // THEN
        assertThat(result).isEqualTo(data);
        verify(repository).fetchCVEsAfterTime(time);
    }

    @Test
    void throwOnEmptyCVEAfterTimeTest() {
        // GIVEN
        ZonedDateTime time = ZonedDateTime.now().minusHours(1);
        Mono<CVEData> mono = Mono.empty();

        when(repository.fetchCVEsAfterTime(any())).thenReturn(mono);
        // WHEN
        assertThatExceptionOfType(CVEException.class)
                .isThrownBy(() -> service.getCVEAfterTime(time));

        // THEN
        verify(repository).fetchCVEsAfterTime(time);
    }

    @Test
    void getPartitionedCVEMessagesTest() {
        // GIVEN
        List<CVE> cveList = List.of(CVE_TEST);

        List<MessageEmbed> expectedEmbeds = List.of(CVE_EMBED_TEST);
        Set<Severity> expectedSeverities = Set.of(Severity.CRITICAL);
        CVEMessage expected = new CVEMessage(expectedEmbeds, expectedSeverities);

        Collection<CVEMessage> result;

        // WHEN
        result = service.getPartitionedCVEMessages(cveList);

        // THEN
        assertThat(result).contains(expected);
    }

    @Test
    void getCVEMessageTest() {
        // GIVEN
        List<CVE> cveList = List.of(CVE_TEST);

        List<MessageEmbed> expectedEmbeds = List.of(CVE_EMBED_TEST);
        Set<Severity> expectedSeverities = Set.of(Severity.CRITICAL);
        CVEMessage expected = new CVEMessage(expectedEmbeds, expectedSeverities);

        CVEMessage result;

        // WHEN
        result = service.getCVEMessage(cveList);

        // THEN
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getCVEAsEmbedMessageTest() {
        // GIVEN
        MessageEmbed result;

        // WHEN
        result = service.getCVEAsEmbedMessage(CVE_TEST);

        // THEN
        assertThat(result).isEqualTo(CVE_EMBED_TEST);
    }

}
