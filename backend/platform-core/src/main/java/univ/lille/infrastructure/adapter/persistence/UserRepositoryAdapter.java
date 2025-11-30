package univ.lille.infrastructure.adapter.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import univ.lille.domain.model.User;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.enums.UserRole;
import univ.lille.infrastructure.adapter.persistence.entity.UserEntity;
import univ.lille.infrastructure.adapter.persistence.mapper.UserEntityMapper;
import univ.lille.infrastructure.adapter.persistence.repository.UserJpaRepository;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper mapper;
    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return  mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailWithOrganization(email)
                .map(mapper::toDomain);
    }
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findByOrganizationId(Long organizationId) {
        return userJpaRepository.findByOrganization_Id(organizationId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findByOrganizationIdAndRole(Long organizationId, UserRole role) {
        return userJpaRepository.findByOrganization_IdAndRole(organizationId, role)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByResetPasswordToken(String token) {
        return userJpaRepository.findByResetPasswordToken(token)
                .map(mapper::toDomain);
    }

    /**
     * @param organizationId
     * @param customRoleId
     * @return
     */
    @Override
    public List<User> findByOrganizationIdAndCustomRoleId(Long organizationId, Long customRoleId) {
        return userJpaRepository.findByOrganizationIdAndCustomRole(organizationId, customRoleId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public void deleteUser(User user) {
        userJpaRepository.delete(mapper.toEntity(user));

    }

    /**
     * @param users
     */
    @Override
    public void saveAll(List<User> users) {
        List<UserEntity> entities = users.stream()
                .map(mapper::toEntity)
                .toList();
        userJpaRepository.saveAll(entities);

    }
}
