package com.example.mutsideout_mju.service;

import com.example.mutsideout_mju.authentication.token.AccessTokenProvider;
import com.example.mutsideout_mju.authentication.PasswordHashEncryption;
import com.example.mutsideout_mju.authentication.token.RefreshTokenProvider;
import com.example.mutsideout_mju.dto.request.auth.LoginDto;
import com.example.mutsideout_mju.dto.request.auth.SignupDto;
import com.example.mutsideout_mju.dto.response.token.TokenResponseDto;
import com.example.mutsideout_mju.entity.RefreshToken;
import com.example.mutsideout_mju.entity.User;
import com.example.mutsideout_mju.repository.RefreshTokenRepository;
import com.example.mutsideout_mju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordHashEncryption passwordHashEncryption;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenProvider accessTokenProvider;
    private final UserService userService;
    private final RefreshTokenProvider refreshTokenProvider;
    private final CookieService cookieService;

    /**
     * 회원가입
     */
    public TokenResponseDto signup(SignupDto signupDto) {

        // 중복 이름 회원가입 방지
        userService.validateIsDuplicatedName(signupDto.getName());

        // 중복 이메일 회원가입 방지
        userService.validateIsDuplicatedEmail(signupDto.getEmail());

        // 비밀번호 암호화
        String plainPassword = signupDto.getPassword();
        String hashedPassword = passwordHashEncryption.encrypt(plainPassword);

        User newUser = User.builder()
                .email(signupDto.getEmail())
                .password(hashedPassword)
                .name(signupDto.getName())
                .build();
        userRepository.save(newUser);

        return createToken(newUser);
    }

    /**
     * 로그인
     */
    public TokenResponseDto login(LoginDto loginDto) {
        // 유저 검증
        User user = userService.findExistingUserByEmail(loginDto.getEmail());

        // 비밀번호 검증
        userService.validateIsPasswordMatches(loginDto.getPassword(), user.getPassword());

        // 토큰 생성
        return createToken(user);
    }

    /**
     * accessToken, refreshToken 재발급
     */
    public TokenResponseDto refresh(String refreshToken) {
        RefreshToken storedRefreshToken = cookieService.findExistingRefreshToken(refreshToken);
        cookieService.validateRefreshToken(storedRefreshToken);
        User user = userService.findExistingUserByRefreshToken(storedRefreshToken);
        return createToken(user);
    }
    /**
     * accessToken 토큰 생성 및 refreshToken 저장
     */
    private TokenResponseDto createToken(User user) {
        String payload = String.valueOf(user.getId());
        String accessToken = accessTokenProvider.createToken(payload);
        // refreshToken 생성.
        String refreshTokenValue = refreshTokenProvider.createRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElse(new RefreshToken(user.getId(), refreshTokenValue));

        // refreshToken db에 저장.
        refreshToken.setToken(refreshTokenValue);
        refreshTokenRepository.save(refreshToken);

        return new TokenResponseDto(accessToken, refreshTokenValue);
    }
}
