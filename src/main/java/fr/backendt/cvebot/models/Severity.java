package fr.backendt.cvebot.models;

import java.awt.*;
import java.io.InputStream;

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

    public InputStream getImageFile() {
        return this.getClass().getClassLoader()
                .getResourceAsStream("images/%s.jpeg".formatted(
                        this.toString().toLowerCase()
                ));
    }
}
