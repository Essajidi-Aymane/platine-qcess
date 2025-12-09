package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.ZoneQrCode;
import univ.lille.domain.port.out.QrCodeGenerator;
import univ.lille.domain.port.out.ZoneQrCodeRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneQrCodeUseCaseTest {

    @Mock
    private ZoneQrCodeRepository qrCodeRepository;
    @Mock
    private QrCodeGenerator qrCodeGenerator;

    @InjectMocks
    private ZoneQrCodeUseCase zoneQrCodeUseCase;

    @Test
    void createForZone_ExistingQrCode_ReturnsExisting() {
        ZoneQrCode existingQr = ZoneQrCode.builder().id(1L).zoneId(10L).build();
        when(qrCodeRepository.existsByZoneId(10L)).thenReturn(true);
        when(qrCodeRepository.findByZoneId(10L)).thenReturn(Optional.of(existingQr));

        ZoneQrCode result = zoneQrCodeUseCase.createForZone(10L, 100L, "Zone A");

        assertEquals(existingQr, result);
        verify(qrCodeGenerator, never()).generatePng(anyString(), anyInt(), anyInt());
        verify(qrCodeRepository, never()).save(any());
    }

    @Test
    void createForZone_NewQrCode_GeneratesAndSaves() {
        when(qrCodeRepository.existsByZoneId(10L)).thenReturn(false);
        byte[] fakeImage = new byte[]{1, 2, 3};
        when(qrCodeGenerator.generatePng(anyString(), anyInt(), anyInt())).thenReturn(fakeImage);
        when(qrCodeRepository.save(any(ZoneQrCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ZoneQrCode result = zoneQrCodeUseCase.createForZone(10L, 100L, "Zone A");

        assertNotNull(result);
        assertEquals(10L, result.getZoneId());
        assertEquals(100L, result.getOrganizationId());
        assertArrayEquals(fakeImage, result.getImage());
        assertEquals("PNG", result.getFormat());
        
        verify(qrCodeGenerator).generatePng(contains("Zone A"), eq(300), eq(300));
        verify(qrCodeRepository).save(any(ZoneQrCode.class));
    }

    @Test
    void getByZoneId_Found_ReturnsQrCode() {
        ZoneQrCode qr = ZoneQrCode.builder().id(1L).zoneId(10L).organizationId(100L).build();
        when(qrCodeRepository.findByZoneIdAndOrganizationId(10L, 100L)).thenReturn(Optional.of(qr));

        ZoneQrCode result = zoneQrCodeUseCase.getByZoneId(10L, 100L);

        assertEquals(qr, result);
    }

    @Test
    void getByZoneId_NotFound_ThrowsRuntimeException() {
        when(qrCodeRepository.findByZoneIdAndOrganizationId(10L, 100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> zoneQrCodeUseCase.getByZoneId(10L, 100L));
        assertEquals("QR Code not found for zone 10", ex.getMessage());
    }
}
