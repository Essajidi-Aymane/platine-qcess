package univ.lille.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AccessLogTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        AccessLog log = AccessLog.builder()
                .id(1L)
                .userId(100L)
                .zoneId(200L)
                .organizationId(300L)
                .timestamp(now)
                .accessGranted(true)
                .reason("AUTHORIZED")
                .build();

        assertEquals(1L, log.getId());
        assertEquals(100L, log.getUserId());
        assertEquals(200L, log.getZoneId());
        assertEquals(300L, log.getOrganizationId());
        assertEquals(now, log.getTimestamp());
        assertTrue(log.isAccessGranted());
        assertEquals("AUTHORIZED", log.getReason());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        AccessLog log1 = AccessLog.builder()
                .id(1L)
                .userId(100L)
                .timestamp(now)
                .build();

        AccessLog log2 = AccessLog.builder()
                .id(1L)
                .userId(100L)
                .timestamp(now)
                .build();

        assertEquals(log1, log2);
        assertEquals(log1.hashCode(), log2.hashCode());
    }

    @Test
    void testToString() {
        AccessLog log = AccessLog.builder()
                .id(1L)
                .reason("TEST")
                .build();
        
        String stringRepresentation = log.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("AccessLog"));
        assertTrue(stringRepresentation.contains("id=1"));
        assertTrue(stringRepresentation.contains("reason=TEST"));
    }
}
