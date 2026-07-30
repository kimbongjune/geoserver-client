package io.github.kimbongjune.geoserverclient.dto.gwc;

/** Update type for a GWC filter ({@code POST /gwc/rest/filter/{name}/update/{type}}). */
public enum GwcFilterUpdateType {
    /** XML update: triggers a WMS GetCapabilities reload. */
    XML("xml"),
    /** ZIP update: imports raster tiles from a zip file. Filename pattern: {@code {prefix}_{gridSetId}_{zoom}.{ext}} */
    ZIP("zip");

    private final String value;

    GwcFilterUpdateType(String value) { this.value = value; }

    public String getValue() { return value; }
}
