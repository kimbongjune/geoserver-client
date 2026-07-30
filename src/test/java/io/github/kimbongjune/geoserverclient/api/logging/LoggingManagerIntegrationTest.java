package io.github.kimbongjune.geoserverclient.api.logging;

import io.github.kimbongjune.geoserverclient.BaseIntegrationTest;
import io.github.kimbongjune.geoserverclient.dto.logging.LoggingInfo;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[IntegrationTest] LoggingManager")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoggingManagerIntegrationTest extends BaseIntegrationTest {

    private LoggingManager logging;

    @BeforeAll
    void setUp() {
        logging = client.logging();
    }

    // ── [1] getLogging() ─────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("[1] getLogging() returns non-null with valid level and location")
    void getLogging_returnsValidInfo() {
        LoggingInfo info = logging.getLogging();

        assertNotNull(info, "LoggingInfo must not be null");
        assertNotNull(info.getLevel(), "level must not be null");
        assertFalse(info.getLevel().isEmpty(), "level must not be empty");
        assertNotNull(info.getLocation(), "location must not be null");
        assertNotNull(info.getStdOutLogging(), "stdOutLogging must not be null");
    }

    // ── [2] updateLogging() ───────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("[2] updateLogging() changes level and preserves stdOutLogging when included")
    void updateLogging_changesLevel() {
        LoggingInfo before = logging.getLogging();
        String originalLevel   = before.getLevel();
        String originalLocation = before.getLocation();
        Boolean originalStdOut  = before.getStdOutLogging();

        // switch to a different level (DEFAULT_LOGGING → VERBOSE_LOGGING, or vice versa)
        String newLevel = "DEFAULT_LOGGING".equals(originalLevel)
                ? "VERBOSE_LOGGING"
                : "DEFAULT_LOGGING";

        LoggingInfo patch = new LoggingInfo();
        patch.setLevel(newLevel);
        patch.setLocation(originalLocation);    // preserve existing location
        patch.setStdOutLogging(originalStdOut); // must be included — omitting it resets to false
        logging.updateLogging(patch);

        LoggingInfo after = logging.getLogging();
        assertEquals(newLevel, after.getLevel(), "level must be updated");
        assertEquals(originalStdOut, after.getStdOutLogging(),
                "stdOutLogging must be preserved when explicitly included");

        // restore original values
        LoggingInfo restore = new LoggingInfo();
        restore.setLevel(originalLevel);
        restore.setLocation(originalLocation);
        restore.setStdOutLogging(originalStdOut);
        logging.updateLogging(restore);

        LoggingInfo restored = logging.getLogging();
        assertEquals(originalLevel, restored.getLevel(), "level must be restored");
    }

    // ── [3] stdOutLogging reset warning verification ─────────────────────

    @Test
    @Order(3)
    @DisplayName("[3] updateLogging() without stdOutLogging resets it to false (server behavior)")
    void updateLogging_withoutStdOutLogging_resetsToFalse() {
        LoggingInfo before = logging.getLogging();
        String originalLevel    = before.getLevel();
        String originalLocation = before.getLocation();
        Boolean originalStdOut  = before.getStdOutLogging();

        // PUT without stdOutLogging → server overwrites it with false
        LoggingInfo patch = new LoggingInfo();
        patch.setLevel(originalLevel);
        patch.setLocation(originalLocation);
        // stdOutLogging intentionally omitted
        logging.updateLogging(patch);

        LoggingInfo after = logging.getLogging();
        assertFalse(Boolean.TRUE.equals(after.getStdOutLogging()),
                "stdOutLogging should be false after PUT without explicit value (server reset behavior)");

        // restore original values
        LoggingInfo restore = new LoggingInfo();
        restore.setLevel(originalLevel);
        restore.setLocation(originalLocation);
        restore.setStdOutLogging(originalStdOut);
        logging.updateLogging(restore);
    }
}
