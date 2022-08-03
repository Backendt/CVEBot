package fr.backendt.cvebot.services;

import fr.backendt.cvebot.exceptions.CVEException;
import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.models.CVEMessage;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class ScheduledService {

    @Value("${cvebot.automatic-updates.disabled:false}")
    private boolean automaticUpdateDisabled;
    private ZonedDateTime lastCheck = ZonedDateTime.now().minusHours(2L);

    private final CVEService cveService;
    private final DiscordService discordService;

    public ScheduledService(CVEService cveService, DiscordService discordService) {
        this.cveService = cveService;
        this.discordService = discordService;
    }

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.HOURS)
    public void onAutomaticUpdate() {
        if(automaticUpdateDisabled) {
            log.warn("Automatic updates are disabled");
            return;
        }

        log.info("Updating CVEs...");
        try {
            this.lastCheck = updateCVEs(lastCheck);
        } catch(CVEException exception) {
            log.error("An error occurred when updating CVEs", exception);
        }
    }

    /**
     * Fetch CVEs and send them to discord servers
     * @return The time of the most recent CVE
     */
    public ZonedDateTime updateCVEs(ZonedDateTime lastCheck) throws CVEException {
        CVEData fetchedCVEs = cveService.getCVEAfterTime(lastCheck);
        Collection<TextChannel> cveChannels = discordService.getAllServersCveChannels();
        Collection<CVEMessage> cveMessages = cveService.getPartitionedCVEMessages(fetchedCVEs.getCveList());

        discordService.sendCVEMessagesToChannels(cveMessages, cveChannels);
        log.info("Sent {} CVE(s) to {} channel(s)", fetchedCVEs.getCveList().size(), cveChannels.size());
        return fetchedCVEs.getRequestTime();
    }

}
