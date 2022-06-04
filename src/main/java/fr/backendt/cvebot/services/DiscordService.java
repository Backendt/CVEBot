package fr.backendt.cvebot.services;

import fr.backendt.cvebot.models.CVE;
import org.javacord.api.DiscordApi;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordService {

    private final DiscordApi api;

    public DiscordService(DiscordApi api) {
        this.api = api;
    }

    /**
     * Send CVEs to all discord servers
     * @param cveList The CVEs to send
     */
    @Async
    public void postNewCVEs(List<CVE> cveList) {
        // TODO Loop over servers, find or create "cve-news" channel, loop over CVEs, post them as embeds
    }

}
