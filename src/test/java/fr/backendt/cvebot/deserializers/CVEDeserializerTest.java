package fr.backendt.cvebot.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.backendt.cvebot.TestConsts;
import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class CVEDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void cveDeserializationTest() throws IOException {
        // GIVEN
        CVE expected = TestConsts.CVE_TEST;
        String json = TestConsts.getJson("cve_example");

        CVE result;

        // WHEN
        result = mapper.readValue(json, CVE.class);

        // THEN
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void cveDataDeserializationTest() throws IOException {
        // GIVEN
        CVEData expected = TestConsts.CVE_DATA_TEST;
        String json = TestConsts.getJson("response_example");

        CVEData result;

        // WHEN
        result = mapper.readValue(json, CVEData.class);

        // THEN
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void realCveDataDeserializationTest() throws IOException {
        // GIVEN
        String json = TestConsts.getJson("full_response");

        CVEData result;

        // WHEN
        result = mapper.readValue(json, CVEData.class);

        // THEN
        assertThat(result).isNotNull();
    }

}
