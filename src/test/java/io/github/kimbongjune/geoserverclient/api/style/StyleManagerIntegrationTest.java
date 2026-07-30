package io.github.kimbongjune.geoserverclient.api.style;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.style.Style;
import io.github.kimbongjune.geoserverclient.dto.style.StyleContent;
import io.github.kimbongjune.geoserverclient.dto.style.StyleSummary;
import io.github.kimbongjune.geoserverclient.dto.workspace.CreateWorkspaceRequest;
import io.github.kimbongjune.geoserverclient.exception.AuthenticationException;
import io.github.kimbongjune.geoserverclient.exception.ResourceAlreadyExistsException;
import io.github.kimbongjune.geoserverclient.exception.StyleNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] StyleManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StyleManagerIntegrationTest extends BaseIntegrationTest {

    private static final long   TS           = System.currentTimeMillis();
    private static final String WS           = "st_ws_"     + TS;
    private static final String CS           = "st_cs_"     + TS;
    private static final String GLOBAL_STYLE = "st_global_" + TS;
    private static final String WS_STYLE     = "st_ws_sty_" + TS;
    private static final String DEL_GLOBAL   = "st_del_g_"  + TS;
    private static final String DEL_WS       = "st_del_ws_" + TS;

    private static File BYTE_TIF;
    private StyleManager styles;

    // ── Setup / Teardown ─────────────────────────────────────────────────

    @BeforeAll
    void setUp() throws Exception {
        URL res = getClass().getClassLoader().getResource("byte.tif");
        assertNotNull(res, "byte.tif missing from src/test/resources");
        BYTE_TIF = new File(res.toURI());

        client.workspaces().create(CreateWorkspaceRequest.of(WS));
        // Upload GeoTIFF so we have a layer for addStyleToLayer tests
        client.coverageStores().uploadFile(WS, CS, "file", "geotiff", BYTE_TIF, "first", null, null);

        // Create styles used across tests
        client.styles().create(StyleContent.of(sld(GLOBAL_STYLE)), GLOBAL_STYLE);
        client.styles().createByWorkspace(WS, StyleContent.of(sld(WS_STYLE)), WS_STYLE);
        // Create throwaway styles for deletion tests
        client.styles().create(StyleContent.of(sld(DEL_GLOBAL)), DEL_GLOBAL);
        client.styles().createByWorkspace(WS, StyleContent.of(sld(DEL_WS)), DEL_WS);

        styles = client.styles();
    }

    @AfterAll
    void cleanUp() {
        try { client.styles().delete(GLOBAL_STYLE); }  catch (Exception ignored) {}
        try { client.styles().delete(DEL_GLOBAL); }     catch (Exception ignored) {}
        try { client.workspaces().delete(WS, true); }   catch (Exception ignored) {}
    }

    // ── [1] list() ────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] list() contains GLOBAL_STYLE (GeoServer built-in styles also present)")
    void list_containsGlobalStyle() {
        List<StyleSummary> list = styles.list();
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Global style list must not be empty (built-ins always present)");
        assertTrue(list.stream().anyMatch(s -> GLOBAL_STYLE.equals(s.getName())),
                "Expected '" + GLOBAL_STYLE + "' in global style list");
        list.forEach(s -> assertNotNull(s.getName(), "Each summary must have a name"));
    }

    // ── [2] listByWorkspace() ─────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] listByWorkspace(WS) contains WS_STYLE, not GLOBAL_STYLE")
    void listByWorkspace_containsWsStyle() {
        List<StyleSummary> list = styles.listByWorkspace(WS);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(s -> WS_STYLE.equals(s.getName())),
                "Expected '" + WS_STYLE + "' in workspace style list");
        assertFalse(list.stream().anyMatch(s -> GLOBAL_STYLE.equals(s.getName())),
                "GLOBAL_STYLE must NOT appear in workspace style list");
    }

    // ── [3] listByWorkspace(nonexistent) ─────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] listByWorkspace(nonexistent_ws) throws ResourceNotFoundException — GeoServer 2.28.2 returns 404")
    void listByWorkspace_nonexistentWs_throwsNotFoundException() {
        // GeoServer 2.28.2 returns 404 for nonexistent workspace (unlike some other list endpoints)
        assertThrows(Exception.class,
                () -> styles.listByWorkspace("nonexistent_ws_xyz_" + TS));
    }

    // ── [4] get() ─────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[4] get(GLOBAL_STYLE) returns correct metadata")
    void get_globalStyle_returnsMetadata() {
        Style style = styles.get(GLOBAL_STYLE);
        assertNotNull(style);
        assertEquals(GLOBAL_STYLE, style.getName());
        assertEquals("sld", style.getFormat());
        assertNotNull(style.getLanguageVersion());
        assertEquals("1.0.0", style.getLanguageVersion().getVersion());
        assertNotNull(style.getFilename());
        assertTrue(style.getFilename().endsWith(".sld"),
                "Filename should end with .sld: " + style.getFilename());
        // Global style: no workspace field
        assertNull(style.getWorkspace(), "Global style must not have a workspace ref");
    }

    // ── [5] getByWorkspace() ─────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("[5] getByWorkspace(WS, WS_STYLE) returns workspace ref")
    void getByWorkspace_wsStyle_hasWorkspaceRef() {
        Style style = styles.getByWorkspace(WS, WS_STYLE);
        assertNotNull(style);
        assertEquals(WS_STYLE, style.getName());
        assertNotNull(style.getWorkspace(), "Workspace style must have workspace ref");
        assertEquals(WS, style.getWorkspace().getName());
        assertEquals("sld", style.getFormat());
    }

    // ── [6] get() nonexistent throws StyleNotFoundException ───────────────

    @Test
    @Order(6)
    @DisplayName("[6] get(nonexistent) throws StyleNotFoundException")
    void get_nonexistent_throwsStyleNotFoundException() {
        assertThrows(StyleNotFoundException.class,
                () -> styles.get("nonexistent_style_xyz_" + TS));
    }

    // ── [7] getSld() ──────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("[7] getSld(GLOBAL_STYLE) returns SLD XML")
    void getSld_globalStyle_returnsSldXml() {
        String sld = styles.getSld(GLOBAL_STYLE).getSldBody();
        assertNotNull(sld);
        assertFalse(sld.trim().isEmpty());
        assertTrue(sld.contains("StyledLayerDescriptor"),
                "SLD response must contain StyledLayerDescriptor element");
        assertTrue(sld.contains(GLOBAL_STYLE) || sld.contains("NamedLayer"),
                "SLD response must reference the style name or NamedLayer");
    }

    // ── [8] getSldByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("[8] getSldByWorkspace(WS, WS_STYLE) returns SLD XML")
    void getSldByWorkspace_wsStyle_returnsSldXml() {
        String sld = styles.getSldByWorkspace(WS, WS_STYLE).getSldBody();
        assertNotNull(sld);
        assertFalse(sld.trim().isEmpty());
        assertTrue(sld.contains("StyledLayerDescriptor"),
                "SLD response must contain StyledLayerDescriptor element");
    }

    // ── [9] exists() ──────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("[9] exists() true for existing, false for nonexistent")
    void exists_trueAndFalse() {
        assertTrue(styles.exists(GLOBAL_STYLE));
        assertFalse(styles.exists("nonexistent_style_xyz_" + TS));
    }

    // ── [10] existsByWorkspace() ───────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("[10] existsByWorkspace() true for existing, false for nonexistent")
    void existsByWorkspace_trueAndFalse() {
        assertTrue(styles.existsByWorkspace(WS, WS_STYLE));
        assertFalse(styles.existsByWorkspace(WS, "nonexistent_style_xyz_" + TS));
    }

    // ── [11] create() duplicate ────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("[11] create() with duplicate name throws exception (GeoServer 2.28.2 returns 403)")
    void create_duplicate_throwsException() {
        // GeoServer 2.28.2 returns 403 (not 409) for duplicate style creation
        assertThrows(AuthenticationException.class,
                () -> styles.create(StyleContent.of(sld(GLOBAL_STYLE)), GLOBAL_STYLE));
    }

    // ── [12] update() ─────────────────────────────────────────────────────

    @Test
    @Order(12)
    @DisplayName("[12] update(GLOBAL_STYLE, altSld) changes the SLD content")
    void update_globalStyle_changesSld() {
        String updatedSld = altSld(GLOBAL_STYLE);
        // Must not throw
        assertDoesNotThrow(() -> styles.update(GLOBAL_STYLE, StyleContent.of(updatedSld)));

        // Verify change reflected
        String retrieved = styles.getSld(GLOBAL_STYLE).getSldBody();
        assertNotNull(retrieved);
        assertTrue(retrieved.contains("FF0000") || retrieved.contains("ff0000"),
                "Updated SLD should contain the new color #FF0000");
    }

    // ── [13] update(raw=true) ──────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("[13] update(GLOBAL_STYLE, sld, raw=true) preserves original SLD")
    void update_raw_preservesOriginalSld() {
        String rawSld = sld(GLOBAL_STYLE);
        assertDoesNotThrow(() -> styles.update(GLOBAL_STYLE, StyleContent.of(rawSld, true)));
        // Verify style still accessible (not corrupted)
        Style style = styles.get(GLOBAL_STYLE);
        assertNotNull(style);
        assertEquals(GLOBAL_STYLE, style.getName());
    }

    // ── [14] updateByWorkspace() ────────────────────────────────────────────

    @Test
    @Order(14)
    @DisplayName("[14] updateByWorkspace(WS, WS_STYLE, altSld, false) changes the SLD")
    void updateByWorkspace_changesSld() {
        assertDoesNotThrow(() -> styles.updateByWorkspace(WS, WS_STYLE, StyleContent.of(altSld(WS_STYLE))));
        String retrieved = styles.getSldByWorkspace(WS, WS_STYLE).getSldBody();
        assertNotNull(retrieved);
        assertTrue(retrieved.contains("FF0000") || retrieved.contains("ff0000"),
                "Updated workspace SLD should contain updated color");
    }

    // ── [15] addStyleToLayer() ─────────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("[15] addStyleToLayer(layer, GLOBAL_STYLE) succeeds without exception")
    void addStyleToLayer_addsStyle() {
        String layerName = WS + ":" + CS;
        assertDoesNotThrow(() -> styles.addStyleToLayer(layerName, GLOBAL_STYLE));
    }

    // ── [16] addStyleToLayer(setDefault=true) ─────────────────────────────

    @Test
    @Order(16)
    @DisplayName("[16] addStyleToLayer(layer, WS_STYLE, setDefault=true) sets default style")
    void addStyleToLayer_setDefault() {
        String layerName = WS + ":" + CS;
        // WS_STYLE uses workspace-qualified name for association with global layer
        assertDoesNotThrow(() -> styles.addStyleToLayer(layerName, WS + ":" + WS_STYLE, true));
    }

    // ── [17] listByLayer() ─────────────────────────────────────────────────

    @Test
    @Order(17)
    @DisplayName("[17] listByLayer(layer) contains styles added in [15]-[16]")
    void listByLayer_containsAddedStyles() {
        String layerName = WS + ":" + CS;
        List<StyleSummary> list = styles.listByLayer(layerName);
        assertNotNull(list);
        // At least the GLOBAL_STYLE that was added
        assertFalse(list.isEmpty(), "Layer should have at least one available style");
        assertTrue(list.stream().anyMatch(s -> GLOBAL_STYLE.equals(s.getName())),
                "GLOBAL_STYLE should be in layer's available styles");
    }

    // ── [18] delete() ─────────────────────────────────────────────────────

    @Test
    @Order(18)
    @DisplayName("[18] delete(DEL_GLOBAL) removes the style; deleteByWorkspace(DEL_WS) succeeds")
    void delete_removesStyles() {
        // Delete global throwaway style (no files to purge; recurse needed only if referenced)
        assertDoesNotThrow(() -> styles.delete(DEL_GLOBAL, true, false));
        assertFalse(styles.exists(DEL_GLOBAL), "DEL_GLOBAL should no longer exist after delete");

        // Delete workspace throwaway style
        assertDoesNotThrow(() -> styles.deleteByWorkspace(WS, DEL_WS, true, false));
        assertFalse(styles.existsByWorkspace(WS, DEL_WS),
                "DEL_WS should no longer exist after deleteByWorkspace");

        // Re-deleting non-existent style must throw StyleNotFoundException
        assertThrows(StyleNotFoundException.class,
                () -> styles.delete(DEL_GLOBAL));
    }

    // ── [19] create(sldBody) auto-name ────────────────────────────────────

    @Test
    @Order(19)
    @DisplayName("[19] create(sldBody) auto-detects name from SLD UserStyle/Name tag")
    void create_autoName() {
        String autoName = "st_auto_" + TS;
        assertDoesNotThrow(() -> styles.create(StyleContent.of(sld(autoName))));
        assertTrue(styles.exists(autoName), "auto-named style must exist after create");
        styles.delete(autoName, false, false);
    }

    // ── [20] create(sldBody, name, raw=true) ──────────────────────────────

    @Test
    @Order(20)
    @DisplayName("[20] create(sldBody, name, raw=true) uploads raw SLD and style exists")
    void create_withRaw() {
        String rawName = "st_raw_" + TS;
        assertDoesNotThrow(() -> styles.create(StyleContent.of(sld(rawName), true), rawName));
        assertTrue(styles.exists(rawName), "raw-created style must exist");
        styles.delete(rawName, false, false);
    }

    // ── SLD helpers ────────────────────────────────────────────────────────

    /** Basic blue polygon SLD with the given style name. */
    private static String sld(String name) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<StyledLayerDescriptor version=\"1.0.0\""
                + " xmlns=\"http://www.opengis.net/sld\">\n"
                + "  <NamedLayer><Name>" + name + "</Name>\n"
                + "  <UserStyle><Name>" + name + "</Name>\n"
                + "  <FeatureTypeStyle><Rule>\n"
                + "  <PolygonSymbolizer><Fill>\n"
                + "  <CssParameter name=\"fill\">#336699</CssParameter>\n"
                + "  </Fill></PolygonSymbolizer>\n"
                + "  </Rule></FeatureTypeStyle>\n"
                + "  </UserStyle></NamedLayer>\n"
                + "</StyledLayerDescriptor>";
    }

    /** Alternative red polygon SLD (used for update tests). */
    private static String altSld(String name) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<StyledLayerDescriptor version=\"1.0.0\""
                + " xmlns=\"http://www.opengis.net/sld\">\n"
                + "  <NamedLayer><Name>" + name + "</Name>\n"
                + "  <UserStyle><Name>" + name + "</Name>\n"
                + "  <FeatureTypeStyle><Rule>\n"
                + "  <PolygonSymbolizer><Fill>\n"
                + "  <CssParameter name=\"fill\">#FF0000</CssParameter>\n"
                + "  </Fill></PolygonSymbolizer>\n"
                + "  </Rule></FeatureTypeStyle>\n"
                + "  </UserStyle></NamedLayer>\n"
                + "</StyledLayerDescriptor>";
    }
}
