package io.github.kimbongjune.geoserverclient.dto.gwc;
import java.util.Objects;

/**
 * Result DTO for {@code POST /gwc/rest/reload}.
 *
 * <p>The GWC HTTP client returns a 200 response for both success and certain errors
 * ({@code reinitialize()} may return 200 even on failure). This DTO parses the raw
 * HTML response body to determine whether the reload actually succeeded.
 */
public class GwcReloadResult {

    private final boolean reloaded;
    private final Integer layerCount;
    private final String rawMessage;

    /**
     * Constructs a {@code GwcReloadResult}.
     * @param reloaded    {@code true} if the response body contained "reloaded"
     * @param layerCount  the count of reloaded layers, or {@code null} if unavailable
     * @param rawMessage  the raw HTML response body from the server
     */
    public GwcReloadResult(boolean reloaded, Integer layerCount, String rawMessage) {
        this.reloaded = reloaded;
        this.layerCount = layerCount;
        this.rawMessage = rawMessage;
    }

    /**
     * Returns {@code true} when the raw response body contains "reloaded".
     * @return {@code true} if reload succeeded
     */
    public boolean isReloaded() {
        return reloaded;
    }

    /**
     * Returns the count of reloaded layers, or {@code null} if unavailable.
     * @return the layer count
     */
    public Integer getLayerCount() {
        return layerCount;
    }

    /**
     * Returns the raw HTML response body from the server.
     * @return the raw message
     */
    public String getRawMessage() {
        return rawMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GwcReloadResult that = (GwcReloadResult) o;
        return Objects.equals(reloaded, that.reloaded)
                && Objects.equals(layerCount, that.layerCount)
                && Objects.equals(rawMessage, that.rawMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reloaded, layerCount, rawMessage);
    }

    @Override
    public String toString() {
        return "GwcReloadResult{" +
                "reloaded=" + reloaded +
                ", layerCount=" + layerCount +
                ", rawMessage=" + rawMessage +
                '}';
    }
}
