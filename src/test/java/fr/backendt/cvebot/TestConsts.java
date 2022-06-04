package fr.backendt.cvebot;

import fr.backendt.cvebot.models.CVE;
import fr.backendt.cvebot.models.CVEData;
import fr.backendt.cvebot.models.Severity;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.time.ZoneOffset.UTC;

public class TestConsts {

    public static final CVE CVE_TEST = new CVE(
            "CVE-2022-30595",
            "libImaging/TgaRleDecode.c in Pillow 9.1.0 has a heap buffer overflow in the processing of invalid TGA image files.",
            Severity.CRITICAL,
            9.8d,
            Set.of("CWE-787"),
            "NETWORK"
    );

    public static final CVEData CVE_DATA_TEST = new CVEData(
            List.of(CVE_TEST, CVE_TEST),
            ZonedDateTime.of(
                    LocalDateTime.of(2022, 6, 3, 14, 31),
                    UTC
            )
    );

    public static String getJson(String fileName) throws IOException {
        String resourcePath = "json/%s.json".formatted(fileName);
        URL resource = TestConsts.class.getClassLoader().getResource(resourcePath);
        String fullPath = Objects.requireNonNull(resource).getPath();

        Path path = Path.of(fullPath);
        return Files.readString(path);
    }

}
