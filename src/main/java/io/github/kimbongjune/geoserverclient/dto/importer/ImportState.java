package io.github.kimbongjune.geoserverclient.dto.importer;

/**
 * Lifecycle state of an import or import task. Mirrors GeoServer Importer's
 * {@code org.geoserver.importer.ImportContext.State} / {@code ImportTask.State} server-side enums
 * (task-level state is a superset of context-level state).
 *
 * <p>{@link #PENDING} was directly confirmed via a live {@code POST /rest/imports} call against
 * GeoServer 2.28.2 in this round of verification. The remaining values are taken from GeoServer
 * Importer's published source — this codebase's pre-existing Javadoc already cited {@code READY}
 * and {@code COMPLETE}.
 */
public enum ImportState {
    /** The import was just created and has not been analyzed yet. */
    PENDING,
    /** Analyzed and ready to run. */
    READY,
    /** Currently executing. */
    RUNNING,
    /** Finished successfully. */
    COMPLETE,
    /** Finished with an error. */
    ERROR,
    /** Cancelled by the user. */
    CANCELED,
    /** Ready state blocked: the source data has no discernible CRS. */
    NO_CRS,
    /** Ready state blocked: the source data has no discernible bounding box. */
    NO_BOUNDS,
    /** Ready state blocked: the source data format could not be determined. */
    NO_FORMAT,
    /** Ready state blocked: the source data format was determined but is not supported. */
    BAD_FORMAT
}
