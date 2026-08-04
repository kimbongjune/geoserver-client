package io.github.kimbongjune.geoserverclient.api.importer;

import io.github.kimbongjune.geoserverclient.dto.importer.ImportData;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.InvalidParameterException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import io.github.kimbongjune.geoserverclient.http.GeoServerHttpClient;
import io.github.kimbongjune.geoserverclient.http.GeoServerResponse;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import io.github.kimbongjune.geoserverclient.serialization.SerializerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ImporterManager}'s data-source endpoints
 * ({@code getImportData}, {@code getTaskData}, {@code listImportDataFiles},
 * {@code listTaskDataFiles}, {@code getImportDataFile}, {@code deleteImportDataFile}), with the
 * HTTP layer mocked out.
 *
 * <p>Real-server behavior for these is covered in {@code ImporterManagerIntegrationTest} where
 * possible; {@code getImportDataFile}/{@code deleteImportDataFile} could not be exercised live
 * because no directory-type import could be produced with the available Shapefile ZIP fixture
 * (GeoServer treats a single Shapefile as {@code type="file"}, not {@code "directory"}) — these
 * mocked tests are the only coverage for their success/404 paths.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[UnitTest] ImporterManager - data endpoints")
class ImporterManagerTest {

    @Mock
    private GeoServerHttpClient httpClient;

    private ImporterManager manager;

    @BeforeEach
    void setUp() {
        manager = new ImporterManager(httpClient, new SerializerFactory(), DataFormat.JSON);
    }

    private static GeoServerResponse response(int status, String body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    private static GeoServerResponse binaryResponse(int status, byte[] body) {
        return new GeoServerResponse(status, body, Collections.<String, String>emptyMap());
    }

    // ── getImportData() ─────────────────────────────────────────────────

    @Test
    @DisplayName("getImportData() parses ImportData fields")
    void getImportData_success_parsesFields() {
        String body = "{\"type\":\"file\",\"format\":\"Shapefile\",\"file\":\"a.shp\"}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, body));
        ImportData data = manager.getImportData(1L);
        assertNotNull(data);
        assertEquals("file", data.getType());
        assertEquals("Shapefile", data.getFormat());
        assertEquals("a.shp", data.getFile());
    }

    @Test
    @DisplayName("getImportData() with 500 (GeoServer NPE bug) throws GeoServerResponseException")
    void getImportData_serverBug500_throwsGeoServerResponseException() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(500, "NullPointerException"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.getImportData(1L));
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    @DisplayName("getImportData() with 404 throws ResourceNotFoundException")
    void getImportData_notFound_throwsResourceNotFoundException() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class, () -> manager.getImportData(1L));
    }

    // ── getTaskData() ────────────────────────────────────────────────────

    @Test
    @DisplayName("getTaskData() parses ImportData fields including 'other' sidecar files")
    void getTaskData_success_parsesFields() {
        String body = "{\"type\":\"file\",\"format\":\"Shapefile\",\"file\":\"a.shp\","
                + "\"prj\":\"a.prj\",\"other\":[\"a.dbf\",\"a.shx\"]}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, body));
        ImportData data = manager.getTaskData(1L, 0L);
        assertNotNull(data);
        assertEquals("file", data.getType());
        assertEquals("Shapefile", data.getFormat());
        assertEquals("a.prj", data.getPrj());
        assertEquals(2, data.getOther().size());
    }

    @Test
    @DisplayName("getTaskData() with 404 throws ResourceNotFoundException")
    void getTaskData_notFound_throwsResourceNotFoundException() {
        when(httpClient.get(anyString(), anyString())).thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class, () -> manager.getTaskData(1L, 0L));
    }

    // ── listImportDataFiles() / listTaskDataFiles() ─────────────────────

    @Test
    @DisplayName("listImportDataFiles() with 400 'not a directory' throws GeoServerResponseException")
    void listImportDataFiles_notDirectory_throws400() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(400, "Data is not a directory"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.listImportDataFiles(1L));
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    @DisplayName("listTaskDataFiles() with 400 'not a directory' throws GeoServerResponseException")
    void listTaskDataFiles_notDirectory_throws400() {
        when(httpClient.get(anyString(), anyString()))
                .thenReturn(response(400, "Data is not a directory"));
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> manager.listTaskDataFiles(1L, 0L));
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    @DisplayName("listImportDataFiles() parses file name array when data is a directory")
    void listImportDataFiles_directory_parsesFileNames() {
        String body = "{\"files\":[\"a.shp\",\"a.dbf\"]}";
        when(httpClient.get(anyString(), anyString())).thenReturn(response(200, body));
        List<String> files = manager.listImportDataFiles(1L);
        assertEquals(2, files.size());
        assertTrue(files.contains("a.shp"));
        assertTrue(files.contains("a.dbf"));
    }

    // ── getImportDataFile() ──────────────────────────────────────────────

    @Test
    @DisplayName("getImportDataFile(null filename) throws InvalidParameterException")
    void getImportDataFile_nullFilename_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.getImportDataFile(1L, null));
    }

    @Test
    @DisplayName("getImportDataFile() returns raw bytes on 200")
    void getImportDataFile_success_returnsBytes() {
        byte[] content = "shapefile-bytes".getBytes();
        when(httpClient.getBinary(anyString(), anyString())).thenReturn(binaryResponse(200, content));
        byte[] result = manager.getImportDataFile(1L, "a.shp");
        assertArrayEquals(content, result);
    }

    @Test
    @DisplayName("getImportDataFile() with 404 throws ResourceNotFoundException")
    void getImportDataFile_notFound_throwsResourceNotFoundException() {
        when(httpClient.getBinary(anyString(), anyString()))
                .thenReturn(new GeoServerResponse(404, "not found", Collections.<String, String>emptyMap()));
        assertThrows(ResourceNotFoundException.class, () -> manager.getImportDataFile(1L, "missing.shp"));
    }

    // ── deleteImportDataFile() ───────────────────────────────────────────

    @Test
    @DisplayName("deleteImportDataFile(null filename) throws InvalidParameterException")
    void deleteImportDataFile_nullFilename_throwsInvalidParameter() {
        assertThrows(InvalidParameterException.class, () -> manager.deleteImportDataFile(1L, null));
    }

    @Test
    @DisplayName("deleteImportDataFile() success does not throw")
    void deleteImportDataFile_success_doesNotThrow() {
        when(httpClient.delete(anyString())).thenReturn(response(200, null));
        assertDoesNotThrow(() -> manager.deleteImportDataFile(1L, "a.shp"));
    }

    @Test
    @DisplayName("deleteImportDataFile() with 404 throws ResourceNotFoundException")
    void deleteImportDataFile_notFound_throwsResourceNotFoundException() {
        when(httpClient.delete(anyString())).thenReturn(response(404, "not found"));
        assertThrows(ResourceNotFoundException.class, () -> manager.deleteImportDataFile(1L, "missing.shp"));
    }
}
