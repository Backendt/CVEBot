package fr.backendt.cvebot.models;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public enum Severity {
    UNKNOWN(Color.DARK_GRAY),
    LOW(Color.CYAN),
    MEDIUM(Color.ORANGE),
    HIGH(Color.RED),
    CRITICAL(Color.WHITE);

    private final Color color;
    Severity(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public File getImageFile() {
        URL url = this.getClass().getClassLoader()
                .getResource("images/%s.jpeg".formatted(
                        this.toString().toLowerCase()
                ));

        if(url != null) {
            try {
                return new File(url.toURI());
            } catch(URISyntaxException ignored) {
                return null;
            }
        }

        return null;
    }
}
