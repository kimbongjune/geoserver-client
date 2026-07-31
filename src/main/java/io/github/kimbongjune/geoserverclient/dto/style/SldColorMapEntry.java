package io.github.kimbongjune.geoserverclient.dto.style;

/** A single entry in a {@code <ColorMap>} for {@link SldBuilder} raster symbolizers. */
public final class SldColorMapEntry {

    private final String color;
    private final double quantity;
    private final String label;
    private final double opacity;

    private SldColorMapEntry(String color, double quantity, String label, double opacity) {
        this.color    = color;
        this.quantity = quantity;
        this.label    = label;
        this.opacity  = opacity;
    }

    public static SldColorMapEntry of(String color, double quantity) {
        return new SldColorMapEntry(color, quantity, null, 1.0);
    }

    public static SldColorMapEntry of(String color, double quantity, String label) {
        return new SldColorMapEntry(color, quantity, label, 1.0);
    }

    public static SldColorMapEntry of(String color, double quantity, String label, double opacity) {
        return new SldColorMapEntry(color, quantity, label, opacity);
    }

    String getColor() {
        return color;
    }
    double getQuantity() {
        return quantity;
    }
    String getLabel() {
        return label;
    }
    double getOpacity() {
        return opacity;
    }
}
