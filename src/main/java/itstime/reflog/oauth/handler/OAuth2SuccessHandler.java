package itstime.reflog.oauth.handler;

import java.io.IOException;

import itstime.reflog.oauth.domain.UserPrincipal;
import itstime.reflog.oauth.token.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Generate JWT
        String jwtToken = jwtTokenProvider.createToken(userPrincipal.getMember().getId().toString());

        response.addHeader("Authorization", "Bearer " + jwtToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");
    }
}
