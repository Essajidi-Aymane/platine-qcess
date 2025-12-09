package univ.lille.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZoneQrCodeTest {

    @Test
    void testBuilderAndGetters() {
        byte[] imageBytes = new byte[]{1, 2, 3};
        ZoneQrCode qrCode = ZoneQrCode.builder()
                .id(1L)
                .zoneId(10L)
                .organizationId(20L)
                .content("QR_CONTENT")
                .image(imageBytes)
                .format("PNG")
                .build();

        assertEquals(1L, qrCode.getId());
        assertEquals(10L, qrCode.getZoneId());
        assertEquals(20L, qrCode.getOrganizationId());
        assertEquals("QR_CONTENT", qrCode.getContent());
        assertArrayEquals(imageBytes, qrCode.getImage());
        assertEquals("PNG", qrCode.getFormat());
    }

    @Test
    void testEqualsAndHashCode() {
        byte[] imageBytes = new byte[]{1, 2, 3};
        ZoneQrCode qr1 = ZoneQrCode.builder()
                .id(1L)
                .content("DATA")
                .image(imageBytes)
                .build();

        ZoneQrCode qr2 = ZoneQrCode.builder()
                .id(1L)
                .content("DATA")
                .image(imageBytes)
                .build();

        assertEquals(qr1, qr2);
        assertEquals(qr1.hashCode(), qr2.hashCode());
    }

    @Test
    void testToString() {
        ZoneQrCode qrCode = ZoneQrCode.builder()
                .id(1L)
                .content("TEST_CONTENT")
                .build();
        
        String stringRepresentation = qrCode.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("ZoneQrCode"));
        assertTrue(stringRepresentation.contains("id=1"));
        assertTrue(stringRepresentation.contains("content=TEST_CONTENT"));
    }
}
