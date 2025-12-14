package univ.lille.domain.port.out;
import univ.lille.domain.model.AccessLog;
import java.util.List;

public interface AccessLogRepository {
    AccessLog save(AccessLog log);
    List<AccessLog> findByOrganizationId(Long orgId);
    List<AccessLog> findByUserIdOrderByTimestampDesc(Long userId, int limit);

}
