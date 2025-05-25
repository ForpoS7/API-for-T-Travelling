package ru.itis.api.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.itis.api.dto.JwtTokenPairDto;
import ru.itis.api.util.JwtUtil;
import ru.itis.api.util.JsonUtil;

import java.io.IOException;

@Component("loginAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String phoneNumber = ((UserDetails) authentication.getPrincipal()).getUsername();
        JwtTokenPairDto tokenPair = jwtUtil.getTokenPair(phoneNumber);
        jwtUtil.updateRefreshToken(tokenPair.getRefreshToken(),phoneNumber);

        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JsonUtil.write(tokenPair));
    }
}
