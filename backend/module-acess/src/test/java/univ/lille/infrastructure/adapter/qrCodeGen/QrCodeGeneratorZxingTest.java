package univ.lille.infrastructure.adapter.qrCodeGen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QrCodeGeneratorZxingTest {

    private final QrCodeGeneratorZxing generator = new QrCodeGeneratorZxing();

    @Test
    void generatePng_ShouldReturnBytes() {
        byte[] result = generator.generatePng("test content", 100, 100);

        assertNotNull(result);
        assertTrue(result.length > 0);
        
   
        if (result.length >= 8) {
            assertEquals((byte) 0x89, result[0]); // -119
            assertEquals((byte) 0x50, result[1]); // 'P'
            assertEquals((byte) 0x4E, result[2]); // 'N'
            assertEquals((byte) 0x47, result[3]); // 'G'
        }
    }
}
