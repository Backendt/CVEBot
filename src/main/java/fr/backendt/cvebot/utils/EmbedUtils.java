package fr.backendt.cvebot.utils;

import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedUtils {

    private EmbedUtils() {}

    private static final String CVSS_LINK_TEMPLATE = "https://nvd.nist.gov/vuln-metrics/cvss/v3-calculator?name=%s";
    private static final String CVE_LINK_TEMPLATE = "https://nvd.nist.gov/vuln/detail/%s";

    public static CVEMessage createCveEmbed(CVE cve) {
        String cveLink = CVE_LINK_TEMPLATE.formatted(cve.getName());
        String cvssLink = CVSS_LINK_TEMPLATE.formatted(cve.getName());

        String attachmentImage = "attachment://%s".formatted(cve.getSeverity().getImageName());

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(cve.getName(), cveLink)
                .setDescription(cve.getDescription())
                .setAuthor(cve.getSeverityString(), cvssLink, attachmentImage)
                .setColor(cve.getSeverity().getColor())
                .addField("Attack Vector", cve.getAttackVector(), false)
                .addField("Problem(s) Type", cve.getProblemsString(), false)
                .setFooter("Click the title for more information")
                .build();

        return new CVEMessage(embed, cve.getSeverity());
    }

}
