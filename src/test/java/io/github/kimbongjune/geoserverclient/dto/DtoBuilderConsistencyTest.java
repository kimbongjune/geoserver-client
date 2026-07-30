package io.github.kimbongjune.geoserverclient.dto;

import io.github.kimbongjune.geoserverclient.dto.coverage.CreateCoverageRequest;
import io.github.kimbongjune.geoserverclient.dto.coveragestore.CreateCoverageStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.datastore.CreateDataStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.featuretype.CreateFeatureTypeRequest;
import io.github.kimbongjune.geoserverclient.dto.namespace.CreateNamespaceRequest;
import io.github.kimbongjune.geoserverclient.dto.wmslayer.PublishWmsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmsstore.CreateWmsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtslayer.PublishWmtsLayerRequest;
import io.github.kimbongjune.geoserverclient.dto.wmtsstore.CreateWmtsStoreRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * No live GeoServer required — pure DTO construction checks.
 * <p>
 * Every {@code UpdateXxxRequest} in this library already exposes {@code builder()...build()}.
 * The 10 {@code CreateXxxRequest}/{@code PublishXxxRequest} DTOs listed in CHANGELOG.md
 * ("Added (사용성 통일)") historically only exposed {@code of(...)} + mutable fluent chaining.
 * This test proves the newly added {@code builder(...)} entry points are real aliases of
 * {@code of(...)} — same object, same fluent chain, {@code build()} a no-op terminal — so every
 * Create/Update/Publish request DTO in the library now answers to the same
 * {@code builder(...)...build()} calling convention, without changing the pre-existing
 * {@code of(...)} behavior that other callers/tests already depend on.
 */
@DisplayName("[UnitTest] builder(...)...build() parity with of(...) across Create/Publish request DTOs")
class DtoBuilderConsistencyTest {

    @Test
    @DisplayName("CreateWorkspaceRequest.builder() == of(), build() is identity")
    void workspace() {
        CreateWorkspaceRequest viaOf = CreateWorkspaceRequest.of("ws").isolated(true);
        CreateWorkspaceRequest viaBuilder = CreateWorkspaceRequest.builder("ws").isolated(true);
        assertEquals(viaOf, viaBuilder.build());
        assertSame(viaBuilder, viaBuilder.build(), "build() must be a no-op identity call");
    }

    @Test
    @DisplayName("CreateNamespaceRequest.builder() == of()")
    void namespace() {
        assertEquals(
                CreateNamespaceRequest.of("acme", "http://acme.example.com").isolated(true),
                CreateNamespaceRequest.builder("acme", "http://acme.example.com").isolated(true).build());
    }

    @Test
    @DisplayName("CreateDataStoreRequest.builder() == of()")
    void dataStore() {
        assertEquals(
                CreateDataStoreRequest.of("ds").type("PostGIS").connectionParam("host", "localhost"),
                CreateDataStoreRequest.builder("ds").type("PostGIS").connectionParam("host", "localhost").build());
    }

    @Test
    @DisplayName("CreateCoverageStoreRequest.builder() == of()")
    void coverageStore() {
        assertEquals(
                CreateCoverageStoreRequest.of("cs").type("GeoTIFF"),
                CreateCoverageStoreRequest.builder("cs").type("GeoTIFF").build());
    }

    @Test
    @DisplayName("CreateCoverageRequest.builder() == of() (partial-definition mode)")
    void coverage() {
        assertEquals(
                CreateCoverageRequest.of("cov").srs("EPSG:4326"),
                CreateCoverageRequest.builder("cov").srs("EPSG:4326").build());
    }

    @Test
    @DisplayName("CreateFeatureTypeRequest.builder() == of()")
    void featureType() {
        assertEquals(
                CreateFeatureTypeRequest.of("ft").srs("EPSG:4326").attribute("the_geom", "Point"),
                CreateFeatureTypeRequest.builder("ft").srs("EPSG:4326").attribute("the_geom", "Point").build());
    }

    @Test
    @DisplayName("CreateWmsStoreRequest.builder() == of()")
    void wmsStore() {
        assertEquals(
                CreateWmsStoreRequest.of("wms", "http://x/wms?REQUEST=GetCapabilities").enabled(true),
                CreateWmsStoreRequest.builder("wms", "http://x/wms?REQUEST=GetCapabilities").enabled(true).build());
    }

    @Test
    @DisplayName("PublishWmsLayerRequest.builder() == of()")
    void wmsLayer() {
        assertEquals(
                PublishWmsLayerRequest.of("layer", "ws:native").title("t"),
                PublishWmsLayerRequest.builder("layer", "ws:native").title("t").build());
    }

    @Test
    @DisplayName("CreateWmtsStoreRequest.builder() == of()")
    void wmtsStore() {
        assertEquals(
                CreateWmtsStoreRequest.of("wmts", "http://x/wmts?REQUEST=GetCapabilities").enabled(true),
                CreateWmtsStoreRequest.builder("wmts", "http://x/wmts?REQUEST=GetCapabilities").enabled(true).build());
    }

    @Test
    @DisplayName("PublishWmtsLayerRequest.builder() == of()")
    void wmtsLayer() {
        assertEquals(
                PublishWmtsLayerRequest.of("layer", "ws:native").title("t"),
                PublishWmtsLayerRequest.builder("layer", "ws:native").title("t").build());
    }
}
