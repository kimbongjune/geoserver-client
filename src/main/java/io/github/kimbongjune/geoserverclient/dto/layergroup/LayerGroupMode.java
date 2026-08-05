package io.github.kimbongjune.geoserverclient.dto.layergroup;

/**
 * Layer group publication mode. Mirrors GeoServer's
 * {@code org.geoserver.catalog.LayerGroupInfo.Mode} server-side enum.
 *
 * <p>Confirmed against a live GeoServer 2.28.2 instance: these five values pass server-side enum
 * validation; any other value is rejected with HTTP 500
 * ("No enum constant org.geoserver.catalog.LayerGroupInfo.Mode.&lt;value&gt;").
 */
public enum LayerGroupMode {
    /** Default mode: layers are rendered in listed order as if requested individually. */
    SINGLE,
    /** Like {@link #NAMED}, but the group itself cannot be requested — only its children. */
    OPAQUE_CONTAINER,
    /** The group can be requested as a single named layer in GetCapabilities and GetMap. */
    NAMED,
    /** Like {@link #NAMED}, but child layers remain individually requestable too. */
    CONTAINER,
    /** Earth Observation mode: a single representative "root" layer plus queryable children. */
    EO
}
