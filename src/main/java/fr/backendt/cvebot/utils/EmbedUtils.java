package fr.backendt.cvebot.utils;

import fr.backendt.cvebot.models.CVE;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class EmbedUtils {

    private EmbedUtils() {}

    private static final String CVSS_LINK_TEMPLATE = "https://nvd.nist.gov/vuln-metrics/cvss/v3-calculator?name=%s";
    private static final String CVE_LINK_TEMPLATE = "https://nvd.nist.gov/vuln/detail/%s";

    public static EmbedBuilder createCveEmbed(CVE cve) {
        String cveLink = CVE_LINK_TEMPLATE.formatted(cve.getName());
        String cvssLink = CVSS_LINK_TEMPLATE.formatted(cve.getName());

        return new EmbedBuilder()
                .setTitle(cve.getName())
                .setUrl(cveLink)
                .setDescription(cve.getDescription())
                .setAuthor(cve.getSeverityString(), cvssLink, cve.getSeverity().getImageFile())
                .setColor(cve.getSeverity().getColor())
                .addField("Attack Vector", cve.getAttackVector())
                .addField("Problem(s) Type", cve.getProblemsString())
                .setFooter("Click the title for more information");
    }

}
