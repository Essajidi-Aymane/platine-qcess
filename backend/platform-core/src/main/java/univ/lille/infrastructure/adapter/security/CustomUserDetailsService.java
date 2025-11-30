package univ.lille.infrastructure.adapter.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import univ.lille.infrastructure.adapter.persistence.repository.UserJpaRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var userEntity = userJpaRepository.findByEmailWithOrganization(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Long orgId = userEntity.getOrganization() != null
                ? userEntity.getOrganization().getId()
                : null;

        String displayName;
        if (userEntity.getFirstName() != null && userEntity.getLastName() != null) {
            displayName = userEntity.getFirstName() + " " + userEntity.getLastName();
        } else if (userEntity.getFullName() != null) {
            displayName = userEntity.getFullName();
        } else {
            displayName = userEntity.getEmail();
        }

        return new QcessUserPrincipal(
                userEntity.getId(),
                orgId,
                userEntity.getEmail(),
                userEntity.getPassword() != null ? userEntity.getPassword() : "",
                displayName,
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())
                )
        );
    }
}
