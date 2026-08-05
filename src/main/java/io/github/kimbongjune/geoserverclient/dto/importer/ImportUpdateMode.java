package io.github.kimbongjune.geoserverclient.dto.importer;

/**
 * How an import task reconciles incoming data with an existing target layer. Mirrors GeoServer
 * Importer's {@code org.geoserver.importer.UpdateMode} server-side enum.
 *
 * <p><b>Not independently re-verified against a live server in this pass:</b> this value set is
 * taken from GeoServer Importer's published source/documentation; only {@link ImportState#PENDING}
 * was directly observed via a live {@code POST /rest/imports} call in this round of verification.
 */
public enum ImportUpdateMode {
    /** Default: fail if the target layer already exists. */
    CREATE,
    /** Add new features to the existing target layer, keeping existing ones. */
    ADD,
    /** Alias for {@link #ADD} used by some GeoServer versions. */
    APPEND,
    /** Delete all existing features in the target layer, then add the imported ones. */
    REPLACE
}
