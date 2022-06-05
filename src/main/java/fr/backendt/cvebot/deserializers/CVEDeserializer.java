package fr.backendt.cvebot.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.Severity;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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

        CVE cve = new CVE();

        fillCveWithImpactNode(rootNode, cve);

        JsonNode cveNode = rootNode.get("cve");

        String name = cveNode.get("CVE_data_meta").get("ID").asText();
        cve.setName(name);

        String description = cveNode.get("description")
                .get("description_data")
                .get(0)
                .get("value")
                .asText();
        cve.setDescription(description);

        Set<String> problemTypes = getProblemTypes(cveNode);
        cve.setProblemTypes(problemTypes);

        return cve;
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

    private void fillCveWithImpactNode(JsonNode rootNode, CVE cve) {

        final List<String> nodeNames = List.of("impact", "baseMetricV3", "cvssV3");

        JsonNode impactNode = rootNode;
        for(String nodeName : nodeNames) {
            impactNode = impactNode.get(nodeName);
            if(impactNode == null || impactNode.isNull()) {
                return;
            }
        }

        JsonNode baseSeverityNode = impactNode.get("baseSeverity");
        if(baseSeverityNode != null && baseSeverityNode.isTextual()) {
            String severityString = baseSeverityNode.asText();
            Severity severity = Severity.valueOf(severityString);
            cve.setSeverity(severity);
        }

        JsonNode severityScoreNode = impactNode.get("baseScore");
        if(severityScoreNode != null && severityScoreNode.isDouble()) {
            double severityScore = severityScoreNode.asDouble();
            cve.setSeverityScore(severityScore);
        }

        JsonNode attackVectorNode = impactNode.get("attackVector");
        if(attackVectorNode != null && attackVectorNode.isTextual()) {
            String attackVector = attackVectorNode.asText();
            cve.setAttackVector(attackVector);
        }
    }

}
