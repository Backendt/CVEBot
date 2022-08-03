package fr.backendt.cvebot.services;

import fr.backendt.cvebot.exceptions.CVEException;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.models.CVEMessage;
import fr.backendt.cvebot.models.Severity;
import fr.backendt.cvebot.repositories.NVDRepository;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Log4j2
public class CVEService {

    private static final String CVSS_LINK_TEMPLATE = "https://nvd.nist.gov/vuln-metrics/cvss/v3-calculator?name=%s";
    private static final String CVE_LINK_TEMPLATE = "https://nvd.nist.gov/vuln/detail/%s";

    private final NVDRepository repository;

    public CVEService(NVDRepository repository) {
        this.repository = repository;
    }

    public CVEData getCVEAfterTime(ZonedDateTime time) {
        return repository.fetchCVEsAfterTime(time)
                .blockOptional()
                .orElseThrow(() -> new CVEException("Could not get response from NVD"));
    }

    public Collection<CVEMessage> getPartitionedCVEMessages(List<CVE> cveList) {
        List<List<CVE>> partitionedCVEs = ListUtils.partition(cveList, 10);
        return partitionedCVEs.stream()
                .map(this::getCVEMessage)
                .toList();
    }

    public CVEMessage getCVEMessage(List<CVE> cveList) {
        List<MessageEmbed> embeds = new ArrayList<>();
        Set<Severity> severitySet = new HashSet<>();

        for(CVE cve : cveList) {
            MessageEmbed embed = getCVEAsEmbedMessage(cve);
            embeds.add(embed);
            severitySet.add(cve.getSeverity());
        }

        return new CVEMessage(embeds, severitySet);
    }

    public MessageEmbed getCVEAsEmbedMessage(CVE cve) {
        String cveLink = CVE_LINK_TEMPLATE.formatted(cve.getName());
        String cvssLink = CVSS_LINK_TEMPLATE.formatted(cve.getName());

        String attachmentImage = "attachment://%s".formatted(cve.getSeverity().getImageName());

        return new EmbedBuilder()
                .setTitle(cve.getName(), cveLink)
                .setDescription(cve.getDescription())
                .setAuthor(cve.getSeverityString(), cvssLink, attachmentImage)
                .setColor(cve.getSeverity().getColor())
                .addField("Attack Vector", cve.getAttackVector(), false)
                .addField("Problem(s) Type", cve.getProblemsString(), false)
                .setFooter("Click the title for more information")
                .build();
    }

}
