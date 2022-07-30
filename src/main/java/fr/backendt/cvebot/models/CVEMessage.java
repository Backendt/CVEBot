package fr.backendt.cvebot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVEMessage {

    private MessageEmbed embed;
    private Severity severity;

}
