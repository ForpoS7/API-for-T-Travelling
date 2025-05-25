package ru.itis.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ru.itis.api.dto.JwtTokenPairDto;
import ru.itis.api.entity.redis.RefreshToken;
import ru.itis.api.repository.TokenRepository;

import java.util.Date;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class JwtUtil {

    private final static String AUTH_HEADER = "Authorization";
    private final static String AUTH_HEADER_PREFIX = "Bearer ";

    private static final String PHONE_NUMBER_CLAIM = "phoneNumber";
    private static final String TYPE_CLAIM = "type";

    @Value("${jwt.access-token.ttl}")
    private Long accessTokenTtl;

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final TokenRepository tokenRepository;

    public JwtTokenPairDto getTokenPair(String phoneNumber) {
        return new JwtTokenPairDto(
                createAccessToken(phoneNumber),
                createRefreshToken(phoneNumber));
    }

    private String createAccessToken(String phoneNumber) {
        return JWT.create()
                .withExpiresAt(new Date(
                        new Date().getTime() + accessTokenTtl))
                .withClaim(PHONE_NUMBER_CLAIM, phoneNumber)
                .withClaim(TYPE_CLAIM, "access")
                .sign(algorithm);
    }

    private String createRefreshToken(String phoneNumber) {
        return JWT.create()
                .withExpiresAt(new Date(
                        new Date().getTime() + 1000 * 60 * 60 * 24 * 10))
                .withClaim(PHONE_NUMBER_CLAIM, phoneNumber)
                .withClaim(TYPE_CLAIM, "refresh")
                .sign(algorithm);
    }

    public String getRawToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);

        if (header != null && header.startsWith(AUTH_HEADER_PREFIX)) {
            return header.substring(AUTH_HEADER_PREFIX.length());
        }

        throw new BadCredentialsException("Token not found");
    }

    public String getPhoneNumber(String token) {
        return Optional.of(jwtVerifier.verify(token))
                .map(jwt -> jwt.getClaim(PHONE_NUMBER_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }

    public void updateRefreshToken(String token, String phoneNumber) {
        if(tokenRepository.findById(phoneNumber).isPresent()) {
            RefreshToken refreshToken = tokenRepository.findById(phoneNumber).get();
            refreshToken.setRawToken(token);
            tokenRepository.save(refreshToken);
        }
        else {
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setId(phoneNumber).setRawToken(token);
            tokenRepository.save(refreshToken);
        }
    }
}
