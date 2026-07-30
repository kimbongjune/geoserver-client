package io.github.kimbongjune.geoserverclient.api.monitoring;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequest;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitorRequestSummary;
import io.github.kimbongjune.geoserverclient.dto.monitoring.MonitoringQuery;
import io.github.kimbongjune.geoserverclient.exception.GeoServerResponseException;
import io.github.kimbongjune.geoserverclient.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link MonitoringManager}.
 *
 * <p>Verified against: GeoServer 2.28.2 / Docker http://localhost:9090/geoserver
 * Requires Monitor plugin (included in STABLE_EXTENSIONS).
 *
 * <p>Endpoints:
 * <pre>
 * [1] GET    /rest/monitor/requests        list (no query)
 * [2] GET    /rest/monitor/requests        list (MonitoringQuery)
 * [3] GET    /rest/monitor/requests/{id}   get single
 * [4] DELETE /rest/monitor/requests        delete all
 * </pre>
 */
@DisplayName("[IntegrationTest] MonitoringManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonitoringManagerIntegrationTest extends BaseIntegrationTest {

    private MonitoringManager monitoring;
    private static long firstRequestId = -1;

    @BeforeAll
    void setUp() {
        monitoring = client.monitoring();
    }

    // [1] list()

    @Test
    @Order(1)
    @DisplayName("[1] list() - returns list (items present when monitor plugin is active)")
    void list_shouldReturnList() {
        List<MonitorRequestSummary> list = monitoring.list();
        assertNotNull(list, "list() must not return null");
        list.forEach(item -> {
            assertTrue(item.getId() > 0, "id must be positive");
            assertNotNull(item.getHref(), "href must not be null");
        });
        if (!list.isEmpty()) {
            firstRequestId = list.get(0).getId();
        }
    }

    // [2] list(MonitoringQuery)

    @Test
    @Order(2)
    @DisplayName("[2] list(query) - count limit")
    void listWithQuery_count_shouldLimitResults() {
        List<MonitorRequestSummary> list = monitoring.list(new MonitoringQuery().count(3));
        assertNotNull(list);
        assertTrue(list.size() <= 3, "count=3 must return at most 3 items");
    }

    @Test
    @Order(3)
    @DisplayName("[2] list(query) - filter(status:EQ:FINISHED)")
    void listWithQuery_filter_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().filter("status:EQ:FINISHED").count(5));
        assertNotNull(list);
    }

    @Test
    @Order(4)
    @DisplayName("[2] list(query) - order(startTime;DESC)")
    void listWithQuery_order_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().order("startTime;DESC").count(5));
        assertNotNull(list);
    }

    @Test
    @Order(5)
    @DisplayName("[2] list(query) - live=false (completed requests only)")
    void listWithQuery_live_false_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(new MonitoringQuery().live(false));
        assertNotNull(list);
    }

    // [3] get(id)

    @Test
    @Order(6)
    @DisplayName("[3] get(id) - returns single request detail (first item)")
    void get_shouldReturnDetails() {
        // Re-query to get a fresh (most recent) ID — avoids eviction in full-suite runs
        // where the monitoring buffer may have rotated out the ID captured in Order 1.
        List<MonitorRequestSummary> current = monitoring.list();
        Assumptions.assumeTrue(!current.isEmpty(),
                "Skip: no monitor requests available to test single get");
        long id = current.get(current.size() - 1).getId();
        MonitorRequest req = monitoring.get(id);
        assertNotNull(req);
        assertEquals(id, req.getId());
        assertNotNull(req.getStatus(), "status must not be null");
        assertNotNull(req.getHttpMethod(), "httpMethod must not be null");
        assertNotNull(req.getPath(), "path must not be null");
    }

    @Test
    @Order(7)
    @DisplayName("[3] get(id) - nonexistent id → ResourceNotFoundException (404)")
    void get_nonExistent_shouldThrow() {
        assertThrows(ResourceNotFoundException.class,
                () -> monitoring.get(Long.MAX_VALUE));
    }

    // [4] deleteAll()

    @Test
    @Order(8)
    @DisplayName("[4] deleteAll() - list is empty after delete")
    void deleteAll_shouldClearRequests() {
        assertDoesNotThrow(() -> monitoring.deleteAll(), "deleteAll() must not throw");

        List<MonitorRequestSummary> afterDelete = monitoring.list();
        assertNotNull(afterDelete);
        assertTrue(afterDelete.isEmpty(), "After deleteAll(), list must be empty");
    }

    // empty query

    @Test
    @Order(9)
    @DisplayName("[edge] list(new MonitoringQuery()) - empty query is equivalent to list()")
    void listWithEmptyQuery_shouldBeEquivalentToNoParams() {
        // Monitoring records its own API calls, so 'b' may have 1-2 more items than 'a'
        // because the call that produced 'a' is recorded before 'b' is called.
        // Semantic validation: all items in 'a' must also appear in 'b' (by ID),
        // and 'b' must not have significantly more (at most +2 for self-recorded calls).
        List<MonitorRequestSummary> a = monitoring.list();
        List<MonitorRequestSummary> b = monitoring.list(new MonitoringQuery());
        assertNotNull(a);
        assertNotNull(b);
        // b >= a (b was called after a, so it sees a's call recorded too)
        assertTrue(b.size() >= a.size(),
                "list(empty query) must return at least as many items as list(), got a=" + a.size() + " b=" + b.size());
        // b should not have more than 2 extra items (one for a-call, one for b-call itself)
        assertTrue(b.size() <= a.size() + 2,
                "list(empty query) should not return more than 2 extra items vs list(), got a=" + a.size() + " b=" + b.size());
        // All items from 'a' must be present in 'b'
        a.forEach(item -> assertTrue(
                b.stream().anyMatch(bi -> bi.getId() == item.getId()),
                "Item id=" + item.getId() + " from list() missing in list(new MonitoringQuery())"));
    }

    // [2] from / to / offset / live=true

    @Test
    @Order(10)
    @DisplayName("[2] list(query) - from date filter (no-throw)")
    void listWithQuery_from_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().from("2026-01-01T00:00:00"));
        assertNotNull(list);
    }

    @Test
    @Order(11)
    @DisplayName("[2] list(query) - from+to date range (no-throw)")
    void listWithQuery_fromTo_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().from("2026-01-01T00:00:00").to("2099-12-31T23:59:59"));
        assertNotNull(list);
    }

    @Test
    @Order(12)
    @DisplayName("[2] list(query) - offset=0, count=5 (no-throw)")
    void listWithQuery_offset_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(new MonitoringQuery().offset(0).count(5));
        assertNotNull(list);
        assertTrue(list.size() <= 5, "count=5 must return at most 5 items");
    }

    @Test
    @Order(13)
    @DisplayName("[2] list(query) - live=true (active requests only, no-throw)")
    void listWithQuery_live_true_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(new MonitoringQuery().live(true));
        assertNotNull(list);
    }

    // [2] invalid filter → 400

    @Test
    @Order(14)
    @DisplayName("[2] list(query) - invalid filter format → GeoServerResponseException (400)")
    void listWithQuery_invalidFilter_shouldThrow400() {
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> monitoring.list(new MonitoringQuery().filter("INVALID_FILTER_FORMAT")));
        assertEquals(400, ex.getStatusCode(), "server must return 400 for invalid filter");
    }

    // [2] listCsv / listExcel / listZip

    @Test
    @Order(15)
    @DisplayName("[2] listCsv() - returns CSV (no-throw, not null)")
    void listCsv_shouldReturnCsv() {
        String csv = monitoring.listCsv();
        assertNotNull(csv, "CSV result must not be null");
    }

    @Test
    @Order(16)
    @DisplayName("[2] listExcel() - returns Excel binary (no-throw, not null)")
    void listExcel_shouldReturnBytes() {
        byte[] excel = monitoring.listExcel();
        assertNotNull(excel, "Excel result must not be null");
    }

    @Test
    @Order(17)
    @DisplayName("[2] listZip() - returns ZIP binary (no-throw, not null)")
    void listZip_shouldReturnBytes() {
        byte[] zip = monitoring.listZip();
        assertNotNull(zip, "ZIP result must not be null");
    }

    @Test
    @Order(19)
    @DisplayName("[2] listXml() - returns XML (no-throw, not null)")
    void listXml_shouldReturnXml() {
        String xml = monitoring.listXml();
        assertNotNull(xml, "XML result must not be null");
    }

    @Test
    @Order(20)
    @DisplayName("[2] list(query) - filter IN operator (status:IN:FINISHED,FAILED)")
    void listWithQuery_filterIn_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().filter("status:IN:FINISHED,FAILED").count(5));
        assertNotNull(list);
    }

    @Test
    @Order(21)
    @DisplayName("[2] list(query) - order(startTime;ASC)")
    void listWithQuery_orderAsc_shouldNotThrow() {
        List<MonitorRequestSummary> list = monitoring.list(
                new MonitoringQuery().order("startTime;ASC").count(5));
        assertNotNull(list);
    }

    // [4b] deleteById → 405

    @Test
    @Order(18)
    @DisplayName("[4b] deleteById(id) - delete specific id → GeoServerResponseException (405)")
    void deleteById_shouldThrow405() {
        GeoServerResponseException ex = assertThrows(GeoServerResponseException.class,
                () -> monitoring.deleteById(1L));
        assertEquals(405, ex.getStatusCode(), "server must return 405 for deleteById");
    }
}
