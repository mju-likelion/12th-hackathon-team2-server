package com.example.mutsideout_mju.service;

import com.example.mutsideout_mju.authentication.JwtTokenProvider;
import com.example.mutsideout_mju.authentication.PasswordHashEncryption;
import com.example.mutsideout_mju.dto.request.auth.SignupDto;
import com.example.mutsideout_mju.entity.User;
import com.example.mutsideout_mju.exception.ConflictException;
import com.example.mutsideout_mju.exception.errorCode.ErrorCode;
import com.example.mutsideout_mju.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordHashEncryption passwordHashEncryption;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    public void signup(SignupDto signupDto) {

        // 중복 이름 회원가입 방지
        if (userRepository.findByName(signupDto.getName()).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATED_NAME);
        }

        // 중복 이메일 회원가입 방지
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATED_EMAIL);
        }

        // 비밀번호 암호화
        String plainPassword = signupDto.getPassword();
        String hashedPassword = passwordHashEncryption.encrypt(plainPassword);

        User newUser = User.builder()
                .email(signupDto.getEmail())
                .password(hashedPassword)
                .name(signupDto.getName())
                .build();
        userRepository.save(newUser);
    }
}
