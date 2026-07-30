package io.github.kimbongjune.geoserverclient.api.gwc;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.gwc.GwcDiskQuotaConfig;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] GwcDiskQuotaManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GwcDiskQuotaManagerIntegrationTest extends BaseIntegrationTest {

    private GwcDiskQuotaManager diskQuota;
    private GwcDiskQuotaConfig original;

    @BeforeAll
    void setUp() {
        diskQuota = client.gwcDiskQuota();
        original = diskQuota.get();
    }

    @AfterAll
    void cleanUp() {
        try { diskQuota.update(restoreConfig()); } catch (Exception ignored) {}
    }

    private GwcDiskQuotaConfig restoreConfig() {
        GwcDiskQuotaConfig restore = new GwcDiskQuotaConfig();
        restore.setEnabled(original.getEnabled());
        restore.setCacheCleanUpFrequency(original.getCacheCleanUpFrequency());
        restore.setCacheCleanUpUnits(original.getCacheCleanUpUnits());
        restore.setMaxConcurrentCleanUps(original.getMaxConcurrentCleanUps());
        restore.setGlobalExpirationPolicyName(original.getGlobalExpirationPolicyName());
        return restore;
    }

    // ── [1] GET /gwc/rest/diskquota ──────────────────────────────────────

    @Test @Order(1)
    @DisplayName("[1] get() returns a populated GwcDiskQuotaConfig")
    void get_returnsConfig() {
        assertNotNull(original);
        assertNotNull(original.getEnabled());
    }

    @Test @Order(2)
    @DisplayName("[2] get() populates cacheCleanUpFrequency")
    void get_containsCacheCleanUpFrequency() {
        assertNotNull(original.getCacheCleanUpFrequency());
    }

    @Test @Order(3)
    @DisplayName("[3] get() populates globalExpirationPolicyName")
    void get_containsGlobalExpirationPolicyName() {
        assertNotNull(original.getGlobalExpirationPolicyName());
    }

    // ── [2] PUT /gwc/rest/diskquota ──────────────────────────────────────

    @Test @Order(4)
    @DisplayName("[4] update() with enabled=false is reflected on next get(), and omitted globalQuota is preserved")
    void update_disableDiskQuota_preservesOmittedFields() {
        GwcDiskQuotaConfig update = restoreConfig();
        update.setEnabled(false);
        assertDoesNotThrow(() -> diskQuota.update(update));

        GwcDiskQuotaConfig after = diskQuota.get();
        assertEquals(Boolean.FALSE, after.getEnabled());
        assertNotNull(after.getGlobalQuota(), "globalQuota omitted from PUT body must be preserved by the server");
    }

    @Test @Order(5)
    @DisplayName("[5] update() restores enabled=true")
    void update_restoreEnabled_succeeds() {
        GwcDiskQuotaConfig update = restoreConfig();
        update.setEnabled(true);
        assertDoesNotThrow(() -> diskQuota.update(update));

        GwcDiskQuotaConfig after = diskQuota.get();
        assertEquals(Boolean.TRUE, after.getEnabled());
    }
}
