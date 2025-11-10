package univ.lille.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import univ.lille.application.usecase.mapper.UserMapper;
import univ.lille.domain.exception.EmailAlreadyExistsException;
import univ.lille.domain.exception.OrganizationNotFoundException;
import univ.lille.domain.model.Organization;
import univ.lille.domain.model.User;
import univ.lille.domain.port.in.CreateUserPort;
import univ.lille.domain.port.out.EmailPort;
import univ.lille.domain.port.out.OrganizationRepository;
import univ.lille.domain.port.out.UserRepository;
import univ.lille.dto.auth.user.CreateUserRequest;
import univ.lille.dto.auth.user.UserDTO;
import univ.lille.enums.UserStatus;
import univ.lille.infrastructure.utils.CodeGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase  implements CreateUserPort {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final EmailPort emailPort;


    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequest createUserRequest, Long organisationId, Long adminId) {
        Organization org = organizationRepository.findById(organisationId) .orElseThrow(()->
                new OrganizationNotFoundException("Organization not found "));

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw  new EmailAlreadyExistsException(createUserRequest.getEmail());

        }

        String loginCode = CodeGenerator.generateLoginCode();
        User user = User.builder()
                .email(createUserRequest.getEmail())
                .firstName(createUserRequest.getFirstName())
                .lastName(createUserRequest.getLastName())
                .role(createUserRequest.getRole())
                .loginCode( loginCode)
                .userStatus(UserStatus.PENDING)
                .organization(org)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        emailPort.sendWelcomeEmail(user.getEmail(), user.getFullName(), loginCode);
        return UserMapper.toDTO(user);


    }
}
