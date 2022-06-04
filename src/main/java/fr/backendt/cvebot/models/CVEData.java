package fr.backendt.cvebot.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.backendt.cvebot.deserializers.CVEDataDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = CVEDataDeserializer.class)
public class CVEData {

    private List<CVE> cveList = new ArrayList<>();
    private ZonedDateTime requestTime;

}
