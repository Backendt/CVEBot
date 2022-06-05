package fr.backendt.cvebot.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.backendt.cvebot.deserializers.CVEDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@JsonDeserialize(using = CVEDeserializer.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CVE {

    private String name = "CVE-????-?????";
    private String description = "No description available";

    private Severity severity = Severity.UNKNOWN;
    private double severityScore = 0;

    private Set<String> problemTypes = new HashSet<>();
    private String attackVector = "Unknown";

    public String getSeverityString() {
        return "%s (%s)".formatted(
                severity.toString(),
                severityScore
        );
    }

    public String getProblemsString() {
        if(problemTypes.isEmpty()) {
            return "None";
        }

        StringBuilder builder = new StringBuilder();
        String separator = ", ";

        problemTypes.forEach(problem -> builder
                .append(problem)
                .append(separator));

        int stringLength = builder.length();
        builder.delete(stringLength - separator.length(), stringLength - 1); // Deletes the last comma

        return builder.toString();
    }

}
