package fr.backendt.cvebot.repositories;

import fr.backendt.cvebot.models.CVEData;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class NVDRepository {

    public static final DateTimeFormatter NVD_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS z");

    private static final String NVD_URL = "https://services.nvd.nist.gov/rest/json/cves/1.0";

    private final WebClient client;
    private final Clock clock;

    public NVDRepository() {
        this(NVD_URL, Clock.systemDefaultZone());
    }

    public NVDRepository(String url, Clock clock) {
        this.client = WebClient.create(url);
        this.clock = clock;
    }

    /**
     * Fetch all modified CVEs after the given time
     * @return A list with all modified CVEs, and the timestamp of the last CVE
     */
    public Mono<CVEData> fetchCVEsAfterTime(ZonedDateTime time) {

        String startTime = time.format(NVD_TIME_PATTERN);
        String endTime = ZonedDateTime.now(clock).format(NVD_TIME_PATTERN);

        return client.get()
                .uri(builder -> builder
                        .queryParam("modStartDate", startTime)
                        .queryParam("modEndDate", endTime)
                        .build())
                .retrieve()
                .bodyToMono(CVEData.class)
                .timeout(Duration.ofSeconds(20))
                .retry(3);
    }

}
