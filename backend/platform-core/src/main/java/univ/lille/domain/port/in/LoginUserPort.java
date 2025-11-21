package univ.lille.domain.port.in;

import univ.lille.dto.auth.AuthResponse;
import univ.lille.dto.auth.ForgotPasswordRequest;
import univ.lille.dto.auth.LoginRequest;
import univ.lille.dto.auth.ResetPasswordRequest;

public interface LoginUserPort {
    AuthResponse login (LoginRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);




}
