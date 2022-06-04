package fr.backendt.cvebot.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.backendt.cvebot.deserializers.CVEDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@JsonDeserialize(using = CVEDeserializer.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVE {

    private String name;
    private String description;

    private Severity severity;
    private double severityScore;

    private Set<String> problemTypes;
    private String attackVector;

}
