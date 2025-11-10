package univ.lille.infrastructure.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import univ.lille.infrastructure.adapter.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.List;
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findByOrganization_Id(Long organizationId);

    @Query("SELECT u FROM UserEntity u WHERE u.customRole.name = :roleName")
    List<UserEntity> findByCustomRoleName(@Param("roleName") String roleName);

    List<UserEntity> findByCustomRole_Id(Long customRoleId);
}