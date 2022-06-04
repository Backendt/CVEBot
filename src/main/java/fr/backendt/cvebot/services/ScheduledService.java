package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.repositories.NVDRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledService {

    private static final Logger LOGGER = LogManager.getLogger(ScheduledService.class);

    @Value("${cvebot.automatic-updates.disabled:false}")
    private boolean automaticUpdateDisabled;
    private ZonedDateTime lastCheck = ZonedDateTime.now().minusHours(2L);

    private final NVDRepository repository;
    private final DiscordService discordService;

    public ScheduledService(NVDRepository repository, DiscordService discordService) {
        this.repository = repository;
        this.discordService = discordService;
    }

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.HOURS)
    public void onAutomaticUpdate() {

        if(automaticUpdateDisabled) {
            LOGGER.warn("Automatic updates are disabled");
            return;
        }

        lastCheck = updateCVEs();
    }

    /**
     * Fetch CVEs and send them to discord servers
     * @return The time of the most recent CVE
     */
    public ZonedDateTime updateCVEs() {
        LOGGER.info("Updating CVEs...");

        // Fetch CVEs from NVD
        CVEData fetchedCVEs = fetchCVEs();

        // Send CVEs to Discord servers
        List<CVE> cveList = fetchedCVEs.getCveList();
        if(!cveList.isEmpty()) {
            discordService.postNewCVEs(cveList);
        } else {
            LOGGER.info("No CVEs to send !");
        }

        return fetchedCVEs.getRequestTime();
    }

    public CVEData fetchCVEs() {
        Optional<CVEData> response = repository.fetchCVEsAfterTime(lastCheck)
                .retry(3)
                .timeout(Duration.ofSeconds(10))
                .blockOptional();

        if(response.isEmpty()) {
            LOGGER.error("Could not get response from NVD");
            LOGGER.debug("Start time requested = {}", lastCheck);
            return new CVEData(List.of(), lastCheck);
        }

        return response.get();
    }

    public ZonedDateTime getLastCheck() {
        return this.lastCheck;
    }

}
