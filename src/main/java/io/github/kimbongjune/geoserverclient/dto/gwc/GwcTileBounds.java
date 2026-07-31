package io.github.kimbongjune.geoserverclient.dto.gwc;

import io.github.kimbongjune.geoserverclient.exception.SerializationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Response DTO for {@code GET /gwc/rest/bounds/{layer}/{srs}/java}.
 *
 * <p>The server returns plain text in Java double-brace array syntax:
 * <pre>{@code {{1, 0, 1, 0, 0}, {3, 1, 3, 1, 1}, ...}}</pre>
 * Each entry represents {@code {tileXMin, tileYMin, tileXMax, tileYMax, zoomLevel}}.
 * These are tile indices, not geographic coordinates.
 */
public class GwcTileBounds {

    private static final Pattern ENTRY = Pattern.compile("\\{\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*}");

    private final List<long[]> coverages;

    private GwcTileBounds(List<long[]> coverages) {
        this.coverages = coverages;
    }

    /**
     * Parses the server's plain-text {@code {{...}, {...}}} response.
     * @param rawText the raw response body
     * @return the parsed tile bounds
     */
    public static GwcTileBounds parse(String rawText) {
        if (rawText == null) {
            throw new SerializationException("GWC bounds response body was null", null);
        }
        List<long[]> result = new ArrayList<long[]>();
        Matcher m = ENTRY.matcher(rawText);
        while (m.find()) {
            long[] entry = new long[5];
            for (int i = 0; i < 5; i++) {
                entry[i] = Long.parseLong(m.group(i + 1));
            }
            result.add(entry);
        }
        return new GwcTileBounds(result);
    }

    /**
     * Returns the raw coverage arrays; each entry is {@code {tileXMin, tileYMin, tileXMax, tileYMax, zoomLevel}}.
     * @return the coverage arrays
     */
    public List<long[]> getCoverages() {
        return Collections.unmodifiableList(coverages);
    }

    /**
     * Returns the coverage entry for the given zoom level, or {@code null} if not present.
     * @param zoom the zoom level
     * @return the 5-element array, or {@code null}
     */
    public long[] forZoomLevel(int zoom) {
        for (long[] c : coverages) {
            if (c[4] == zoom) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcTileBounds that = (GwcTileBounds) o;
        if (coverages.size() != that.coverages.size()) {
            return false;
        }
        for (int i = 0; i < coverages.size(); i++) {
            if (!Arrays.equals(coverages.get(i), that.coverages.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (long[] coverage : coverages) {
            result = 31 * result + Arrays.hashCode(coverage);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GwcTileBounds{coverages=[");
        for (int i = 0; i < coverages.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(Arrays.toString(coverages.get(i)));
        }
        return sb.append("]}").toString();
    }
}
