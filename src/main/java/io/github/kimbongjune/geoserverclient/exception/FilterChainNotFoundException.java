package io.github.kimbongjune.geoserverclient.exception;

/**
 * Thrown when a requested security filter chain does not exist in GeoServer.
 */
public class FilterChainNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public FilterChainNotFoundException(String chainName) {
        super("FilterChain", chainName, null);
    }

    public FilterChainNotFoundException(String chainName, String responseBody) {
        super("FilterChain", chainName, responseBody);
    }

    public String getChainName() {
        return getResourceName();
    }
}
