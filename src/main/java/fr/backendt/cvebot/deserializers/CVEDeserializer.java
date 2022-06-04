package fr.backendt.cvebot.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.Severity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CVEDeserializer extends StdDeserializer<CVE> {

    public CVEDeserializer() {
        this(null);
    }

    public CVEDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CVE deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);

        // Impact
        JsonNode impactNode = rootNode.get("impact")
                .get("baseMetricV3")
                .get("cvssV3");

        String severityString = impactNode.get("baseSeverity").asText();
        Severity severity = Severity.valueOf(severityString);

        double severityScore = impactNode.get("baseScore").asDouble();

        String attackVector = impactNode.get("attackVector").asText();
        
        // CVE
        JsonNode cveNode = rootNode.get("cve");

        String name = cveNode.get("CVE_data_meta").get("ID").asText();
        String description = cveNode.get("description")
                .get("description_data")
                .get(0)
                .get("value")
                .asText();

        Set<String> problemTypes = getProblemTypes(cveNode);

        return new CVE(
                name,
                description,
                severity,
                severityScore,
                problemTypes,
                attackVector
        );
    }

    private Set<String> getProblemTypes(JsonNode cveNode) {
        Set<String> problemTypes = new HashSet<>();

        JsonNode problemsNode = cveNode.get("problemtype").get("problemtype_data");

        for(JsonNode problemTypeNode : problemsNode) {
            for(JsonNode descriptionNode : problemTypeNode.get("description")) {
                String description = descriptionNode.get("value").asText();
                problemTypes.add(description);
            }
        }

        return problemTypes;
    }

}
