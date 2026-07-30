package io.github.kimbongjune.geoserverclient.api.layergroup;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.common.StringMap;
import io.github.kimbongjune.geoserverclient.dto.layergroup.CreateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroup;
import io.github.kimbongjune.geoserverclient.dto.layergroup.LayerGroupSummary;
import io.github.kimbongjune.geoserverclient.dto.layergroup.UpdateLayerGroupRequest;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.LayerGroupNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] LayerGroupManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LayerGroupManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS          = System.currentTimeMillis();
    private static final String WS          = "lg_ws_" + TS;

    // CoverageStore names → layer names (GeoTIFF configure=first)
    private static final String CS1         = "lg_cs1_" + TS;
    private static final String CS2         = "lg_cs2_" + TS;

    // Layer group names
    private static final String GLOBAL_GRP  = "lg_global_" + TS;
    private static final String WS_GRP      = "lg_ws_grp_" + TS;
    private static final String DEL_GRP     = "lg_del_"    + TS;    // for delete test
    private static final String INTL_GRP    = "lg_intl_"   + TS;    // for internationalTitle/Abstract test
    private static final String META_GRP    = "lg_meta_"   + TS;    // for metadata test

    private static File BYTE_TIF;
    private LayerGroupManager layerGroups;

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing from src/test/resources");
        BYTE_TIF = new File(res.toURI());

        // Create workspace
        client.workspaces().create(CreateWorkspaceRequest.of(WS));

        // Upload byte.tif ×2 → 2 CoverageStores + 2 Coverages + 2 Layers
        client.coverageStores().uploadFile(WS, CS1, "file", "geotiff", BYTE_TIF, "first", null, null);
        client.coverageStores().uploadFile(WS, CS2, "file", "geotiff", BYTE_TIF, "first", null, null);

        // Create global group (uses "ws:name" format for layers)
        client.layerGroups().create(
                CreateLayerGroupRequest.builder(GLOBAL_GRP)
                        .layer(WS + ":" + CS1)
                        .layer(WS + ":" + CS2)
                        .styles(Arrays.asList("", ""))
                        .title("Global Test Group")
                        .build());

        // Create workspace group (uses simple name for layers)
        client.layerGroups().createByWorkspace(WS,
                CreateLayerGroupRequest.builder(WS_GRP)
                        .layer(CS1)
                        .layer(CS2)
                        .styles(Arrays.asList("", ""))
                        .title("WS Test Group")
                        .build());

        // Create a throwaway group for the delete test
        client.layerGroups().create(
                CreateLayerGroupRequest.builder(DEL_GRP)
                        .layer(WS + ":" + CS1)
                        .build());

        // Create groups for internationalTitle/Abstract and metadata tests
        client.layerGroups().create(
                CreateLayerGroupRequest.builder(INTL_GRP)
                        .layer(WS + ":" + CS1)
                        .build());

        client.layerGroups().create(
                CreateLayerGroupRequest.builder(META_GRP)
                        .layer(WS + ":" + CS1)
                        .build());

        layerGroups = client.layerGroups();
    }

    @AfterAll
    void cleanUp() {
        // Try to delete global groups first (some may already be deleted by tests)
        try { client.layerGroups().delete(GLOBAL_GRP); } catch (Exception ignored) {}
        try { client.layerGroups().delete(DEL_GRP); }    catch (Exception ignored) {}
        try { client.layerGroups().delete(INTL_GRP); }   catch (Exception ignored) {}
        try { client.layerGroups().delete(META_GRP); }   catch (Exception ignored) {}
        // Workspace delete with recurse=true removes workspace groups and layers
        try { client.workspaces().delete(WS, true); }    catch (Exception ignored) {}
    }

    // ── 1. list() ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() global list contains GLOBAL_GRP (NO_WORKSPACE groups only)")
    void list_global_containsGlobalGroup() {
        List<LayerGroupSummary> list = layerGroups.list();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> GLOBAL_GRP.equals(s.getName())),
                "Expected '" + GLOBAL_GRP + "' in global list: " + list);
        // Workspace group must NOT appear in global list
        assertFalse(list.stream().anyMatch(s -> WS_GRP.equals(s.getName())),
                "WS_GRP should NOT appear in global list: " + list);
        list.forEach(s -> {
            assertNotNull(s.getName());
            assertNotNull(s.getHref());
        });
    }

    // ── 2. listByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] listByWorkspace(ws) contains WS_GRP")
    void listByWorkspace_containsWsGroup() {
        List<LayerGroupSummary> list = layerGroups.listByWorkspace(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> WS_GRP.equals(s.getName())),
                "Expected '" + WS_GRP + "' in workspace list: " + list);
        // Global group must NOT appear in workspace list
        assertFalse(list.stream().anyMatch(s -> GLOBAL_GRP.equals(s.getName())),
                "GLOBAL_GRP should NOT appear in workspace list: " + list);
    }

    // ── 3. get() - global path ─────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] get(globalGroupName) returns full LayerGroup with all fields")
    void get_globalGroup_returnsAllFields() {
        LayerGroup lg = layerGroups.get(GLOBAL_GRP);
        assertNotNull(lg);
        assertEquals(GLOBAL_GRP, lg.getName());
        assertNotNull(lg.getMode(), "mode always present");
        assertEquals("SINGLE", lg.getMode());
        assertEquals("Global Test Group", lg.getTitle());
        // enabled/advertised NOT present after initial POST (only after PUT) — GeoServer 2.28.2 confirmed
        assertNull(lg.getEnabled(), "enabled absent until after first PUT (similar to queryable in Layer)");
        assertNull(lg.getAdvertised(), "advertised absent until after first PUT");

        // publishables contains both layers
        assertNotNull(lg.getPublishables());
        List<LayerGroup.PublishedRef> published = lg.getPublishables().getPublished();
        assertFalse(published.isEmpty(), "publishables should not be empty");
        assertEquals(2, published.size());
        published.forEach(p -> {
            assertNotNull(p.getType());
            assertNotNull(p.getName());
        });

        // styles parallel to publishables
        assertNotNull(lg.getStyles());
        List<LayerGroup.StyleRef> styles = lg.getStyles().getStyle();
        assertEquals(2, styles.size(), "styles count should match publishables count");

        // bounds auto-computed
        assertNotNull(lg.getBounds());
        assertNotNull(lg.getBounds().getCrs());

        // dates
        assertNotNull(lg.getDateCreated());
        // no workspace field for global group
        assertNull(lg.getWorkspace());
    }

    // ── 4. getByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] getByWorkspace(ws, name) returns workspace field")
    void getByWorkspace_returnsWorkspaceField() {
        LayerGroup lg = layerGroups.getByWorkspace(WS, WS_GRP);
        assertNotNull(lg);
        assertEquals(WS_GRP, lg.getName());
        assertNotNull(lg.getWorkspace(), "workspace field present for workspace groups");
        assertEquals(WS, lg.getWorkspace().getName());
        assertEquals("WS Test Group", lg.getTitle());
        assertEquals(2, lg.getPublishables().getPublished().size());
    }

    // ── 5. exists() - global path ──────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("[5] exists(globalGroupName) returns true")
    void exists_globalGroup_returnsTrue() {
        assertTrue(layerGroups.exists(GLOBAL_GRP));
    }

    // ── 6. exists() - not found ────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("[6] exists(nonexistent) returns false")
    void exists_notFound_returnsFalse() {
        assertFalse(layerGroups.exists("nonexistent_group_xyz_" + TS));
    }

    // ── 7. existsByWorkspace() - true ─────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] existsByWorkspace(ws, wsGroupName) returns true")
    void existsByWorkspace_returnsTrue() {
        assertTrue(layerGroups.existsByWorkspace(WS, WS_GRP));
    }

    // ── 8. existsByWorkspace() - not found ────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] existsByWorkspace(ws, nonexistent) returns false")
    void existsByWorkspace_notFound_returnsFalse() {
        assertFalse(layerGroups.existsByWorkspace(WS, "nonexistent_group_xyz_" + TS));
    }

    // ── 9. get() - not found ───────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] get(nonexistent) throws LayerGroupNotFoundException")
    void get_notFound_throwsException() {
        assertThrows(LayerGroupNotFoundException.class,
                () -> layerGroups.get("nonexistent_group_xyz_" + TS));
    }

    // ── 10. update() ──────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[10] update(globalGroupName) changes title and mode")
    void update_globalGroup_changesFields() {
        LayerGroup updated = layerGroups.update(GLOBAL_GRP,
                UpdateLayerGroupRequest.builder()
                        .title("Updated Global Title")
                        .mode("OPAQUE_CONTAINER")
                        .abstractText("Updated abstract text")
                        .build());
        assertNotNull(updated);
        assertEquals("Updated Global Title", updated.getTitle());
        assertEquals("OPAQUE_CONTAINER", updated.getMode());
        assertEquals("Updated abstract text", updated.getAbstractText());
        assertNotNull(updated.getDateModified(), "dateModified set after PUT");
    }

    // ── 11. updateByWorkspace() ───────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] updateByWorkspace(ws, wsGroupName) changes title")
    void updateByWorkspace_changesTitle() {
        LayerGroup updated = layerGroups.updateByWorkspace(WS, WS_GRP,
                UpdateLayerGroupRequest.builder()
                        .title("Updated WS Group Title")
                        .enabled(false)
                        .build());
        assertNotNull(updated);
        assertEquals("Updated WS Group Title", updated.getTitle());
        assertEquals(Boolean.FALSE, updated.getEnabled());
    }

    // ── 12. verify update was persisted ───────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] get after updateByWorkspace — changes persisted")
    void get_afterUpdateByWorkspace_changesVisible() {
        LayerGroup lg = layerGroups.getByWorkspace(WS, WS_GRP);
        assertEquals("Updated WS Group Title", lg.getTitle());
        assertEquals(Boolean.FALSE, lg.getEnabled());
    }

    // ── 13. delete() ──────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("[13] delete(DEL_GRP) removes layer group; referenced layers remain")
    void delete_globalGroup_removesGroup() {
        assertTrue(layerGroups.exists(DEL_GRP), "DEL_GRP should exist before delete");
        layerGroups.delete(DEL_GRP);
        assertFalse(layerGroups.exists(DEL_GRP), "DEL_GRP should not exist after delete");
        // Referenced layer (CS1) should still exist
        assertTrue(client.layers().exists(WS + ":" + CS1),
                "Layer CS1 should still exist after layerGroup delete");
    }

    // ── 14. deleteByWorkspace() ───────────────────────────────────────

    @Test
    @Order(14)
    @DisplayName("[14] deleteByWorkspace(ws, WS_GRP) removes workspace group")
    void deleteByWorkspace_removesWsGroup() {
        assertTrue(layerGroups.existsByWorkspace(WS, WS_GRP), "WS_GRP should exist before delete");
        layerGroups.deleteByWorkspace(WS, WS_GRP);
        assertFalse(layerGroups.existsByWorkspace(WS, WS_GRP), "WS_GRP should not exist after delete");
        // Layer CS2 should still exist
        assertTrue(client.layers().existsByWorkspace(WS, CS2),
                "Layer CS2 should still exist after layerGroup deleteByWorkspace");
    }

    // ── 15. internationalTitle / internationalAbstract (StringMap) ─────────────

    @Test
    @Order(15)
    @DisplayName("[15] update() with internationalTitle/Abstract(StringMap) -> GET reflects values")
    void update_internationalTitleAbstract_reflected() {
        LayerGroup updated = layerGroups.update(INTL_GRP,
                UpdateLayerGroupRequest.builder()
                        .internationalTitle(new StringMap()
                                .put("en", "English Title")
                                .put("ko", "한국어 제목"))
                        .internationalAbstract(StringMap.of("en", "English Abstract"))
                        .build());

        assertNotNull(updated);
        assertNotNull(updated.getInternationalTitle(),
                "internationalTitle should be present in PUT response");
        assertEquals("English Title", updated.getInternationalTitle().get("en"),
                "internationalTitle 'en' should match");
        assertEquals("한국어 제목", updated.getInternationalTitle().get("ko"),
                "internationalTitle 'ko' should match");
        assertNotNull(updated.getInternationalAbstract(),
                "internationalAbstract should be present in PUT response");
        assertEquals("English Abstract", updated.getInternationalAbstract().get("en"),
                "internationalAbstract 'en' should match");

        // Verify persistence via GET
        LayerGroup fetched = layerGroups.get(INTL_GRP);
        assertNotNull(fetched.getInternationalTitle(), "internationalTitle persisted");
        assertEquals("English Title", fetched.getInternationalTitle().get("en"));
        assertEquals("한국어 제목", fetched.getInternationalTitle().get("ko"));
    }

    // ── 16. metadata (StringMap) ────────────────────────────────────────────────

    @Test
    @Order(16)
    @DisplayName("[16] update() with metadata(StringMap) -> GET returns metadata entry")
    void update_withMetadata_reflected() {
        layerGroups.update(META_GRP,
                UpdateLayerGroupRequest.builder()
                        .metadata(StringMap.of("cacheAgeMax", "3000")
                                .put("cachingEnabled", "true"))
                        .build());

        LayerGroup fetched = layerGroups.get(META_GRP);
        assertNotNull(fetched.getMetadata(), "metadata should be present in GET response");
        assertNotNull(fetched.getMetadata().getEntry(), "metadata.entry should be non-null");
    }
}
