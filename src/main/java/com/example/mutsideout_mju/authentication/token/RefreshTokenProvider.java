package com.example.mutsideout_mju.authentication.token;

import javax.crypto.SecretKey;

import com.example.mutsideout_mju.exception.UnauthorizedException;
import com.example.mutsideout_mju.exception.errorCode.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class RefreshTokenProvider {
    private final SecretKey key; // RefreshToken 시크릿 키
    private final long validityInMilliseconds; // RefreshToken 유효 시간

    public RefreshTokenProvider(@Value("${security.jwt.token.secret-refresh-key}") final String secretKey,
                                @Value("${security.jwt.token.refresh-expire-length}") final long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // RefreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, e.getMessage());
        }
    }
}
