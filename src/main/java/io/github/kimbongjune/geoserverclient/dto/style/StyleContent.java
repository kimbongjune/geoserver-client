package io.github.kimbongjune.geoserverclient.dto.style;
import java.util.Objects;

/**
 * DTO for raw SLD style content. Used as the request body for
 * {@code POST/PUT /rest/styles[/{name}]} and as the response body for
 * {@code GET /rest/styles/{name}.sld}.
 *
 * <p>When {@link #isRaw()} is true, GeoServer returns/accepts the SLD without
 * validation or format conversion (see StyleManager for POST/PUT vs GET behavior).
 */
public class StyleContent {

    private final String sldBody;
    private final boolean raw;
    private final boolean sld11;

    private StyleContent(String sldBody, boolean raw, boolean sld11) {
        this.sldBody = sldBody;
        this.raw = raw;
        this.sld11 = sld11;
    }

    /** Creates an SLD 1.0 StyleContent (GeoServer will validate and convert). */
    public static StyleContent of(String sldBody) {
        return new StyleContent(sldBody, false, false);
    }

    public static StyleContent of(String sldBody, boolean raw) {
        return new StyleContent(sldBody, raw, false);
    }

    /** Creates an SLD 1.1 (SE) StyleContent. GeoServer uses {@code application/vnd.ogc.se+xml}. */
    public static StyleContent ofSld11(String sldBody) {
        return new StyleContent(sldBody, false, true);
    }

    public static StyleContent ofSld11(String sldBody, boolean raw) {
        return new StyleContent(sldBody, raw, true);
    }

    public String getSldBody() {
        return sldBody;
    }
    public boolean isRaw() {
        return raw;
    }
    /** Returns true if this is SLD 1.1 (SE) content; false for SLD 1.0. */
    public boolean isSld11() {
        return sld11;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StyleContent that = (StyleContent) o;
        return raw == that.raw && sld11 == that.sld11
                && Objects.equals(sldBody, that.sldBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sldBody, raw, sld11);
    }

    @Override
    public String toString() {
        return "StyleContent{sldBody=" + sldBody + ", raw=" + raw + ", sld11=" + sld11 + '}';
    }
}
