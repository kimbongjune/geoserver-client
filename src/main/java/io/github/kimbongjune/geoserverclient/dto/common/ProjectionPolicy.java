package io.github.kimbongjune.geoserverclient.dto.common;

/**
 * How GeoServer reconciles a resource's native CRS with its declared CRS.
 * Mirrors GeoServer's {@code org.geoserver.catalog.ProjectionPolicy} server-side enum — used by
 * FeatureType, Coverage, WMS layer, and WMTS layer create/update/response DTOs.
 *
 * <p>Confirmed against a live GeoServer 2.28.2 instance: {@code KEEP_NATIVE} and any other value
 * are rejected with HTTP 500 ("No enum constant org.geoserver.catalog.ProjectionPolicy.&lt;value&gt;")
 * — these three are the complete, closed set.
 */
public enum ProjectionPolicy {
    /** Use the declared CRS as-is, without reprojecting or verifying it against native data. */
    FORCE_DECLARED,
    /** Reproject from the native CRS to the declared CRS. */
    REPROJECT_TO_DECLARED,
    /** Use the native CRS; no declared CRS reconciliation is performed. */
    NONE
}
