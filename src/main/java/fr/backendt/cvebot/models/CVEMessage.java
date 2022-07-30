package fr.backendt.cvebot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVEMessage {

    private List<MessageEmbed> embeds = new ArrayList<>();
    private Set<Severity> severities = new HashSet<>();

}
