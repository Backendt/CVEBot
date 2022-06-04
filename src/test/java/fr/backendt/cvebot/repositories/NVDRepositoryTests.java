package fr.backendt.cvebot.repositories;

import fr.backendt.cvebot.TestConsts;
import fr.backendt.cvebot.models.CVEData;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static fr.backendt.cvebot.TestConsts.CVE_DATA_TEST;
import static fr.backendt.cvebot.repositories.NVDRepository.NVD_TIME_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class NVDRepositoryTests {

    private MockWebServer mockServer;

    private Clock clock;
    private NVDRepository repository;

    @BeforeEach
    void initTest() {
        mockServer = new MockWebServer();

        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        repository = new NVDRepository("http://localhost:9090", clock);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void fetchCVEsAfterTimeTest() throws IOException, InterruptedException {
        // GIVEN
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime startTime = now.minusHours(2);

        String modStartDate = startTime.format(NVD_TIME_PATTERN).replace(" +", "%20+");
        String modEndDate = now.format(NVD_TIME_PATTERN).replace(" +", "%20+");

        String expectedPath = "/?modStartDate=%s&modEndDate=%s"
                .formatted(modStartDate, modEndDate);

        CVEData result;
        RecordedRequest request;

        String response = TestConsts.getJson("response_example");

        mockServer.enqueue(new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(response)
        );
        mockServer.start(9090);
        // WHEN
        result = repository.fetchCVEsAfterTime(startTime).block();
        request = mockServer.takeRequest();

        // THEN
        assertThat(result).isEqualTo(CVE_DATA_TEST);
        assertThat(request.getPath()).isEqualTo(expectedPath);
    }

}
