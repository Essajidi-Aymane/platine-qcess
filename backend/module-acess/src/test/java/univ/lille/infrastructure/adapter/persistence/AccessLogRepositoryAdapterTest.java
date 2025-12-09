package univ.lille.infrastructure.adapter.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.domain.model.AccessLog;
import univ.lille.infrastructure.adapter.persistence.entity.AccessLogEntity;
import univ.lille.infrastructure.adapter.persistence.repository.AccessLogJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessLogRepositoryAdapterTest {

    @Mock
    private AccessLogJpaRepository jpaRepository;

    @InjectMocks
    private AccessLogRepositoryAdapter adapter;

    @Test
    void save_ShouldMapAndSave() {
        AccessLog log = AccessLog.builder()
                .userId(1L)
                .organizationId(2L)
                .timestamp(LocalDateTime.now())
                .accessGranted(true)
                .reason("OK")
                .build();

        AccessLogEntity savedEntity = new AccessLogEntity();
        savedEntity.setId(10L);
        savedEntity.setUserId(1L);
        savedEntity.setOrganizationId(2L);
        savedEntity.setTimestamp(log.getTimestamp());
        savedEntity.setAccessGranted(true);
        savedEntity.setReason("OK");

        when(jpaRepository.save(any(AccessLogEntity.class))).thenReturn(savedEntity);

        AccessLog result = adapter.save(log);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getUserId());
        verify(jpaRepository).save(any(AccessLogEntity.class));
    }

    @Test
    void findByOrganizationId_ShouldReturnList() {
        AccessLogEntity entity = new AccessLogEntity();
        entity.setId(1L);
        entity.setOrganizationId(100L);

        when(jpaRepository.findByOrganizationIdOrderByTimestampDesc(100L)).thenReturn(List.of(entity));

        List<AccessLog> result = adapter.findByOrganizationId(100L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(jpaRepository).findByOrganizationIdOrderByTimestampDesc(100L);
    }
}
