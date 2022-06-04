package fr.backendt.cvebot.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CVEDataDeserializer extends StdDeserializer<CVEData> {

    public CVEDataDeserializer() {
        this(null);
    }

    public CVEDataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CVEData deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);
        JsonNode resultNode = rootNode.get("result");

        ObjectMapper mapper = new ObjectMapper();

        // CVEs
        List<CVE> cveList = new ArrayList<>();

        JsonNode cvesNode = resultNode.get("CVE_Items");
        for(JsonNode cveNode : cvesNode) {

            // Convert CVEs individually using CVEDeserializer
            CVE cve = mapper.convertValue(cveNode, CVE.class);
            cveList.add(cve);
        }

        // Request Time
        String timestamp = resultNode.get("CVE_data_timestamp").asText();
        ZonedDateTime requestTime = ZonedDateTime.parse(timestamp);

        return new CVEData(cveList, requestTime);
    }

}
