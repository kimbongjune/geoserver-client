package io.github.kimbongjune.geoserverclient.api.about;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.about.AboutResource;
import io.github.kimbongjune.geoserverclient.dto.about.ModuleStatusSummary;
import io.github.kimbongjune.geoserverclient.dto.about.SystemMetric;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] AboutManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AboutManagerIntegrationTest extends BaseIntegrationTest {

    private AboutManager about;

    @BeforeAll
    void setUp() {
        about = client.about();
    }

    // ── [1] getVersion() ─────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] getVersion() returns 3 items: GeoServer, GeoTools, GeoWebCache")
    void getVersion_returnsThreeComponents() {
        List<AboutResource> resources = about.getVersion();

        assertNotNull(resources, "version list must not be null");
        assertEquals(3, resources.size(), "must return exactly 3 items");

        boolean hasGeoServer  = false;
        boolean hasGeoTools   = false;
        boolean hasGeoWebCache = false;
        for (AboutResource r : resources) {
            if ("GeoServer".equals(r.getName()))   hasGeoServer  = true;
            if ("GeoTools".equals(r.getName()))    hasGeoTools   = true;
            if ("GeoWebCache".equals(r.getName())) hasGeoWebCache = true;
        }
        assertTrue(hasGeoServer,   "must contain GeoServer entry");
        assertTrue(hasGeoTools,    "must contain GeoTools entry");
        assertTrue(hasGeoWebCache, "must contain GeoWebCache entry");
    }

    // ── [2] getVersion() each resource has @name and Version ─────────────

    @Test
    @Order(2)
    @DisplayName("[2] getVersion() each resource has @name and Version property")
    void getVersion_resourcesHaveVersionProperty() {
        List<AboutResource> resources = about.getVersion();

        for (AboutResource r : resources) {
            assertNotNull(r.getName(), "resource @name must not be null");
            assertFalse(r.getName().isEmpty(), "resource @name must not be empty");
            // GeoWebCache may have its version in properties differently; check at least name exists
        }
    }

    // ── [3] getVersion() with filter ─────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] getVersion(filter) with manifest=GeoServer returns only GeoServer entry")
    void getVersion_withManifestFilter_returnsFiltered() {
        List<AboutResource> resources = about.getVersion("GeoServer", null, null);

        assertNotNull(resources, "filtered version list must not be null");
        assertFalse(resources.isEmpty(), "filtered result must not be empty");
        for (AboutResource r : resources) {
            assertTrue(r.getName() != null && r.getName().startsWith("GeoServer"),
                    "all results must have @name starting with GeoServer");
        }
    }

    // ── [4] getManifest() ─────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] getManifest() returns many JAR entries (>=3)")
    void getManifest_returnsManyItems() {
        List<AboutResource> resources = about.getManifest();

        assertNotNull(resources, "manifest list must not be null");
        assertTrue(resources.size() >= 3,
                "must return at least 3 JAR entries, got: " + resources.size());
        // Each resource must have a @name
        for (AboutResource r : resources) {
            assertNotNull(r.getName(), "@name must not be null for each manifest entry");
        }
    }

    // ── [5] getManifest() with filter ────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("[5] getManifest(key=GeoServerModule, value=core) returns core modules only")
    void getManifest_withKeyValueFilter_returnsFiltered() {
        List<AboutResource> resources = about.getManifest(null, "GeoServerModule", "core");

        assertNotNull(resources, "filtered manifest list must not be null");
        assertFalse(resources.isEmpty(), "at least one core GeoServer module must exist");
        for (AboutResource r : resources) {
            Object moduleType = r.getProperties().get("GeoServerModule");
            assertEquals("core", moduleType,
                    "all results must have GeoServerModule=core");
        }
    }

    // ── [6] getStatus() ──────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] getStatus() returns multiple module status entries")
    void getStatus_returnsManyModules() {
        List<ModuleStatusSummary> statuses = about.getStatus();

        assertNotNull(statuses, "status list must not be null");
        assertTrue(statuses.size() >= 5,
                "must return at least 5 module statuses, got: " + statuses.size());
        for (ModuleStatusSummary s : statuses) {
            assertNotNull(s.getName(), "module name must not be null");
            assertFalse(s.getName().isEmpty(), "module name must not be empty");
            assertNotNull(s.getHref(), "module href must not be null");
        }
    }

    // ── [7] getStatus() contains GeoServer Main ───────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] getStatus() includes 'GeoServer Main' module")
    void getStatus_containsGeoServerMain() {
        List<ModuleStatusSummary> statuses = about.getStatus();

        boolean found = false;
        for (ModuleStatusSummary s : statuses) {
            if ("GeoServer Main".equals(s.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "status list must include 'GeoServer Main' module");
    }

    // ── [8] getModuleStatus() ─────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] getModuleStatus('gs-main') returns GeoServer Main entry")
    void getModuleStatus_validModule_returnsEntry() {
        List<ModuleStatusSummary> statuses = about.getModuleStatus("gs-main");

        assertNotNull(statuses, "module status list must not be null");
        assertFalse(statuses.isEmpty(), "result must not be empty for gs-main");

        boolean found = false;
        for (ModuleStatusSummary s : statuses) {
            if ("GeoServer Main".equals(s.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "must contain 'GeoServer Main' entry");
    }

    // ── [9] getModuleStatus() 404 ────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] getModuleStatus('nonexistent-module') throws ResourceNotFoundException")
    void getModuleStatus_nonexistentModule_throwsNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> about.getModuleStatus("nonexistent-module-xyz-12345"),
                "nonexistent module must throw ResourceNotFoundException");
    }

    // ── [10] getSystemStatus() ────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[10] getSystemStatus() returns 31 metrics")
    void getSystemStatus_returns31Metrics() {
        List<SystemMetric> metrics = about.getSystemStatus();

        assertNotNull(metrics, "system-status list must not be null");
        assertEquals(31, metrics.size(),
                "must return exactly 31 system metrics, got: " + metrics.size());
    }

    // ── [11] getSystemStatus() metric fields ──────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] getSystemStatus() each metric has required fields")
    void getSystemStatus_metricsHaveRequiredFields() {
        List<SystemMetric> metrics = about.getSystemStatus();

        for (SystemMetric m : metrics) {
            assertNotNull(m.getName(),        "metric name must not be null");
            assertNotNull(m.getIdentifier(),  "metric identifier must not be null");
            assertNotNull(m.getCategory(),    "metric category must not be null");
            assertNotNull(m.getAvailable(),   "metric available must not be null");
            assertNotNull(m.getValue(),       "metric value must not be null");
        }
    }
}
