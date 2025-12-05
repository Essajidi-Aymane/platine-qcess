package univ.lille.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import univ.lille.infrastructure.adapter.persistence.entity.BlackListedToken;
import univ.lille.infrastructure.adapter.persistence.repository.BlacklistedTokenRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @InjectMocks
    private LogoutUseCase logoutUseCase;

    @Test
    void logout_should_save_token_in_blacklist() {
        
        String token = "jwt-token-123";

        logoutUseCase.logout(token);

        ArgumentCaptor<BlackListedToken> captor = ArgumentCaptor.forClass(BlackListedToken.class);
        verify(blacklistedTokenRepository).save(captor.capture());

        BlackListedToken savedToken = captor.getValue();
        assert savedToken.getToken().equals(token);
    }
}
